package com.cloudy.jpashop.service;

import com.cloudy.jpashop.domain.Delivery;
import com.cloudy.jpashop.domain.Member;
import com.cloudy.jpashop.domain.Order;
import com.cloudy.jpashop.domain.OrderItem;
import com.cloudy.jpashop.domain.item.Item;
import com.cloudy.jpashop.repository.ItemRepository;
import com.cloudy.jpashop.repository.MemberRepository;
import com.cloudy.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // 엔터티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());;

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔터티 조회
        Order order = orderRepository.fineOne(orderId);
        // 주문 취소
        order.cancel();

        // dirty checking. 변경 내역 감지
        // 관련된 entity 변경들을 감지해서 자동으로 업데이트 친ㄷ

        // ?? @Transactional 걸어놔서 끝날 때 자동으로 commit -> flush 하나?
        // 따로 persist 한 게 없는데
        // --> @Transaction 으로 감싸져 있는 함수가 끝날 때
        // commit 이 동작하면서 flush 가 발생한다
        // flush가 발생하면서 변경감지 (dirty check)를 통해서
        // 변경된것을 체크해서 자동으로 넣어줌
    }

    // 검색
//    public List<Order> findOrdders(OrderSearch orderSearch) {
//    }
}
