package com.cloudy.jpashop.repository.order.simplequery;

import com.cloudy.jpashop.domain.Address;
import com.cloudy.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

// DTO는 entity를 참조해도 괜찮다
// 연관관계는 단방향을 흘러야한다
// 또는 헥사곤 아키텍처로 해서 연관관계 interface로 다 발라내거나
@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
