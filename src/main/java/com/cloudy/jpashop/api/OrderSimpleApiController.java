package com.cloudy.jpashop.api;

import com.cloudy.jpashop.domain.Order;
import com.cloudy.jpashop.repository.OrderRepository;
import com.cloudy.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
