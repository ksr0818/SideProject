package project.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.utils.URICreator;

import javax.transaction.Transactional;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@RequestMapping("/posts")
@Validated
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {

    private final PostService service;
    private final PostMapper mapper;
    @PostMapping
    public ResponseEntity Postpost(@Validated @RequestBody PostDto.Post post) {
        Post newPost = mapper.postPostDtoToPost(post);

        Post createdPost = service.savePost(newPost);

        URI uri = URICreator.createUri("/post", createdPost.getPostId());

        return ResponseEntity.created(uri).build();
    }

    @Transactional
    @PatchMapping("/{post-id}")
    public ResponseEntity patchPost(@Validated @RequestBody PostDto.Patch patch) {
//        Post newPost = mapper.patchPostDtoToPost(patch);
//
//        Post updatedPost = service.savePost(newPost);
//
//        URI uri = URICreator.createUri("/post", createdPost.getPostId());
//
//        return new ResponseEntity(new SingleResponse<>(mapper.postToPostResponseDto(updatedPost)), HttpStatus.OK);

        return null;
    }

    @GetMapping
    public ResponseEntity getAllPost() {

        return null;
    }

    @GetMapping("/{post-id}")
    public ResponseEntity getPost(@PathVariable("post-id") @Positive long postId) {
        return null;
    }

    @DeleteMapping("/{post-id}")
    public ResponseEntity PatchPost(@PathVariable("post-id") @Positive long postId) {
        return null;
    }
}
