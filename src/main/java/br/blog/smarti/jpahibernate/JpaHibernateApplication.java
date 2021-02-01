package br.blog.smarti.jpahibernate;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.blog.smarti.jpahibernate.repositories.CourseRepository;
import br.blog.smarti.jpahibernate.repositories.PassportRepository;
import br.blog.smarti.jpahibernate.repositories.StudentRepository;

@SpringBootApplication
public class JpaHibernateApplication  implements CommandLineRunner {
	
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	public static void main(String[] args){
		SpringApplication.run(JpaHibernateApplication.class, args);
	}
	
	@Autowired
	CourseRepository courseRepo;
	
	@Autowired
	StudentRepository studentRepo;
	
	@Autowired
	PassportRepository passportRepo;
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
//		persistindo e atualizando um objeto no banco.
//		desativado para não ifluenciar nos testes unitátios. 
//		courseRepo.playWithEntityManager_forcingRefreshAfterFlush();
	}
}
