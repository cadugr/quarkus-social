package br.com.cadugr.quarkussocial.domain.repository;

import javax.enterprise.context.ApplicationScoped;

import br.com.cadugr.quarkussocial.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {
  
}
