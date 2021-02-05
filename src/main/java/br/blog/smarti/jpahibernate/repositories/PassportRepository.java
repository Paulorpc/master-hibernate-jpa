package br.blog.smarti.jpahibernate.repositories;

import br.blog.smarti.jpahibernate.entities.Passport;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class PassportRepository {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired EntityManager em;

  public Passport findById(Long id) {
    LOG.info("find passport by id: {}", id);
    Passport p = em.find(Passport.class, id);
    return p;
  }

  @SuppressWarnings("unchecked")
  public Passport findByNumber(String number) {
    LOG.info("find passport by number: {}", number);
    String sql = "select * from passport where number = :number";
    return (Passport)
        em.createNativeQuery(sql, Passport.class)
            .setParameter("number", number)
            .getResultStream()
            .findFirst()
            .orElse(null);
  }

  public Passport findByNumberWithStudent(String number) {
    LOG.info("ind passport with student by number: {}", number);
    Passport p = findByNumber(number);
    p.setStudent(p.getStudent());
    return p;
  }

  public Long save(Passport p) {
    if (p.getId() == null) {
      LOG.info("saving passport");
      em.persist(p);
      em.flush();
    } else {
      LOG.info("updating passport id: " + p.getId());
      em.merge(p);
    }
    return p.getId();
  }

  public Passport deleteById(Long id) {
    LOG.info("deleting passport by id: " + id);
    Passport p = findById(id);
    em.remove(p);
    return p;
  }
}
