package com.cloudy.jpashop.service;

import com.cloudy.jpashop.domain.Member;
import com.cloudy.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// 순수한 단위 테스트를 만드는 게 아니라
// jpa 가 실제 DB 까지 엮어서 보는 거
// 그래서 완전히 스프링이랑 엮어서 보는 integration test

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional  // 테스트에서는 rollback
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("회원가입")
    public void signUp() {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long savedId = memberService.join(member);
//        em.flush();

        // then
        assertThat(memberRepository.findOne(savedId)).isEqualTo(member);
    }

    @Test
    @DisplayName("중복 회원 예외")
    public void mustThrowErrorDuplicatedMember() {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        Throwable e = assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2); // 예외가 발생해야 한다.
        });

        // then
        assertEquals("이미 존재하는 회원입니다.", e.getMessage());
    }
}