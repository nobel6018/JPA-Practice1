package com.cloudy.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    // Member.Orders를 변경하면 Order의 fk가 변경되지 않는다
    // 왜냐하면 거울이기 때문이다
    @OneToMany(mappedBy = "member")  // 읽기전용, member 필드에 의해서 거울이 되는 거다
    private List<Order> orders = new ArrayList<>();

    // 이런거 때문에 연관관계 편의 메서드를 만들어서
    // 한번에 원자화 시켜서 처리한다
//    public static void main(String[] args) {
//        Member member = new Member();
//        Order order = new Order();
//
//        member.getOrders().add(order);
//        order.setMember(member);
//    }
}
