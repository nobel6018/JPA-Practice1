package com.cloudy.jpashop.repository;

import com.cloudy.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
            "select o from Order o" +
                " join fetch o.member m" +   // left join fetch 걸면 outer join 할수도 있다
                " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    // distinct : sql의 distinct 맞다
    // sql에서의 distinct는 한 줄 전부가 unique 해야 중복이 제거된다
    // JPA에서는 distinct가 역할을 하나 더 한다
    // 애플리케이션에서 가지고 온 뒤에 root entity (여기에서는 Order)가 중복을 걸러서 collection에 담아준다

    // 일대다 fetch join에서는 paging을 하면 안된다
    // WARNING: firstResult/maxResults specified with collection fetch; applying in memory!
    // [이유는 sql 결과문에서 어떤걸 잘라야할 지 모른다. 강제로 잘랐다가 결과가 틀어지기 때문에 우선 결과를 애플리케이션에 퍼올린 뒤에 거기에서 paging 한다)
    // 일대일 or 다대일 관계에서는 fetch join + paging 해도 된다
    public List<Order> findAllWithItem() {
        return em.createQuery(
            "select distinct o from Order o" +
                " join fetch o.member m" +   // 
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +  // 일대다 관계라서 paging 하면 안된다
                " join fetch oi.item i", Order.class)
//            .setFirstResult(1)
//            .setMaxResults(100)
            .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
            "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
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
