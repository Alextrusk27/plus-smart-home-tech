package ru.practicum.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.interaction.api.dto.request.CreateNewOrderRequest;
import ru.practicum.interaction.api.dto.request.ProductReturnRequest;
import ru.practicum.interaction.api.dto.response.OrderDto;
import ru.practicum.interaction.api.exception.CartNotFoundException;
import ru.practicum.interaction.api.exception.OrderCreationFailedException;

import java.util.UUID;

public interface OrderService {

    Page<OrderDto> getOrders(String username, Pageable pageable);

    UUID getOrderIdByPayment(UUID paymentId);

    /**
     * Создаёт заказ с вызовом внешних сервисов и компенсацией при сбоях.
     * <p>
     * Порядок выполнения:
     * <ol>
     *   <li>Проверка соответствия корзины пользователя и заказа</li>
     *   <li>Резервирование товара на складе (списание)</li>
     *   <li>Сохранение заказа в базе данных</li>
     *   <li>Создание доставки в сервисе delivery</li>
     *   <li>Расчёт стоимости и создание платежа в сервисе payment</li>
     *   <li>Деактивация корзины пользователя</li>
     * </ol>
     * <p>
     * Если после резервирования товара происходит ошибка на любом этапе,
     * выполняется ручная компенсация в обратном порядке:
     * <ol>
     *   <li>Отмена платежа (перевод в статус CANCELLED)</li>
     *   <li>Отмена доставки (перевод в статус CANCELLED)</li>
     *   <li>Возврат товара на склад</li>
     * </ol>
     * Локальный заказ в БД откатывается автоматически благодаря {@code @Transactional}.
     *
     * @param username имя пользователя, создающего заказ
     * @param request  данные заказа (адрес доставки, содержимое корзины)
     * @return созданный заказ с идентификаторами доставки и платежа
     * @throws CartNotFoundException        если корзина не соответствует заказу
     * @throws OrderCreationFailedException при любой ошибке создания заказа
     */
    OrderDto createOrder(String username, CreateNewOrderRequest request);

    OrderDto returnProducts(ProductReturnRequest request);

    OrderDto payment(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto orderAssembled(UUID orderId);

    OrderDto orderAssemblyFailed(UUID orderId);

    OrderDto orderDelivered(UUID orderId);

    OrderDto orderDeliveryFailed(UUID orderId);

    OrderDto completed(UUID orderId);

    OrderDto canceled(UUID orderId);
}
