package br.blog.smarti.jpahibernate.repositories.impl;

import br.blog.smarti.jpahibernate.entities.Passport;
import br.blog.smarti.jpahibernate.repositories.PassportRepository;
import br.blog.smarti.jpahibernate.repositories.PassportRepositoryCustom;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class PassportRepositoryCustomImpl implements PassportRepositoryCustom {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  /***
   * Anotação @Lazy evita erro de inicialização do Spring (JpaHibernateApplication.class) quando o
   * PassportRepository é instânciado. Provavelmente está tentando inicializar antes de criar o
   * bean.
   */
  @Lazy
  @Autowired
  PassportRepository passportRepo;

  @Autowired
  EntityManager em;

  @Transactional
  public Optional<Passport> findByNumberRetrieveStudents(String number) {
    LOG.info("find passport by number retrieving student: {}", number);
    Optional<Passport> passport = passportRepo.findByNumber(number);
    passport.ifPresent(p -> p.setStudent(p.getStudent()));
    return passport;
  }
}
