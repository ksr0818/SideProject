package project.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByMemberId(Long memberId);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    //    Member findByUserName(String userName);
    Optional<Member> findUserByEmailAndProvider(String email, String provider);
}