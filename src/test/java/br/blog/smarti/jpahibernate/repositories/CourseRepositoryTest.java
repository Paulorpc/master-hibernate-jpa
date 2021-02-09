package br.blog.smarti.jpahibernate.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import br.blog.smarti.jpahibernate.builders.CourseFactory;
import br.blog.smarti.jpahibernate.builders.StudentFactory;
import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Review;
import br.blog.smarti.jpahibernate.entities.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
public class CourseRepositoryTest {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired private CourseRepository courseRepo;

  @Autowired private ReviewRepository reviewRepo;

  private Course course;

  @BeforeEach
  void setup() throws Exception {
    course = CourseFactory.newBuiler().getSample();
    courseRepo.save(course);
  }

  void setupList() {
    IntStream.range(2, 11)
        .forEach(
            n -> {
              course = CourseFactory.newBuiler().getSample();
              course.setName("Curso " + n);
              courseRepo.save(course);
            });
  }

  @Test
  @DirtiesContext
  void should_find_by_id() {
    Course courseDb = courseRepo.findById(1L);
    assertThat(courseDb).isNotNull();
    assertEquals(course.getName(), courseDb.getName());

    courseDb = courseRepo.findById_NamedQuery(1L);
    assertThat(courseDb).isNotNull();
    assertEquals(course.getName(), courseDb.getName());
  }

  @Test
  @DirtiesContext
  void should_not_find_by_id() {
    Course courseDb = courseRepo.findById(2L);
    assertThat(courseDb).isNull();
  }

  @Test
  @DirtiesContext
  void should_find_one_by_name() {
    Course courseDb = courseRepo.findById(1L);
    assertThat(courseDb).isNotNull();
    assertEquals(course.getName(), courseDb.getName());
  }

  @Test
  @DirtiesContext
  void should_findAll() {
    this.setupList();
    ArrayList<Course> list = (ArrayList<Course>) courseRepo.findAll_NamedQuery();
    assertEquals(13, list.size());
    list.stream().forEach(System.out::println);
  }

  @Test
  @DirtiesContext
  void should_not_deleteById_there_is_childs() {
    assertThrows(
        DataIntegrityViolationException.class,
        () -> {
          courseRepo.deleteById(course.getId());
        });
  }

  @Test
  @DirtiesContext
  void should_deleteAllByCourseId() {
    List<Review> reviewsDeleted = reviewRepo.deleteAllByCourseId(course.getId());
    assertThat(reviewsDeleted).isNotNull();
    assertEquals(2, reviewsDeleted.size());

    reviewsDeleted.clear();
    reviewsDeleted = reviewRepo.findAllByCourseId(course.getId());
    assertThat(reviewsDeleted).isEmpty();
    ;

    Course courseDeleted = courseRepo.deleteById(course.getId());
    assertEquals(courseDeleted.getId(), course.getId());

    Course courseDb = courseRepo.findById(course.getId());
    assertThat(courseDb).isNull();
  }

  @Test
  @DirtiesContext
  void should_change_name() {
    Course courseDb = courseRepo.playWithEntityManager();
    assertThat(courseDb).isNotNull();
    assertNotEquals("Curso MODELO", courseDb.getName());
    assertThat(courseDb.getName()).contains("setName() é persistido no banco");
  }

  @Test
  @DirtiesContext
  void should_not_change_name() {
    Course courseDb = courseRepo.playWithEntityManager_forcingFlushDetach();
    assertThat(courseDb).isNotNull();
    assertEquals("Curso MODELO", courseDb.getName());
    assertThat(courseDb.getName()).doesNotContain("não é persistido no banco");
  }

  @Test
  @DirtiesContext
  void should_not_change_name_forced_refresh() {
    Course courseDb = courseRepo.playWithEntityManager_forcingRefresh();
    assertThat(courseDb).isNotNull();
    assertEquals("Curso MODELO", courseDb.getName());
    assertThat(courseDb.getName()).doesNotContain("não é persistido no banco");
  }

  @Test
  @DirtiesContext
  void should_change_name_forced_refresh_after_flush() {
    Course courseDb = courseRepo.playWithEntityManager_forcingRefreshAfterFlush();
    assertThat(courseDb).isNotNull();
    assertNotEquals("Curso MODELO", courseDb.getName());
    assertThat(courseDb.getName()).contains("é persistido no banco");
  }

  @Test
  @DirtiesContext
  void should_save_course_with_reviews() {
    ArrayList<Review> list = new ArrayList<Review>();
    list.add(Review.builder().description("nota 10").build());
    list.add(Review.builder().description("nota 8").build());

    courseRepo.saveCourseReviews(course.getId(), list);

    Course courseDb = courseRepo.findCourseRetrieveReviews_JoinFetch(course.getId());
    assertThat(courseDb).isNotNull();
    assertThat(courseDb.getReviews()).isNotNull();
    assertEquals(4, courseDb.getReviews().size());
    LOG.info(courseDb.getReviews().toString());
  }

  /***
   * O atributo review não foi inicializado porque o fetch type do atributo é LAZY por default.
   * Portanto, ele é feito através do JOIN FETCH em JPQL. Solução evita N+1 Problem.
   */
  @Test
  @DirtiesContext
  void should_get_course_retrieving_reviews_using_joinFetch() {
    Course courseDb = courseRepo.findCourseRetrieveReviews_JoinFetch(course.getId());

    assertThat(courseDb).isNotNull();
    assertThat(courseDb.getName()).isNotNull();
    assertThat(courseDb.getReviews().size()).isGreaterThan(1);
    assertEquals(course.getName(), courseDb.getName());
    assertEquals(
        course.getReviews().get(0).getDescription(), courseDb.getReviews().get(0).getDescription());

    LOG.info(courseDb.getReviews().toString());
  }

  /***
   * O atributo review não foi inicializado porque o fetch type do atributo é LAZY por default.
   * Portanto, ele é feito através do Entity Graph. Solução evita N+1 Problem.
   */
  @Test
  @DirtiesContext
  void should_get_course_retrieving_reviews_using_entitygraph() {
    Course courseDb = courseRepo.findCourseRetrieveReviews_EntityGraph(course.getId());

    assertThat(courseDb).isNotNull();
    assertThat(courseDb.getName()).isNotNull();
    assertThat(courseDb.getReviews().size()).isGreaterThan(1);
    assertEquals(course.getName(), courseDb.getName());
    assertEquals(
        course.getReviews().get(0).getDescription(), courseDb.getReviews().get(0).getDescription());

    LOG.info(courseDb.getReviews().toString());
  }

  /***
   * O atributo review não foi inicializado porque o fetch type do atributo é LAZY por default.
   * Portanto, ele é feito através do Hibernate.Initialization. Solução evita N+1 Problem em termos.
   * As soluções de EntityGraph e JoinFetch fazem um único select, já a Hibernate.Initialization faz
   * dois, pois inicialmente é carregado o objeto pai e depois inicializado o getter através do
   * proxy para o objeto fiho, quando é feito o segundo select. a
   */
  @Test
  @DirtiesContext
  void should_get_course_retrieving_reviews_using_hibernateInitialization() {
    Course courseDb = courseRepo.findCourseRetrieveReviews_HibernateInitialization(course.getId());

    assertThat(courseDb).isNotNull();
    assertThat(courseDb.getName()).isNotNull();
    assertThat(courseDb.getReviews().size()).isGreaterThan(1);
    assertEquals(course.getName(), courseDb.getName());
    assertEquals(
        course.getReviews().get(0).getDescription(), courseDb.getReviews().get(0).getDescription());

    LOG.info(courseDb.getReviews().toString());
  }

  @Test
  @DirtiesContext
  void should_get_all_courses_whithout_students() throws Exception {
    List<Course> courses = courseRepo.findAllCoursesWithoutStudents();
    assertThat(courses).isNotEmpty();
    assertEquals(course.getName(), courses.get(0).getName());
    LOG.info(courses.toString());

    System.out.println("\nUm curso não deve ter students: \n");
    this.getDistinctedCoursesAndPrintStudentsList();

    List<Student> students = Lists.newArrayList(StudentFactory.newBuiler().getSample());
    courseRepo.saveCourseAndStudents(course, students);

    System.out.println("\ntodos cursos devem ter students: \n");
    this.getDistinctedCoursesAndPrintStudentsList();

    courses = courseRepo.findAllCoursesWithoutStudents();
    assertThat(courses).isEmpty();
  }

  @Test
  @DirtiesContext
  void should_get_all_courses_with_2_students() throws Exception {
    List<Course> courses = courseRepo.findAllCoursesWithMoreThanStudents(1);
    assertThat(courses).isNotEmpty();
    assertEquals(1, courses.size());
    LOG.info(courses.toString());
  }

  @Test
  void should_find_all_courses_ordered_by_ammount_of_students_joinned() {
    List<Course> courses = courseRepo.findAllCoursesOrderedByStudents();
    courses.forEach(c -> c.setStudents(courseRepo.findAllStudentsByCourseId(c.getId())));
    courses.stream().map(c -> c.getStudents()).map(sl -> sl.size()).forEach(System.out::print);
  }

  private void getDistinctedCoursesAndPrintStudentsList() {
    List<Course> courses = courseRepo.findAllRetrieveStudents();
    courses = courses.stream().distinct().collect(Collectors.toList());
    courses.stream()
        .map(c -> c.getStudents())
        .collect(Collectors.toList())
        .forEach(System.out::println);
    ;
  }
}
