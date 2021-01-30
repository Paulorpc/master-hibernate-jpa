package br.blog.smarti.jpahibernate;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.repositories.CourseRepository;

@SpringBootApplication
public class JpaHibernateApplication  implements CommandLineRunner {
	
	public static void main(String[] args){
		SpringApplication.run(JpaHibernateApplication.class, args);
	}
	
	@Autowired
	CourseRepository courseRepo;
	
	@Override
	public void run(String... args) throws Exception {
	}

}
