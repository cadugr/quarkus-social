package br.com.cadugr.quarkussocial.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.cadugr.quarkussocial.domain.model.Follower;
import br.com.cadugr.quarkussocial.domain.model.User;
import br.com.cadugr.quarkussocial.domain.repository.FollowerRepository;
import br.com.cadugr.quarkussocial.domain.repository.UserRepository;
import br.com.cadugr.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
public class FollowerResourceTest {

  @Inject
  UserRepository userRepository;
  @Inject
  FollowerRepository followerRepository;
  Long userId;
  Long followerId;

  @BeforeEach
  @Transactional
  void setUp() {
    //usuário padrão dos testes
    var user = User.builder().age(30).name("Fulano").build();
    userRepository.persist(user);
    userId = user.getId();

    //usuário seguidor
    var follower = User.builder().age(34).name("Cicrano").build();
    userRepository.persist(follower);
    followerId = follower.getId();

    var followerEntity = Follower.builder().follower(follower).user(user).build();
    followerRepository.persist(followerEntity);
  }

  @Test
  @DisplayName("Should return 409 when followerId is equal to User id")
  public void sameUserAsFollowerTest() {
    
    var body = new FollowerRequest();
    body.setFollowerId(userId);
    
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .pathParam("userId", userId)
    .when()
        .put()
    .then()
        .statusCode(Response.Status.CONFLICT.getStatusCode())
        .body(Matchers.is("You can't follow yourself"));        

  }

  @Test
  @DisplayName("Should return 404 on follow a user when User id doesn't exist")
  public void userNotFoundWhenTryingToFollowTest() {
    
    var body = new FollowerRequest();
    body.setFollowerId(userId);
    var inexistentUserId = 999;
    
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .pathParam("userId", inexistentUserId)
    .when()
        .put()
    .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());        

  }

  @Test
  @DisplayName("Should follow a user")
  public void followerUserTest() {
    
    var body = new FollowerRequest();
    body.setFollowerId(followerId);
    
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .pathParam("userId", userId)
    .when()
        .put()
    .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());        

  }

  @Test
  @DisplayName("Should return 404 on list user followers and User id doesn't exist")
  public void userNotFoundWhenListingFollowersTest() {
    
    var inexistentUserId = 999;
    
    given()
        .contentType(ContentType.JSON)
        .pathParam("userId", inexistentUserId)
    .when()
        .get()
    .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());        

  }
  
  @Test
  @DisplayName("Should list a user's followers")
  public void listFollowersTest() {
    
    var response = given()
        .contentType(ContentType.JSON)
        .pathParam("userId", userId)
    .when()
        .get()
    .then()
        .extract().response();

    var followersCount = response.jsonPath().get("followersCount");
    var followersContent = response.jsonPath().getList("content");

    assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
    assertEquals(1, followersCount);
    assertEquals(1, followersContent.size());

  }

}
