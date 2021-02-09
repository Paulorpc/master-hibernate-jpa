package br.blog.smarti.jpahibernate.repositories;

import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Review;
import br.blog.smarti.jpahibernate.entities.Student;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository {

  public Course findCourseRetrieveReviews_JoinFetch(Long courseId);

  public Course findCourseRetrieveReviews_HibernateInitialization(Long courseId);

  public Course findCourseRetrieveReviews_EntityGraph(Long courseId);

  public List<Course> findAll();

  public Course findById(Long id);

  public Course findById_NamedQuery(Long id);

  public Course findByName(String name);

  public List<Course> findAll_NamedQuery();

  public Long save(Course c);

  public Course deleteById(Long id);

  public Course playWithEntityManager();

  public Course playWithEntityManager_forcingFlushDetach();

  public Course playWithEntityManager_forcingRefresh();

  public Course playWithEntityManager_forcingRefreshAfterFlush();

  public void saveCourseReviews(Long courseId, List<Review> reviews);

  public List<Course> findAllCoursesWithoutStudents();

  public List<Course> findAllCoursesWithMoreThanStudents(int n);

  public List<Course> findAllCoursesOrderedByStudents();

  public List<Student> findAllStudentsByCourseId(Long courseId);

  public List<Course> findAllRetrieveStudents();

  public Long saveCourseAndStudents(Course c, List<Student> students) throws Exception;
}
