package project.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.audit.Auditable;
import project.member.entity.Member;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Post extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private String imgURL;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}
