package project.auth.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.auth.dto.LoginDto;
import project.auth.jwt.JwtTokenizer;
import project.member.entity.Member;
import project.member.repository.MemberRepository;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenizer jwtTokenizer,MemberRepository memberRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenizer = jwtTokenizer;
        this.memberRepository = memberRepository;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws ServletException, IOException {

        Member member = (Member) authResult.getPrincipal();
        String accessToken = delegateAccessToken(member);
        String refreshToken = delegateRefreshToken(member);

        Long memberId = member.getMemberId();

        Member member1 = memberRepository.findByMemberId(memberId);

        String userName = member.getUserName();
//        int profileImgNum = member1.getProfileImgNum();

        // Todo : response에 memberId 및 userName 추가! (login)

        String Authorization = "Bearer_" + accessToken;
        String refresh = "Bearer_" + refreshToken;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberId", memberId);
        jsonObject.put("userName", userName);
//        jsonObject.put("profileImgNum", profileImgNum);
        jsonObject.put("accessToken", Authorization);
        jsonObject.put("refreshToken", refresh);

        // 응답 헤더 설정
        response.setContentType("application/json");

        // 응답 데이터 전송
        response.getWriter().write(jsonObject.toString());
//
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);  // 추가

        Cookie cookie1 = new Cookie("Authorization", Authorization);
        Cookie cookie2 = new Cookie("Refresh", refresh);
        Cookie cookie3 = new Cookie("memberId", String.valueOf(memberId));

        cookie1.setHttpOnly(true);
        cookie2.setHttpOnly(true);
        cookie3.setHttpOnly(true);

        cookie1.setPath("/");
        cookie2.setPath("/");
        cookie3.setPath("/");

        cookie1.setDomain("mainmay.s3-website.ap-northeast-2.amazonaws.com");
        cookie2.setDomain("mainmay.s3-website.ap-northeast-2.amazonaws.com");
        cookie3.setDomain("mainmay.s3-website.ap-northeast-2.amazonaws.com");

        response.addCookie(cookie1);
        response.addCookie(cookie2);
        response.addCookie(cookie3);

    }

    public String delegateAccessToken(Member member) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("memberId", member.getMemberId());  // 식별자도 포함할 수 있다.
        claims.put("userName", member.getUserName());
        claims.put("email", member.getEmail());
        claims.put("roles", member.getRoles());

        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }

    public String delegateRefreshToken(Member member) {
        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        return refreshToken;
    }


}
