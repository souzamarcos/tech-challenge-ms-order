package com.fiap.burger.messenger.order;

import com.fiap.burger.entity.order.Order;
import com.fiap.burger.messenger.adapter.OrderMessenger;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("test")
@Primary
@Service
public class InMemoryOrderMessenger implements OrderMessenger {
    public void sendMessage(Order order) {
        System.out.println("alo, alo");
    }
}