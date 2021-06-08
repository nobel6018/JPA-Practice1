package com.cloudy.jpashop.service;

import com.cloudy.jpashop.domain.Member;
import com.cloudy.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// *readOnly = true
// 영속성 컨텍스트를 flush 안해서 dirty check 안하는 이점
// JPA가 DB에 설정 해서 read only transaction거 알려주서
// driver가 해주는 것도 있음

// *Transactional
// JPA의 데이터변경, 로직은 Transaction 내에서 실행되되어야 한다
// lazy loading 등 가능하
@Service
@Transactional(readOnly = true)
// 생성자 자동으로 만들어준다
@RequiredArgsConstructor
public class MemberService {

    // Field injection은 변경하기가 힘들다
    // Setter injection은 초기하해 놓고나서 변경될 수 있어서 코드 신뢰성이 떨어진다
    // 생성자 injection을 사용한다
    private final MemberRepository memberRepository;

    // 회원가입
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
