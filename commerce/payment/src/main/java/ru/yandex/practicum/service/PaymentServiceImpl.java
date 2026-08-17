package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.order.OrderClient;
import ru.yandex.practicum.api.shoppingStore.ShoppingStoreClient;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.enums.PaymentState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.model.PaymentEntity;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    public PaymentDto payment(OrderDto order) {
        BigDecimal productPrice = productCost(order);
        BigDecimal fee = productPrice.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal deliveryPrice = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO;
        BigDecimal total = productPrice.add(fee).add(deliveryPrice);

        PaymentEntity entity = new PaymentEntity();
        entity.setTotalPayment(total);
        entity.setDeliveryTotal(deliveryPrice);
        entity.setFeeTotal(fee);
        entity.setPaymentState(PaymentState.PENDING);
        paymentRepository.save(entity);

        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(entity.getPaymentId());
        dto.setTotalPayment(total);
        dto.setDeliveryTotal(deliveryPrice);
        dto.setFeeTotal(fee);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto order) {
        if (order.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Delivery price is missing in order");
        }
        BigDecimal productPrice = productCost(order);
        BigDecimal fee = productPrice.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        return productPrice.add(fee).add(order.getDeliveryPrice());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto order) {
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Products list is empty in order");
        }
        return order.getProducts().entrySet().stream()
                .map(entry -> {
                    var product = shoppingStoreClient.getProduct(entry.getKey());
                    return product.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException(paymentId));
        entity.setPaymentState(PaymentState.SUCCESS);
        orderClient.payment(entity.getPaymentId());
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException(paymentId));
        entity.setPaymentState(PaymentState.FAILED);
        orderClient.paymentFailed(entity.getPaymentId());
    }
}
