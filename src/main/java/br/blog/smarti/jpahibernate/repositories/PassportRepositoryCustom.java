package br.blog.smarti.jpahibernate.repositories;

import br.blog.smarti.jpahibernate.entities.Passport;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public interface PassportRepositoryCustom {

  Optional<Passport> findByNumberRetrieveStudents(String number);
}
