package project.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class PostDto {

    @Getter
    @Setter
    public static class Post {
        private Long userId;
        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        @Size(max = 250, message = "제목은 250자를 넘을 수 없습니다.")
        private String title;
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        private String content;
        @NotBlank
        private String imgURL;
    }

    @Getter @Setter
    public static class Patch {
        private Long postId;
        private Long userId;
        @Size(max = 250, message = "제목은 250자를 넘을 수 없습니다.")
        private String title;
        private String content;
        private String imgURL;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class Response {
        private Long postId;
        private String title;
        private String content;
        private String imgURL;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

}
