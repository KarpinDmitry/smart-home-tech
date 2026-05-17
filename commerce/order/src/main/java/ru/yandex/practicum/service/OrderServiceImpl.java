package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.delivery.DeliveryClient;
import ru.yandex.practicum.api.payment.PaymentClient;
import ru.yandex.practicum.api.warehouse.WarehouseClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.dto.ProductReturnRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.OrderEntity;
import ru.yandex.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username must not be empty");
        }
        return orderRepository.findAllByUsername(username).stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        BookedProductsDto booked = warehouseClient.checkProductQuantityEnoughForShoppingCart(
                request.getShoppingCart()
        );

        OrderEntity order = new OrderEntity();
        order.setUsername(request.getUsername());
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setProducts(request.getShoppingCart().getProducts());
        order.setState(OrderState.NEW);
        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.isFragile());

        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        OrderEntity order = findOrder(request.getOrderId());
        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        PaymentDto paymentDto = paymentClient.payment(OrderMapper.toDto(order));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        BigDecimal total = paymentClient.getTotalCost(OrderMapper.toDto(order));
        order.setTotalPrice(total);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        BigDecimal deliveryCost = deliveryClient.deliveryCost(OrderMapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        OrderEntity order = findOrder(orderId);

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setFromAddress(warehouseAddress);
        deliveryDto.setOrderId(orderId);
        deliveryDto.setDeliveryState(DeliveryState.CREATED);

        AssemblyProductsForOrderRequest assemblyRequest = new AssemblyProductsForOrderRequest();
        assemblyRequest.setOrderId(orderId);
        assemblyRequest.setProducts(order.getProducts());
        BookedProductsDto booked = warehouseClient.assemblyProductsForOrder(assemblyRequest);

        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.isFragile());
        order.setState(OrderState.ASSEMBLED);

        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return OrderMapper.toDto(order);
    }

    private OrderEntity findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(orderId));
    }
}
