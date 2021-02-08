package br.blog.smarti.jpahibernate.repositories.impl;

import br.blog.smarti.jpahibernate.entities.Employee;
import br.blog.smarti.jpahibernate.repositories.EmployeeRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class EmployeeRepositoryImpl implements EmployeeRepository {

  @Autowired EntityManager em;

  public Long save(Employee e) {
    if (e.getId() == null) {
      em.persist(e);
      em.flush();
    } else {
      em.merge(e);
    }
    return e.getId();
  }

  public List<Employee> findAll() {
    return em.createQuery("from Employee e", Employee.class).getResultList();
  }
}
