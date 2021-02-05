package br.blog.smarti.jpahibernate.builders;

import java.util.ArrayList;

import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Review;

public class CourseFactory {
	
	private CourseFactory() {}
	
	private static int N;
	
	public static CourseFactory newBuiler() {
		return new CourseFactory();
	}
	
	public Course getSample() {
		ArrayList<Review> reviews = new ArrayList<Review>();
		reviews.add(Review.builder().description("Nota 10").build());
		reviews.add(Review.builder().description("Nota 9, show!").build());
		
		return Course.builder()
				.name("Curso " + ++N)
				.reviews(reviews)
				.build();
	}

}
