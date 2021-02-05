package br.blog.smarti.jpahibernate.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NamedQueries({
  @NamedQuery(name = "getAllCourses", query = "SELECT c FROM Course c"),
  @NamedQuery(name = "getCourseById", query = "SELECT c FROM Course c WHERE c.id = :id")
})
@Entity
@Table(name = "course")
public class Course {

  @Id @GeneratedValue private Long id;

  @Column(nullable = false)
  private String name;

  private String professor;

  // fk deve estar na tabela review (owner relationchip)
  @OneToMany(mappedBy = "course")
  private List<Review> reviews = new ArrayList<Review>();

  @ManyToMany(mappedBy = "courses")
  List<Student> students = new ArrayList<Student>();

  @UpdateTimestamp private LocalDateTime updatedDate;

  @CreationTimestamp private LocalDateTime createdDate;

  public Course() {}

  public Course(String name) {
    super();
    this.name = name;
  }

  @Builder
  public Course(Long id, String name, String professor, List<Review> reviews) {
    super();
    this.id = id;
    this.name = name;
    this.professor = professor;
    this.reviews = reviews;
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

  public String getProfessor() {
    return professor;
  }

  public void setProfessor(String professor) {
    this.professor = professor;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }

  public void addReview(Review review) {
    this.reviews.add(review);
  }

  public void removeReview(Review review) {
    this.reviews.remove(review);
  }

  public List<Student> getStudents() {
    return students;
  }

  public void setStudents(List<Student> students) {
    this.students = students;
  }

  public void addStudent(Student student) {
    this.students.add(student);
  }

  public void removeStudent(Student student) {
    this.students.remove(student);
  }

  @Override
  public String toString() {
    return "Course [id="
        + id
        + ", name="
        + name
        + ", professor="
        + professor
        + ", updatedDate="
        + updatedDate
        + ", createdDate="
        + createdDate
        + "]";
  }
}
