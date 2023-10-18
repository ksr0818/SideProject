package project.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.Assert;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class MemberDto {
    @Getter
    @NoArgsConstructor
    public static class Post {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String checkPassword;
        @NotBlank
        private String userName;
        @NotBlank
        private String nickname;
        @NotBlank
        private String phoneNumber;

    }

    @Getter
    @NoArgsConstructor
    public static class Patch {
        private long memberId;
        private String password;
        @NotBlank
        private String userName;
        @NotBlank
        private String nickname;
        @NotBlank
        private String phoneNumber;

        public Patch addMemberId(Long memberId) {
            Assert.notNull(memberId, "member id must not be null.");
            this.memberId = memberId;

            return this;
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Response {
        private long memberId;
        private String email;
        private String userName;
        private String nickname;
        private String phoneNumber;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;


    }
}
