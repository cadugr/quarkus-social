package br.com.cadugr.quarkussocial.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.cadugr.quarkussocial.domain.model.Follower;
import br.com.cadugr.quarkussocial.domain.repository.FollowerRepository;
import br.com.cadugr.quarkussocial.domain.repository.UserRepository;
import br.com.cadugr.quarkussocial.rest.dto.FollowerRequest;
import br.com.cadugr.quarkussocial.rest.dto.FollowersPerUserResponse;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

  @Inject
  private FollowerRepository followerRepository;

  @Inject
  private UserRepository userRepository;

  @GET
  public Response listFollowers(@PathParam("userId") Long userId) {
    
    var user = userRepository.findById(userId);
    
    if(user == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    var list = followerRepository.findByUser(userId);
    FollowersPerUserResponse followersPerUserResponse = FollowersPerUserResponse.createFollowerPerUserResponse(list);
    return Response.ok(followersPerUserResponse).build();
  }

  @PUT
  @Transactional
  public Response followUser(@PathParam("userId") Long userId, FollowerRequest followerRequest) {
    
    if(userId.equals(followerRequest.getFollowerId())) {
      return Response.status(Status.CONFLICT).entity("You can't follow yourself").build();
    }
    
    var user = userRepository.findById(userId);
    if(user == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    var follower = userRepository.findById(followerRequest.getFollowerId());

    boolean follows = followerRepository.follows(follower, user);

    if(!follows) {
      var entity = Follower.builder().user(user).follower(follower).build(); 
      followerRepository.persist(entity);
    }

    return Response.status(Status.NO_CONTENT).build();

  }

  @DELETE
  @Transactional
  public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
    
    var user = userRepository.findById(userId);
    if(user == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    followerRepository.deleteByFollowerAndUser(followerId, userId);

    return Response.status(Status.NO_CONTENT).build();

  }
  
}
