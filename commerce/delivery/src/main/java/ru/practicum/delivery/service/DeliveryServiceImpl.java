package ru.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.delivery.model.Delivery;
import ru.practicum.delivery.model.mapper.DeliveryMapper;
import ru.practicum.delivery.repository.AddressRepository;
import ru.practicum.delivery.repository.DeliveryRepository;
import ru.practicum.interaction.api.dto.request.DeliveryRequest;
import ru.practicum.interaction.api.dto.request.ShippedToDeliveryRequest;
import ru.practicum.interaction.api.enums.DeliveryState;
import ru.practicum.interaction.api.exception.NoDeliveryFoundException;
import ru.practicum.interaction.api.feign.OrderClient;
import ru.practicum.interaction.api.feign.WarehouseClient;

import java.math.BigDecimal;
import java.util.UUID;

import static ru.practicum.interaction.api.constants.BaseRateConstants.*;
import static ru.practicum.interaction.api.constants.WarehouseConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final DeliveryMapper deliveryMapper;

    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional
    public UUID planDelivery(DeliveryRequest request) {
        Delivery delivery = deliveryMapper.toEntity(request);

        addressRepository.findByRequest(request.fromAddress()).ifPresent(delivery::setFromAddress);
        addressRepository.findByRequest(request.toAddress()).ifPresent(delivery::setToAddress);

        return deliveryRepository.save(delivery).getDeliveryId();
    }

    @Override
    public BigDecimal deliveryCost(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("No delivery found with id %s".formatted(deliveryId)));

        log.debug("Calculating delivery cost for delivery {}, order {}",
                deliveryId, delivery.getOrderId());

        BigDecimal result = baseCostWithWarehouse(delivery)
                .multiply(fragileMultiplier(delivery))
                .add(weightCost(delivery))
                .add(volumeCost(delivery))
                .multiply(distanceMultiplier(delivery));

        log.debug("Delivery cost for order {}: {}", delivery.getOrderId(), result);

        return result;
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setState(DeliveryState.IN_PROGRESS);
        orderClient.assembled(orderId);
        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(orderId, delivery.getDeliveryId()));
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setState(DeliveryState.DELIVERED);
        orderClient.delivered(orderId);
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setState(DeliveryState.FAILED);
        orderClient.deliveryFailed(orderId);
    }

    @Override
    @Transactional
    public void deliveryCancelled(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setState(DeliveryState.CANCELLED);
    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("No delivery found for order %s".formatted(orderId)));
    }

    private BigDecimal baseCostWithWarehouse(Delivery delivery) {
        String city = delivery.getFromAddress().getCity();

        BigDecimal result = switch (city) {
            case WAREHOUSE_1_ADDRESS -> BASE_DELIVERY_COST.add(BASE_DELIVERY_COST.multiply(WAREHOUSE_1_RATE));
            case WAREHOUSE_2_ADDRESS -> BASE_DELIVERY_COST.add(BASE_DELIVERY_COST.multiply(WAREHOUSE_2_RATE));
            default -> throw new IllegalStateException("Unexpected warehouse: %s"
                    .formatted(city));
        };

        log.trace("Base cost with warehouse for order {}: city {}, result {}", delivery.getOrderId(), city, result);
        return result;
    }

    private BigDecimal fragileMultiplier(Delivery delivery) {
        BigDecimal result = Boolean.TRUE.equals(delivery.getFragile())
                ? FRAGILE_MULTIPLIER
                : BigDecimal.ONE;

        log.trace("Fragile multiplier for order {}: fragile {}, result {}",
                delivery.getOrderId(), delivery.getFragile(), result);
        return result;
    }

    private BigDecimal weightCost(Delivery delivery) {
        BigDecimal weight = delivery.getDeliveryWeight();
        BigDecimal result = weight.multiply(WEIGHT_RATE);

        log.trace("Weight cost for order {}: weight {}, rate {}, result {}",
                delivery.getOrderId(), weight, WEIGHT_RATE, result);
        return result;
    }

    private BigDecimal volumeCost(Delivery delivery) {
        BigDecimal volume = delivery.getDeliveryVolume();
        BigDecimal result = volume.multiply(VOLUME_RATE);

        log.trace("Volume cost for order {}: volume {}, rate {}, result {}",
                delivery.getOrderId(), volume, VOLUME_RATE, result);
        return result;
    }

    private BigDecimal distanceMultiplier(Delivery delivery) {
        String fromStreet = delivery.getFromAddress().getStreet();
        String toStreet = delivery.getToAddress().getStreet();
        boolean sameStreet = fromStreet.equals(toStreet);
        BigDecimal result = sameStreet ? BigDecimal.ONE : DISTANCE_MULTIPLIER;

        log.debug("Distance multiplier for order {}: from {}, to {}, same street is {}, result {}",
                delivery.getOrderId(), fromStreet, toStreet, sameStreet, result);
        return result;
    }
}
