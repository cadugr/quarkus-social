package br.com.cadugr.quarkussocial.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FollowerResponse {
  private Long id;
  private String name;
}
