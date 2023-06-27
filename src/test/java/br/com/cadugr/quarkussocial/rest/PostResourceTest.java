package br.com.cadugr.quarkussocial.rest;

import static io.restassured.RestAssured.given;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.cadugr.quarkussocial.domain.model.User;
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
  Long userId;

  @BeforeEach
  @Transactional
  public void setUP() {
    var user = User.builder().age(30).name("Fulano").build();
    userRepository.persist(user);
    userId = user.getId();
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
  
}
