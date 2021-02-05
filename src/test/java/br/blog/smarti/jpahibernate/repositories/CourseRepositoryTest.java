package br.blog.smarti.jpahibernate.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import br.blog.smarti.jpahibernate.builders.CourseFactory;
import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Review;

@SpringBootTest
public class CourseRepositoryTest {

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CourseRepository courseRepo;
	
	@Autowired
	private ReviewRepository reviewRepo;

	private Course course;

	@BeforeEach
	void setup() throws Exception {
		course = CourseFactory.newBuiler().getSample();
		courseRepo.save(course);
	}

	void setupList() {
		IntStream.range(2, 11).forEach(n -> {
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
		Course courseDb = courseRepo.findByName("Curso 1");
		assertThat(courseDb).isNotNull();
		assertEquals(course.getName(), courseDb.getName());
	}

	@Test
	@DirtiesContext
	void should_findAll() {
		this.setupList();
		ArrayList<Course> list = (ArrayList<Course>) courseRepo.findAll_NamedQuery();
		assertEquals(10, list.size());
		Assertions.assertThat(list.get(9).getName()).containsIgnoringCase("curso 10");
		list.stream().forEach(System.out::println);
	}

	@Test
	@DirtiesContext
	void should_not_deleteById_there_is_childs() {
		assertThrows(DataIntegrityViolationException.class, () -> {
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
		assertThat(reviewsDeleted).isEmpty();;
		
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
		
		Course courseDb = courseRepo.findCourseWithReviews(course.getId());
		assertThat(courseDb).isNotNull();
		assertThat(courseDb.getReviews()).isNotNull();
		assertEquals(4, courseDb.getReviews().size());
		LOG.info(courseDb.getReviews().toString());
	}

	/***
	 * O atributo review não foi inicializado porque o fetch type do atributo é LAZY
	 * por default. Portanto, ele é feito através do JOIN FETCH em JPQL, pois apenas
	 * rodando o getter não é recuperado os dados, acredito que por ser uma
	 * collection (lista).
	 */
	@Test
	@DirtiesContext
	void should_get_course_with_reviews_using_joinFetch() {
		Course courseDb = courseRepo.findCourseWithReviews(course.getId());

		assertThat(courseDb).isNotNull();
		assertThat(courseDb.getName()).isNotNull();
		assertEquals(2, courseDb.getReviews().size());
		
		LOG.info(courseDb.getReviews().toString());

	}

	/***
	 * O atributo reviews não foi inicializado ao fazer o select porque o fetch type
	 * do atributo na entidade é LAZY por default. Está gerando exception de
	 * collection, mesmo forçando a inicialização do proxy, não sei ao certo o pq.
	 * Foi resolvido utilizando JPQL no método this.should_save_course_with_reviews
	 * e forçando a inicialização do proxy em
	 * StudentRepositoryTest.should_getCourses_has_hibernateInitialization_in_find_method.
	 */
	@Test
	@DirtiesContext
	void should_not_get_course_with_reviews_forcing_hibernateInitialization() {
		Course courseDb = courseRepo.findCourseWithReviewsForcingHibernateInitialization(course.getId());

		assertThat(courseDb).isNotNull();
		assertThat(courseDb.getName()).isNotNull();
		assertThrows(LazyInitializationException.class, () -> {
			assertEquals(2, courseDb.getReviews().size());
		});
	}
}
