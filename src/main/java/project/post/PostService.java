package project.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.member.entity.Member;
import project.member.service.MemberService;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final PostMapper mapper;
    private final MemberService memberService;

    public Post savePost(Post post) {

        Member member = memberService.verifiedMember(post.getMember().getMemberId());

        post.setMember(member);

        return repository.save(post);
    }

}
