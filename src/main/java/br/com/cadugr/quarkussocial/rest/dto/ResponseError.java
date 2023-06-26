package br.com.cadugr.quarkussocial.rest.dto;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ResponseError {

  public static final int UNPROCESSABLE_ENTITY_STATUS = 422;
  
  private String message;
  private Collection<FieldError> errors;

  public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations ) {
    List<FieldError> errors = violations
              .stream()
              .map(cv -> FieldError
                                  .builder()
                                  .field(cv.getPropertyPath().toString())
                                  .message(cv.getMessage())
                                  .build())
              .collect(Collectors.toList());
    String message = "Validation Error";
    return ResponseError.builder().message(message).errors(errors).build();        
  }

  public Response withStatusCode(int code) {
    return Response.status(code).entity(this).build();
  }

}
