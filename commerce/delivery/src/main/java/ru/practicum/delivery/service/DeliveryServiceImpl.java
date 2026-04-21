package ru.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DeliveryServiceImpl implements DeliveryService {
    private static final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);
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

        return baseCostWithWarehouse(delivery)
                .multiply(fragileMultiplier(delivery))
                .add(weightCost(delivery))
                .add(volumeCost(delivery))
                .multiply(distanceMultiplier(delivery));
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
        return switch (delivery.getFromAddress().getCity()) {
            case WAREHOUSE_1_ADDRESS -> BASE_DELIVERY_COST.add(BASE_DELIVERY_COST.multiply(WAREHOUSE_1_RATE));
            case WAREHOUSE_2_ADDRESS -> BASE_DELIVERY_COST.add(BASE_DELIVERY_COST.multiply(WAREHOUSE_2_RATE));
            default -> throw new IllegalStateException("Unexpected warehouse: %s"
                    .formatted(delivery.getFromAddress().getCity()));
        };
    }

    private BigDecimal fragileMultiplier(Delivery delivery) {
        return Boolean.TRUE.equals(delivery.getFragile())
                ? FRAGILE_MULTIPLIER
                : BigDecimal.ONE;
    }

    private BigDecimal weightCost(Delivery delivery) {
        return delivery.getDeliveryWeight().multiply(WEIGHT_RATE);
    }

    private BigDecimal volumeCost(Delivery delivery) {
        return delivery.getDeliveryVolume().multiply(VOLUME_RATE);
    }

    private BigDecimal distanceMultiplier(Delivery delivery) {
        return delivery.getToAddress().getStreet().equals(
                delivery.getFromAddress().getStreet())
                ? BigDecimal.ONE
                : DISTANCE_MULTIPLIER;
    }
}
