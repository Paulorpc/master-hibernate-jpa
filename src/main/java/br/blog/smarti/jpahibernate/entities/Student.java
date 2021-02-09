package br.blog.smarti.jpahibernate.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "student")
@SQLDelete(sql = "update student set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class Student {

  private static Logger LOG = LoggerFactory.getLogger(Course.class);

  @Id @GeneratedValue private Long id;

  @Column(nullable = false)
  private String name;

  @OneToOne(fetch = FetchType.LAZY)
  private Passport passport;

  @ManyToMany
  @JoinTable(
      name = "student_course",
      joinColumns = @JoinColumn(name = "student_id"),
      inverseJoinColumns = @JoinColumn(name = "course_id"))
  private List<Course> courses = new ArrayList<>();

  @Embedded private Address address;

  @CreationTimestamp private LocalDateTime createdDate;

  @UpdateTimestamp private LocalDateTime updatedDate;

  private boolean isDeleted;

  public Student() {}

  @Builder
  public Student(Long id, String name, Passport passport, Address address) {
    super();
    this.id = id;
    this.name = name;
    this.passport = passport;
    this.address = address;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Passport getPassport() {
    return passport;
  }

  public void setPassport(Passport passport) {
    this.passport = passport;
  }

  public List<Course> getCourses() {
    return courses;
  }

  public void setCourses(List<Course> courses) {
    this.courses = courses;
  }

  public void addCourse(Course course) {
    this.courses.add(course);
  }

  public void removeCourse(Course course) {
    this.courses.remove(course);
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  @PreRemove
  public void preRemove() {
    LOG.info("setting {}.id({}).isDeleted = true", this.getClass().getSimpleName(), this.id);
    this.isDeleted = true;
  }

  @Override
  public String toString() {
    return "Student [id=" + id + ", name=" + name + "]";
  }
}
