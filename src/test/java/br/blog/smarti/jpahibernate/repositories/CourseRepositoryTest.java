package br.blog.smarti.jpahibernate.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import br.blog.smarti.jpahibernate.builders.CourseFactory;
import br.blog.smarti.jpahibernate.entities.Course;

@SpringBootTest
public class CourseRepositoryTest {
	
	@Autowired
	private CourseRepository courseRepo;
	
	private Course course;
	
	@BeforeEach
	void setup() {
		course = CourseFactory.newBuiler().getSample();
		courseRepo.save(course);
	}
	
	void setupList() {
		IntStream.range(2, 11).forEach(n -> {
			course = CourseFactory.newBuiler().getSample();
			course.setName("Curso " +n);
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
	void should_delete_by_id() {
		Course courseDeleted = courseRepo.deleteById(1L);
		assertEquals(courseDeleted.getId(), course.getId());
		assertEquals(course.getName(), courseDeleted.getName());
		
		Course courseDb = courseRepo.findById(1L);
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
}
