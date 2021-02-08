package br.blog.smarti.jpahibernate.repositories;

import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Student;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.persistence.CascadeType;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository {

  public Student findById(Long id);

  public Student findByName(String name);

  public Student findByNameRetrievePassport(String name);

  public Student findByIdWithCourses(Long id);

  public Student findByIdWithCourses(Long id, boolean hibernateInitialize);

  public Long save(Student s) throws NoSuchFieldException, SecurityException;

  public Student saveStudentAndCourse(Student s, Course c);

  public Long saveStudentAndCourses(Student s, List<Course> courses)
      throws NoSuchFieldException, SecurityException;

  public Student deleteById(Long id);

  public List<Course> findAllStudentCourses(Long studentId);

  public List<Student> findAllStudentsByPassport(String passport);

  /***
   * Verifica se o atributo fieldName na classe é transient ou não. É transiente
   * se não tiver annotations do tipo cascade persist ou all no objeto de
   * relacionamento.
   */
  public static boolean isTransient(String fieldName)
      throws NoSuchFieldException, SecurityException {
    boolean isTransient = true;
    Annotation[] annotations = Student.class.getDeclaredField(fieldName).getDeclaredAnnotations();
    for (Annotation annotation : annotations) {
      if (annotation instanceof javax.persistence.OneToOne) {
        javax.persistence.OneToOne myAnnotation = (javax.persistence.OneToOne) annotation;

        try {
          if (myAnnotation.cascade()[0].equals(CascadeType.PERSIST)
              || myAnnotation.cascade()[0].equals(CascadeType.ALL)) {
            isTransient = false;
          }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
      }
    }
    return isTransient;
  }
}
