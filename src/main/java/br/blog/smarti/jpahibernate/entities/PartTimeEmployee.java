package br.blog.smarti.jpahibernate.entities;

import java.math.BigDecimal;
import javax.persistence.Entity;
import lombok.Builder;

@Entity
public class PartTimeEmployee extends Employee {

  private BigDecimal hourlyWage;

  public PartTimeEmployee() {}

  @Builder
  public PartTimeEmployee(String name, BigDecimal hourSalary) {
    super(name);
    this.hourlyWage = hourSalary;
  }

  public BigDecimal getHourlyWage() {
    return hourlyWage;
  }

  public void setHourlyWage(BigDecimal hourlyWage) {
    this.hourlyWage = hourlyWage;
  }

  @Override
  public String toString() {
    return "PartTimeEmployee [hourlyWage=" + hourlyWage + "]";
  }
}
