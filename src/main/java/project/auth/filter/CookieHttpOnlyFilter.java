package project.auth.filter;//package server.mainproject.auth.filter;
//
//import org.springframework.core.annotation.Order;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//import javax.servlet.*;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//
//public class CookieHttpOnlyFilter extends OncePerRequestFilter {
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public CookieHttpOnlyFilter(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // 쿠키 생성 및 httpOnly 속성 설정
//        jwtAuthenticationFilter.get
//        Cookie cookie = new Cookie("Authentication", "token");
//        cookie.setHttpOnly(true);
//        response.addCookie(cookie);
//
//        filterChain.doFilter(request, response);
//    }
//}