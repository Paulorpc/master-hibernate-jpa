package br.blog.smarti.jpahibernate.repositories;

import br.blog.smarti.jpahibernate.entities.Employee;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository {

  public Long save(Employee e);

  public List<Employee> findAll();
}
