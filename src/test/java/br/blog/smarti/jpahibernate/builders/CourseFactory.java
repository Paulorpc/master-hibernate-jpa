package br.blog.smarti.jpahibernate.builders;

import br.blog.smarti.jpahibernate.entities.Course;

public class CourseFactory {
	
	private CourseFactory() {}
	
	public static CourseFactory newBuiler() {
		return new CourseFactory();
	}
	
	public Course getSample() {
		return Course.builder().name("Curso 1").build();
	}

}
