package com.cloudy.jpashop.api;

import com.cloudy.jpashop.domain.Address;
import com.cloudy.jpashop.domain.Order;
import com.cloudy.jpashop.domain.OrderStatus;
import com.cloudy.jpashop.repository.OrderRepository;
import com.cloudy.jpashop.repository.OrderSearch;
import com.cloudy.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import com.cloudy.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // Entity를 외부로 노출하면 여러 문제가 있다
    // 1. 양방향 연관관계의 경우 @JsonIgnore 해야한다 (Entity를 건드리는거니까 벌써부터 좋지 않다)
    // 2. LAZY 로딩을 피하기 위해서 EAGER로 바꾸는 것도 하면 안된다. Entity를 사용하는 다른곳에서 EAGER로 불러오는 문제가 생긴다
    // 3. EAGER로 바꾸는 것도 JPA에서 했을 때 id 값을 기준으로 join해서 가져와서 성능상 이점이 있는 것이지
    //    JPQL은 sql문으로 그대로 바꿔서 나가기 때문에 EAGER로 되어있는 것도 1+N 문제 그대로 있다
    // LAZY 로딩에서 proxy 객체 사용하는 개념을 알고 있어야한다 그래야 나중에 디버깅이나 깊이 있는 내부적인 생각을 할 수 있다 (JPA 기본 개념 강의에 설명)
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // LAZY 로딩 되어있는 것들 불러오기 위해서 조회
        for (Order order : all) {
            // order.getMember() 까지는 proxy 객체
            order.getMember().getName(); // lazy 강제 초기화
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
            .map(o -> new SimpleOrderDto(o))
            .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
            .map(o -> new SimpleOrderDto(o))
            .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
    // **
    // V3 vs V4 tradeoff
    // V4에서는 원하는 것만 select 해서 네트워크 쪽에서 필요한 것만 가지고 와서 퍼포먼스 좋으나 (네트워크 성능이 좋아서 생각보다 미비)
    // 재사용성이 떨어진다 vice versa
    // Repository 는 Entity의 객체 그래프를 조회하는 것이라 생각하므로
    // V4는 사실 api 스펙이 들어와있는 것이라 생각하므로 V3를 더 선호
    // 어드민 API나 한번씩 요청되는 것은 V3처럼 모든 객체 그래프를 조회해도 괜찮다
    // 자주 호출하면서 칼럼 갯수가 많으면 V4처럼 해야한다
    // 그래서 성능테스트를 해봐야한다

    // DTO가 Entity를 받는건 크게 문제되지 않는다
    // 중요하지 않은 것에서 중요한 것을 의존하는 것이기 때문이다
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;  // value object 라고 부른다 (JPA 강의 참고)

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();  // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();  // LAZY 초기화
        }
    }
}
