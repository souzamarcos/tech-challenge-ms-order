package com.fiap.burger.controller.controller;

import com.fiap.burger.controller.adapter.api.OrderController;
import com.fiap.burger.entity.customer.Customer;
import com.fiap.burger.entity.notification.Notification;
import com.fiap.burger.entity.order.Order;
import com.fiap.burger.entity.order.OrderStatus;
import com.fiap.burger.messenger.adapter.NotificationMessenger;
import com.fiap.burger.messenger.adapter.OrderMessenger;
import com.fiap.burger.usecase.adapter.gateway.CustomerGateway;
import com.fiap.burger.usecase.adapter.gateway.OrderGateway;
import com.fiap.burger.usecase.adapter.gateway.ProductGateway;
import com.fiap.burger.usecase.adapter.usecase.OrderUseCase;
import com.fiap.burger.usecase.misc.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;

@Component
public class DefaultOrderController implements OrderController {
    @Autowired
    private OrderUseCase useCase;
    @Autowired
    OrderGateway orderGateway;
    @Autowired
    ProductGateway productGateway;
    @Autowired
    OrderMessenger orderMessenger;
    @Autowired
    CustomerGateway customerGateway;
    @Autowired
    NotificationMessenger notificationMessenger;

    @Override
    @Transactional
    public Order insert(Order order) {
        var persistedOrder = useCase.insert(order);
        orderMessenger.sendMessage(persistedOrder);
        return persistedOrder;
    }

    @Override
    public Order findById(Long orderId) {
        var persistedOrder = useCase.findById(orderId);
        if (persistedOrder == null) throw new OrderNotFoundException();
        var products = productGateway.findByIds(persistedOrder.getProductIds());
        persistedOrder.enrichItemsWithProducts(products);
        if (Optional.ofNullable(persistedOrder.getCustomer()).map(c -> Optional.ofNullable(c.getId()).isPresent()).orElse(false)) {
            var customer = customerGateway.findById(persistedOrder.getCustomer().getId());
            persistedOrder.setCustomer(customer);
        }
        return persistedOrder;
    }

    @Override
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        var modifiedOrder = useCase.updateStatus(orderId, newStatus);
        sendNotificationMessage(modifiedOrder);
        return modifiedOrder;
    }

    @Override
    public List<Order> findAllBy(OrderStatus status) {
        return useCase.findAllBy(status);
    }

    @Override
    public List<Order> findAllInProgress() {
        return useCase.findAllInProgress();
    }

    @Override
    public Order checkout(Long orderId) {
        var modifiedOrder = useCase.checkout(orderId);
        sendNotificationMessage(modifiedOrder);
        return modifiedOrder;
    }

    private void sendNotificationMessage(Order order) {
        if (Boolean.TRUE.equals(shouldNotify(order))) {
            notificationMessenger.sendMessage(new Notification(order));
        }
    }

    private Boolean shouldNotify(Order order) {
        return Optional.ofNullable(order.getCustomer()).map(Customer::getId).orElse(null) != null &&
            order.getStatus().asNotificationType() != null;
    }
}

