package br.blog.smarti.jpahibernate.entities;

import java.math.BigDecimal;
import javax.persistence.Entity;
import lombok.Builder;

@Entity
public class FullTimeEmployee extends Employee {

  private BigDecimal salary;

  public FullTimeEmployee() {}
  ;

  @Builder
  public FullTimeEmployee(String name, BigDecimal salary) {
    super(name);
    this.salary = salary;
  }

  public BigDecimal getSalary() {
    return salary;
  }

  public void setSalary(BigDecimal salary) {
    this.salary = salary;
  }

  @Override
  public String toString() {
    return "FullTimeEmployee [salary=" + salary + "]";
  }
}
