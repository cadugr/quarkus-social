package br.com.cadugr.quarkussocial.rest;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.cadugr.quarkussocial.domain.model.Post;
import br.com.cadugr.quarkussocial.domain.model.User;
import br.com.cadugr.quarkussocial.domain.repository.FollowerRepository;
import br.com.cadugr.quarkussocial.domain.repository.PostRepository;
import br.com.cadugr.quarkussocial.domain.repository.UserRepository;
import br.com.cadugr.quarkussocial.rest.dto.CreatePostRequest;
import br.com.cadugr.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
  
  @Inject
  private UserRepository userRepository;

  @Inject
  private PostRepository postRepository;

  @Inject
  private FollowerRepository followerRepository;

  @POST
  @Transactional
  public Response savePost(@PathParam("userId") Long userId, CreatePostRequest postRequest) {
    User user = userRepository.findById(userId);
    if(user == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    Post post = Post.builder().text(postRequest.getText()).user(user).build();
    postRepository.persist(post);

    return Response.status(Status.CREATED).entity(post).build();
  }

  @GET
  public Response listPosts(@PathParam("userId") Long userId,
                            @HeaderParam("followerId") Long followerId) {
    User user = userRepository.findById(userId);
    if(user == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    if(followerId == null) {
      return Response.status(Status.BAD_REQUEST).entity("You forgot the header followerId").build();
    }

    User follower = userRepository.findById(followerId);

    if(follower == null) {
      return Response.status(Status.BAD_REQUEST).entity("Inexistent followerId").build();
    }

    boolean follows = followerRepository.follows(follower, user);

    if(!follows) {
      return Response.status(Status.FORBIDDEN).entity("You can't see these posts").build();
    }

    PanacheQuery<Post> query = postRepository.find("user", Sort.by("dataTime", Direction.Descending) ,user);
    var posts = query.list();

    List<PostResponse> postsResponse = PostResponse.fromListPost(posts);

    return Response.ok(postsResponse).build();
  }

}
