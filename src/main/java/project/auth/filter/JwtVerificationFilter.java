package project.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import project.auth.jwt.JwtTokenizer;
import project.auth.utils.CustomAuthorityUtils;
import project.auth.utils.JwtUtils;
import project.member.entity.Member;
import project.member.repository.MemberRepository;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomAuthorityUtils authorityUtils;
    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository;

    public JwtVerificationFilter(
            JwtUtils jwtUtils,
            CustomAuthorityUtils authorityUtils,
            JwtTokenizer jwtTokenizer,
            MemberRepository memberRepository
            ) {
        this.jwtUtils = jwtUtils;
        this.authorityUtils = authorityUtils;
        this.jwtTokenizer = jwtTokenizer;
        this.memberRepository = memberRepository;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("# JwtVerificationFilter");

        try {
            Map<String, Object> claims = jwtUtils.getJwsClaimsFromRequest(request);
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {

            request.setAttribute("exception", ee);
        } catch (Exception e) {
            ResponseEntity<String> refreshResponse = refreshAccessToken(request);
            if (refreshResponse.getStatusCode() == HttpStatus.OK && refreshResponse.getBody().equals("Access token refreshed")) {
                String accessToken = refreshResponse.getHeaders().getFirst("Authorization");
                if (accessToken != null && accessToken.startsWith("Bearer_")) {
                    accessToken = accessToken.substring(7); // "Bearer_" 접두사 제거
                    // Access Token을 사용하여 필요한 작업 수행
                    Map<String, Object> claims = jwtUtils.getJwsClaimsFromAccessToken(accessToken);
                    setAuthenticationToContext(claims);
                    response.setHeader("Authorization", "Bearer_" + accessToken); // 수정된 Access Token을 헤더에 설정
                } else {
                    // 올바른 Access Token을 가져오지 못한 경우에 대한 처리
                    request.setAttribute("exception", new RuntimeException("Invalid Access Token"));
                }
                Map<String, Object> claims = jwtUtils.getJwsClaimsFromAccessToken(accessToken);
                setAuthenticationToContext(claims);
//                response.setHeader("Authorization",accessToken);
            }
            else request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }





    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");
        String refresh = request.getHeader("Refresh");

        return (authorization == null || !authorization.startsWith("Bearer")) && (refresh == null || !refresh.startsWith("Authorization"));
    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        String email = (String) claims.get("email");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader("Refresh");
        if (refreshTokenHeader != null && refreshTokenHeader.startsWith("Bearer_")) {
            String refreshToken = refreshTokenHeader.substring(7);
            try {
                Jws<Claims> claims = jwtTokenizer.getClaims(refreshToken, jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()));

                String email = claims.getBody().getSubject();
                Optional<Member> optionalMember = memberRepository.findByEmail(email);

                if (optionalMember.isPresent()) {
                    Member member = optionalMember.get();
                    String accessToken = delegateAccessToken(member);

                    return ResponseEntity.ok().header("Authorization", "Bearer_" + accessToken).body("Access token refreshed");
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid member email");
                }
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }
    }

    private String delegateAccessToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", member.getEmail());
        claims.put("roles", member.getRoles());
        claims.put("userName", member.getUserName());

        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }



    //        try {
//            // 쿠키에서 AccessToken 추출
//            String accessToken = extractAccessTokenFromCookie(request);
//
//            // AccessToken 검증
//            if (accessToken != null) {
//                Map<String, Object> claims = jwtUtils.getJwsClaimsFromRequest(request);
//                setAuthenticationToContext(claims);
//            }
//        } catch (SignatureException se) {
//            request.setAttribute("exception", se);
//        } catch (ExpiredJwtException ee) {
//            request.setAttribute("exception", ee);
//        } catch (Exception e) {
//            request.setAttribute("exception", e);
//        }
//
//        filterChain.doFilter(request, response);
//    }

//    private String extractAccessTokenFromCookie(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("Authorization".equals(cookie.getName())) {
//                    return cookie.getValue().replace("Bearer_", "");
//                }
//            }
//        }
//        return null;
//    }

}
