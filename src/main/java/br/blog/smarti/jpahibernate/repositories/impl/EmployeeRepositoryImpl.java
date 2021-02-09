package br.blog.smarti.jpahibernate.repositories.impl;

import br.blog.smarti.jpahibernate.entities.Employee;
import br.blog.smarti.jpahibernate.repositories.EmployeeRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class EmployeeRepositoryImpl implements EmployeeRepository {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired EntityManager em;

  /***
   * Método persisti ou alterar entidade
   */
  public Long save(Employee e) {
    LOG.info("save employee: {}", e.toString());
    if (e.getId() == null) {
      em.persist(e);
      em.flush();
    } else {
      em.merge(e);
    }
    return e.getId();
  }

  public List<Employee> findAll() {
    LOG.info("find all emploiees: {}");
    return em.createQuery("from Employee e", Employee.class).getResultList();
  }
}
