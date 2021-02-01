package br.blog.smarti.jpahibernate.builders;

import br.blog.smarti.jpahibernate.entities.Passport;
import br.blog.smarti.jpahibernate.entities.Student;

public class StudentFactory {
	
	private StudentFactory() {}
	
	private static int N = 0;
	
	public static StudentFactory newBuiler() {
		return new StudentFactory();
	}
	
	public Student getSample() {
		return Student.builder().name("Student " + ++N)
								.passport(Passport.builder()
													.number("PN-1234-" +N).build())
				.build();			
	}

}
