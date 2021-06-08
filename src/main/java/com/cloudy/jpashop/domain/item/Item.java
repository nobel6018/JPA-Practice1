package com.cloudy.jpashop.domain.item;

import com.cloudy.jpashop.domain.Category;
import com.cloudy.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")  // Category.items 의 거울이다
    private List<Category> categories = new ArrayList<>();

    // 도메인 주도 설계
    // entity 자체가 해결할 수 있는 것들은
    // entity 안에 넣어서 문제를 해결하는 게 좋다 (객체지향적, 코드 응집)

    //==비즈니스 로직==//
    // 재고 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // 재고 감소
    public void removeStock(int quantity) {
        int leftStock = this.stockQuantity - quantity;
        if (leftStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = leftStock;
    }
}
