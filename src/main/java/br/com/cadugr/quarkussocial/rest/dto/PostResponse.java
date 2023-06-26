package br.com.cadugr.quarkussocial.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.cadugr.quarkussocial.domain.model.Post;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {

  private String text;
  private LocalDateTime dataTime;

  public static List<PostResponse> fromListPost(List<Post> posts) {
    return posts.stream()
                .map(post -> PostResponse.builder()
                                         .text(post.getText())
                                         .dataTime(post.getDataTime())
                                         .build()).collect(Collectors.toList());
  }
  
}