package com.cloudy.jpashop.service;

import com.cloudy.jpashop.domain.Address;
import com.cloudy.jpashop.domain.Member;
import com.cloudy.jpashop.domain.Order;
import com.cloudy.jpashop.domain.OrderStatus;
import com.cloudy.jpashop.domain.item.Book;
import com.cloudy.jpashop.exception.NotEnoughStockException;
import com.cloudy.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// 좋은 테스트는 DB, spring dependency 없이
// 순수하게 단위 테스트하는 게 좋은거다
// 여기에서는 JPA가 잘 돌아가는 지 보기 위해서
// integration test하는 것이다
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("상품주문")
    public void order() {
        // given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order order = orderRepository.fineOne(orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems().size()).isEqualTo(1);
        assertThat(order.getTotalPrice()).isEqualTo(10000 * orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }


    @Test
    @DisplayName("상품주문취소")
    public void cancelOrder() {
        // given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order cancelledOrder = orderRepository.fineOne(orderId);

        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("상품재고수량초과")
    public void checkStockOver() {
        // given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;
        // when
        Throwable e = assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), orderCount);
        });

        // then
        assertEquals("need more stock", e.getMessage());

    }

    private Book createBook(String name, int price, int stockQuality) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuality);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}