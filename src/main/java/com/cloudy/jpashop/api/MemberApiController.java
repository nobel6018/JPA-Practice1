package com.cloudy.jpashop.api;

import com.cloudy.jpashop.domain.Member;
import com.cloudy.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
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
