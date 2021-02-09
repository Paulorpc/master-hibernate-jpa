package br.blog.smarti.jpahibernate;

import br.blog.smarti.jpahibernate.repositories.CourseRepository;
import br.blog.smarti.jpahibernate.repositories.EmployeeRepository;
import br.blog.smarti.jpahibernate.repositories.PassportRepository;
import br.blog.smarti.jpahibernate.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpaHibernateApplication implements CommandLineRunner {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  public static void main(String[] args) {
    SpringApplication.run(JpaHibernateApplication.class, args);
  }

  @Autowired CourseRepository courseRepo;

  @Autowired StudentRepository studentRepo;

  @Autowired PassportRepository passportRepo;

  @Autowired EmployeeRepository employeeRepo;

  @Override
  public void run(String... args) throws Exception {
    // desativado para não ifluenciar nos testes unitátios.

    /***
     * persistindo e atualizando um objeto no banco.
     */
    // courseRepo.playWithEntityManager_forcingRefreshAfterFlush();

    /***
     * recuperando para testar a instância de student (fetch type lazy) este mesmo procedimento não
     * funcionou para o reviews (@oneToMany) por isso foi utilizado outra obordagem para recuperar
     * os dados.
     */
    // Optional<Passport> passport = passportRepo.findByNumberRetrieveStudents("E123456");
    // passport.ifPresent(p -> LOG.info(p.getStudent().toString()));

    /***
     * recuperando para testar a instância de reviews (fetch type lazy)
     */
    // ArrayList<Review> reviews = new ArrayList<Review>();
    // reviews.add(Review.builder().description("Nota 10").build());
    // reviews.add(Review.builder().description("Nota 9, show!").build());
    //
    // Course c =
    // Course.builder()
    // .name("Curso 1")
    // .reviews(reviews)
    // .build();
    //
    // courseRepo.save(c);
    // Course course = courseRepo.findCourseWithReviews(c.getId());
    // LOG.info(course.toString());
    // LOG.info(course.getReviews().toString());

    /*
     * Adicionando e recuperando employees com herança
     */
    // employeeRepo.save(FullTimeEmployee.builder()
    // .name("Paulo Cezar")
    // .salary(new BigDecimal(10000))
    // .build());
    //
    // employeeRepo.save(PartTimeEmployee.builder()
    // .name("Daniela Cristina")
    // .hourSalary(new BigDecimal(300))
    // .build());
    //
    // LOG.info(employeeRepo.findAll().toString());

    /***
     * usando o soft delete na entidade Student. Ao consultar o banco de dados os registro deletados
     * devem estar setados com is_delete = true.
     */
    // List<Student> students = studentRepo.findAll();
    // studentRepo.deleteById(students.get(1).getId());

    /***
     * Adicionando Student com classe @Embedable e Enum
     */
    //    Student student = Student.builder().name("Paulo Cezar")
    //        .address(Address.builder().logradouro("Rua ABC").numero("200").bairro("mirante sul")
    //            .cep("14022322").cidade("Ribeirão Preto").uf(EstadosEnum.SP).build())
    //        .build();
    //
    //    studentRepo.save(student);
  }
}
