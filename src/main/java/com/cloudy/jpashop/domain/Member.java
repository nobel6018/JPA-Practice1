package com.cloudy.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    // 문제
    // 1. Presentation 계층 검증을 위한 로직이 Entity 에 있다
    // 2. 속성명을 변경시켰을 때 (userName) api 스펙이 변경된다
    // 엔터티는 여러곳에서 사용되기 때문에 변경될 여지가 많다 여기에 의존하면 안된다
    // 그래서 DTO를 만들어야 한다
    // 회원가입 여러 로직을 받을 수가 없다 (소셜 로그인, 간편 로그인 등등)
    @NotEmpty
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
