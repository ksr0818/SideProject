package project.auth.Oauth;

import lombok.Getter;
import project.member.entity.Member;

@Getter
public class UserProfile {

    private String username; // 사용자 이름
    private String provider; // 로그인한 서비스
    private String email; // 사용자의 이메일
    private String password;

    public void setUserName(String userName) {
        this.username = userName;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }

////     DTO 파일을 통하여 Entity를 생성하는 메소드
//    public OauthUser toEntity() {
//        return OauthUser.builder()
//                .username(this.username)
//                .email(this.email)
//                .provider(this.provider)
//                .build();
//    }


    public Member toEntity() {
        return Member.builder()
                .userName(this.username)
                .email(this.email)
                .provider(this.provider)
                .password(this.password)
                .build();
    }
}