package br.com.cadugr.quarkussocial.domain.repository;

import javax.enterprise.context.ApplicationScoped;

import br.com.cadugr.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
  
}
