package project.post;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

    Post postPostDtoToPost(PostDto.Post requestBody);

    PostDto.Response postToPostResponseDto(Post post);

}
