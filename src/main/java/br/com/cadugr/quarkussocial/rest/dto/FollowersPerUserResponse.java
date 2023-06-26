package br.com.cadugr.quarkussocial.rest.dto;

import java.util.List;
import java.util.stream.Collectors;

import br.com.cadugr.quarkussocial.domain.model.Follower;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FollowersPerUserResponse {
  private Integer followersCount;
  private List<FollowerResponse> content;


  public static FollowersPerUserResponse createFollowerPerUserResponse(List<Follower> followers) {
    
    List<FollowerResponse> followersResponse =  followers.stream().map(follower -> FollowerResponse.builder()
                                                       .id(follower.getFollower().getId())
                                                       .name(follower.getFollower().getName())
                                                       .build())
                                                       .collect(Collectors.toList());
    return FollowersPerUserResponse.builder()
                                   .followersCount(followers.size())
                                   .content(followersResponse)
                                   .build();                                                   


  }


}
