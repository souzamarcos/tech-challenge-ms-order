package com.fiap.burger.listener.payment;

import com.fiap.burger.controller.adapter.api.OrderController;
import com.fiap.burger.entity.order.OrderPaymentStatus;
import com.fiap.burger.entity.order.OrderStatus;
import com.fiap.burger.usecase.misc.exception.PaymentMessageListenerException;
import com.fiap.burger.usecase.misc.profiles.NotTest;
import com.fiap.burger.usecase.misc.profiles.Production;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Production
@NotTest
@Component
public class DefaultPaymentMessageListener {
    @Autowired
    OrderController orderController;

    @SqsListener(value = "${cloud.aws.sqs.payment-queue}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    @Transactional
    public void paymentQueueListener(String message) {
        try {
            var dto = new Gson().fromJson(message, PaymentMessageListenerDto.class);
            if(dto.getOrderPaymentStatus().equals(OrderPaymentStatus.APROVADO)) {
                orderController.checkout(dto.getOrderId());
            } else if(dto.getOrderPaymentStatus().equals(OrderPaymentStatus.RECUSADO)) {
                orderController.updateStatus(dto.getOrderId(), OrderStatus.CANCELADO);
            }
        } catch (Exception ex) {
            throw new PaymentMessageListenerException(ex.getMessage());
        }
    }
}
