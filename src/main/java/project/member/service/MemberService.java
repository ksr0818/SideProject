package project.member.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.auth.utils.CustomAuthorityUtils;
import project.exception.BusinessLogicException;
import project.exception.ExceptionCode;
import project.member.entity.Member;
import project.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;

    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;
    private final JavaMailSender javaMailSender;

    public Member createMember(Member member) {

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        return repository.save(member);
    }

    public Member updateMember(Member member) {

        Member findMember = repository.findByMemberId(member.getMemberId());

        Optional.ofNullable(member.getUserName())
                .ifPresent(findMember::setUserName);
        Optional.ofNullable(member.getPassword())
                .ifPresent(findMember::setPassword);
        Optional.ofNullable(member.getNickname())
                .ifPresent(findMember::setNickname);
        Optional.ofNullable(member.getPhoneNumber())
                .ifPresent(findMember::setPhoneNumber);

//        변경된 비밀번호 암호화 해서 저장
        if (member.getPassword() != null) {
            String encryptedPassword = passwordEncoder.encode(findMember.getPassword());
            findMember.setPassword(encryptedPassword);
        }
            return repository.save(findMember);
    }

    public Member findMember(long memberId) {

//        long loginMemberId = getLoginMemberId();
//        if (loginMemberId != memberId) {
//            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
//        }

        Member findMember = repository.findByMemberId(memberId);

        return findMember;

    }

    public List<Member> findMembers() {
        return repository.findAll();
    }

    public void deleteMember(long memberId) {
//        long loginMemberId = getLoginMemberId();
//        if (loginMemberId != memberId) {
//            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
//        }

        repository.deleteById(memberId);
    }

    public Member verifiedMember(long memberId) {
        Optional<Member> optional = repository.findById(memberId);
        Member findId = optional.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return findId;
    }

    // Todo: 비밀번호 찾기 로직

//    public void sendVerificationCode(String email) {
//        // 사용자 정보 가져오기
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("입력한 이메일 주소가 존재하지 않습니다."));
//
//        // 인증번호 생성 및 저장
//        String verificationCode = generateVerificationCode();
//        member.setVerificationCode(verificationCode);
//        memberRepository.save(member);
//
//        // 이메일 발송
//        String appUrl = getAppUrl(); // 애플리케이션 URL 가져오기
//        String message = "인증번호: " + verificationCode + "\n" + appUrl;
//        sendEmail(email, "인증번호 발송", message);
//    }
//
//    public void verifyCode(String email, String verificationCode) {
//        // 사용자 정보 가져오기
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("입력한 이메일 주소가 존재하지 않습니다."));
//
//        // 인증번호 확인
//        String savedVerificationCode = member.getVerificationCode();
//        if (savedVerificationCode == null || !savedVerificationCode.equals(verificationCode)) {
//            throw new IllegalArgumentException("잘못된 인증번호입니다.");
//        }
//
//        // 인증번호 검증 완료
//        member.setVerificationCode(null);
//        memberRepository.save(member);
//    }
//
//    private void sendEmail(String email, String subject, String message) {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom("ksr940818@gmail.com");
//        mailMessage.setTo(email);
//        mailMessage.setSubject(subject);
//        mailMessage.setText(message);
//        javaMailSender.send(mailMessage);
//    }

    private String generateVerificationCode() {
        // 랜덤 숫자 문자열 생성
        return RandomStringUtils.randomNumeric(6);
    }

    private String getAppUrl() {
        // 애플리케이션 URL 반환
        return "http://localhost:8080";
    }

//    public long getLoginMemberId() {
//        String loginEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        long memberId = memberRepository.findByEmail(loginEmail).get().getMemberId();
//        return memberId;
//    }
}