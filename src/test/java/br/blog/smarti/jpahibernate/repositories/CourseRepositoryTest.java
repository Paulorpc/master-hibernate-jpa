package br.blog.smarti.jpahibernate.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
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
		assertThat(course).isNotNull();
		assertEquals(course.getName(), courseDb.getName());
	}
	
	@Test
	@DirtiesContext
	void should_find_one_by_name() {
		Course courseDb = courseRepo.findByName("Curso 1");
		assertThat(course).isNotNull();
		assertEquals(course.getName(), courseDb.getName());
	}
	
	@Test
	@DirtiesContext
	void should_findAll() {
		this.setupList();
		ArrayList<Course> list = (ArrayList<Course>) courseRepo.findAll();
		assertEquals(10, list.size());
		Assertions.assertThat(list.get(9).getName()).containsIgnoringCase("curso 10");
		courseRepo.findAll().stream().forEach(System.out::println);
	}
	
	@Test
	@DirtiesContext
	void should_delete_by_id() {
		Course courseDeleted = courseRepo.deleteById(1L);
		assertEquals(courseDeleted.getId(), course.getId());
		assertEquals(course.getName(), courseDeleted.getName());
		
		course = courseRepo.findById(1L);
		assertThat(course).isNull();
	}
	
	@Test 
	@DirtiesContext
	void should_change_name() {
		Course courseSaved = courseRepo.playWithEntityManager();
		assertThat(courseSaved).isNotNull();
		assertNotEquals("Curso MODELO", courseSaved.getName());
		assertThat(courseSaved.getName()).contains("setName() é persistido no banco");
	}
	
	@Test
	@DirtiesContext
	void should_not_change_name() {
		Course courseSaved = courseRepo.playWithEntityManager_forcingFlushDetach();
		assertThat(courseSaved).isNotNull();
		assertEquals("Curso MODELO", courseSaved.getName());
		assertThat(courseSaved.getName()).doesNotContain("não é persistido no banco");
	}
	
	@Test
	@DirtiesContext
	void should_not_change_name_forced_refresh() {
		Course courseSaved = courseRepo.playWithEntityManager_forcingRefresh();
		assertThat(courseSaved).isNotNull();
		assertEquals("Curso MODELO", courseSaved.getName());
		assertThat(courseSaved.getName()).doesNotContain("não é persistido no banco");
	}
	
	@Test
	@DirtiesContext
	void should_change_name_forced_refresh_after_flush() {
		Course courseSaved = courseRepo.playWithEntityManager_forcingRefreshAfterFlush();
		assertThat(courseSaved).isNotNull();
		assertNotEquals("Curso MODELO", courseSaved.getName());
		assertThat(courseSaved.getName()).contains("é persistido no banco");
	}
}
