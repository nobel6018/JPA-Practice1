package com.cloudy.jpashop.repository;

import com.cloudy.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order fineOne(Long id) {
        return em.find(Order.class, id);
    }

//    public List<Order> findAll(OrderSearch orderSearch) {
//        em.createQuery("select o from Order o join o.member m", Order.class);
//    }

    /**
     * JPA Criteria (..??) 동적쿼리 & 복잡한 쿼리는 queryDSL
     */
//    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Order> query = cb.createQuery(Order.class);
//        query.from( )
//    }
}
