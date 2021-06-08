package com.cloudy.jpashop.repository;

import com.cloudy.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        // jpa에 저장하기 전까지는 id값이 없다
        // 그래서 id값이 없다라는 건 새로 생성한 객체

        if (item.getId() == null) {
            // 새삥이 저장
            em.persist(item);
        } else {
            // 디비에 등록됐거나 가져온거라서
            // update와 비슷한거다
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
            .getResultList();
    }
}
