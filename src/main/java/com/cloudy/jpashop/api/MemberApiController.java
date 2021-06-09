package com.cloudy.jpashop.api;

import com.cloudy.jpashop.domain.Member;
import com.cloudy.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

// @ResponseBody 는 데이터 자체를 json, xml로 보낸다라는 뜻이다
// @Controller @ResponseBody
@RestController  // (@Controller @ResponseBody)를 합친거
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // Entity를 변경해도 api 스펙이 변경되지 않는다
    // Entity를 봐서 어떤 파라미터를 넘겨야하지 않는 지 모른는 데 명확히 한다
    // "Entity를 노출하지 말고 API 스펙에 맞는 DTO 객체를 만들어라"
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(
        @RequestBody @Valid CreateMemberRequest request
    ) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // Response 객체는 공통으로 가져갈 수도 있는데 이거는 상황에 따라
    // 똑같이 가져갈수도 다르게 가ㅈ갈수도 있는 거 같음
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
        @PathVariable Long id,
        @RequestBody @Valid UpdateMemberRequest request
    ) {
        // 커맨드랑 쿼리를 철저하게 분리한다 라는 정책
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    // DTO에는 실용적인 관점에서 Lombok 많이 사용한다
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
