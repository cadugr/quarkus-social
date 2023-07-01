package br.com.cadugr.quarkussocial.rest;

import static io.restassured.RestAssured.given;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.hamcrest.Matchers;

import br.com.cadugr.quarkussocial.domain.model.Follower;
import br.com.cadugr.quarkussocial.domain.model.Post;
import br.com.cadugr.quarkussocial.domain.model.User;
import br.com.cadugr.quarkussocial.domain.repository.FollowerRepository;
import br.com.cadugr.quarkussocial.domain.repository.PostRepository;
import br.com.cadugr.quarkussocial.domain.repository.UserRepository;
import br.com.cadugr.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
public class PostResourceTest {

  @Inject
  UserRepository userRepository;
  @Inject
  FollowerRepository followerRepository;
  @Inject
  PostRepository postRepository;
  Long userId;
  Long userNotFollowerId;
  Long userFollowerId;

  @BeforeEach
  @Transactional
  public void setUP() {
    //usuário padrão dos testes
    var user = User.builder().age(30).name("Fulano").build();
    userRepository.persist(user);
    userId = user.getId();

    //cria postagens para o user
    Post post = Post.builder().text("Hello people!").user(user).build();
    postRepository.persist(post);

    //usuário que não segue ninguém
    var userNotFollower = User.builder().age(33).name("Cicrano").build();
    userRepository.persist(userNotFollower);
    userNotFollowerId = userNotFollower.getId();

    //usuário seguidor
    var userFollower = User.builder().age(34).name("Beltrano").build();
    userRepository.persist(userFollower);
    userFollowerId = userFollower.getId();

    Follower follower = Follower.builder().user(user).follower(userFollower).build();
    followerRepository.persist(follower);

  }

  @Test
  @DisplayName("Should create a post for a user")
  public void createPostTest() {
    var postRequest = CreatePostRequest.builder().text("Some text").build();

    given()
      .contentType(ContentType.JSON)
      .body(postRequest)
      .pathParam("userId", userId)
    .when()
      .post()
    .then()
      .statusCode(201);    
  }

  @Test
  @DisplayName("Should return 404 when trying to make a post for an inexistent user")
  public void postForAnInexistentUserTest() {
    var postRequest = CreatePostRequest.builder().text("Some text").build();

    var inexistentUserId = 999;

    given()
      .contentType(ContentType.JSON)
      .body(postRequest)
      .pathParam("userId", inexistentUserId)
    .when()
      .post()
    .then()
      .statusCode(404);    
  }

  @Test
  @DisplayName("Should return 404 when user doesn't exist")
  public void listPostUserNotFoundTest() {
    var inexistentUserid = 999;

    given()
        .pathParam("userId", inexistentUserid)
    .when()
        .get()
    .then()
        .statusCode(404);        
  }

  @Test
  @DisplayName("Should return 400 when followerId header is not present")
  public void listPostFollowerHeaderNotSendTest() {
    given()
        .pathParam("userId", userId)
    .when()
        .get()
    .then()
        .statusCode(400)
        .body(Matchers.is("You forgot the header followerId"));        
  }

  @Test
  @DisplayName("Should return 400 when follower doesn't exist")
  public void listPostFollowerNotFoundTest() {

    var inexistentFollowerId = 999;

    given()
        .pathParam("userId", userId)
        .header("followerId", inexistentFollowerId)
    .when()
        .get()
    .then()
        .statusCode(400)
        .body(Matchers.is("Inexistent followerId"));
  }

  @Test
  @DisplayName("Should return 403 when follower isn't a follower")
  public void listPostNotAFollowerTest() {
    given()
        .pathParam("userId", userId)
        .header("followerId", userNotFollowerId)
    .when()
        .get()
    .then()
        .statusCode(403)
        .body(Matchers.is("You can't see these posts"));
  }

  @Test
  @DisplayName("Should list posts")
  public void listPostsTest() {
    given()
        .pathParam("userId", userId)
        .header("followerId", userFollowerId)
    .when()
        .get()
    .then()
        .statusCode(200)
        .body("size()", Matchers.is(1));
  }
  
}
