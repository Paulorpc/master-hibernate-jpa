package br.blog.smarti.jpahibernate.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.blog.smarti.jpahibernate.builders.CourseFactory;
import br.blog.smarti.jpahibernate.builders.StudentFactory;
import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Passport;
import br.blog.smarti.jpahibernate.entities.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
public class StudentRepositoryTest {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired private StudentRepository studentRepo;

  @Autowired private PassportRepository passportRepo;

  Student studentNew;

  @BeforeEach
  void setup() throws NoSuchFieldException, SecurityException {
    studentNew = StudentFactory.newBuiler().getSample();
    studentRepo.save(studentNew);
  }

  @AfterEach
  void tearsDown() {
    studentRepo.deleteById(studentNew.getId());
  }

  /***
   * Foi feito uma "brincadeira" no StudentRepository para verificar se o objeto
   * filho passport (fk) é transiente ou não. Se ele for transiente, ou seja, não
   * foi adicionado o cascade.PERSIST para fazer a persistencia dos objetos
   * filhos, então é feita a persistência do objeto manualmente. Sendo assim,
   * sendo transiente ou não nunca irá falhar ao persistir um Student mesmo com o
   * objeto passport setado.
   */
  @Test
  void should_save_student_with_passport() throws NoSuchFieldException, SecurityException {
    Student student = studentRepo.findByNameRetrievePassport(studentNew.getName());
    assertThat(student).isNotNull();
    assertEquals(studentNew.getPassport().getNumber(), student.getPassport().getNumber());
    LOG.info(student.toString());
    LOG.info(student.getPassport().toString());
  }

  /***
   * É esperado ao fazer o student.getPassport(), pois é necessários estar numa
   * transação para que o hibernate possa recuperar os dados do database
   * automaticamente. Lembrando que o atributo passport não é populado ao fazer o
   * select do Student porque o fetch type do atributo na entidade está
   * configurado para LAZY
   */
  @Test()
  void should_not_getPassport_doesnt_has_transaction()
      throws NoSuchFieldException, SecurityException {
    Student student = studentRepo.findByName(studentNew.getName());
    assertThat(student).isNotNull();
    assertEquals(studentNew.getName(), student.getName());

    assertThrows(
        LazyInitializationException.class,
        () -> {
          assertEquals(studentNew.getPassport().getNumber(), student.getPassport().getNumber());
        });
  }

  /***
   * O atributo passport não foi inicializado ao fazer o select do Student porque
   * o fetch type do atributo na entidade está configurado para LAZY. Portanto,
   * ele é feito ao fazer a chamada do método .getStudent(), para isso é
   * necessário a transação. Ao alterar para EAGER, fazendo a inicialização de um
   * Student, também seria populado o atributo passport.
   */
  @Test
  @Transactional
  void should_getPassport_has_transaction() throws NoSuchFieldException, SecurityException {
    Student student = studentRepo.findByName(studentNew.getName());
    assertThat(student).isNotNull();
    assertEquals(studentNew.getName(), student.getName());

    Passport studentPassport = student.getPassport();
    assertThat(studentPassport).isNotNull();
    LOG.info(studentPassport.toString());
  }

  /***
   * neste caso é recuperado os dados do passport de forma manual, no entanto não
   * é necessário uma transação como no caso acima.
   */
  @Test
  void should_getPassport_doesnt_has_transaction() throws NoSuchFieldException, SecurityException {
    Student student = studentRepo.findByName(studentNew.getName());
    assertThat(student).isNotNull();
    assertEquals(studentNew.getName(), student.getName());

    Optional<Passport> studentPassport =
        passportRepo.findByNumber(studentNew.getPassport().getNumber());
    student.setPassport(studentPassport.get());

    assertThat(student.getPassport()).isNotNull();
    LOG.info(studentPassport.toString());
  }

  /***
   * Teste de pegar o Student no objeto passport pelo relacionamento bidirecional.
   * o atributo student não foi inicializado ao fazer o select do passport porque
   * o fetch type do atributo na entidade está configurado para LAZY. Para isso
   * foi criado um método no repositório que está dentro de uma transação que após
   * fazer o select do passport, faz o get do student e o seta no passport.
   */
  @Test
  void should_getStudent_has_transaction_in_find_method()
      throws NoSuchFieldException, SecurityException {
    Optional<Passport> passport =
        passportRepo.findByNumberRetrieveStudents(studentNew.getPassport().getNumber());
    assertTrue(passport.isPresent());
    assertEquals(studentNew.getName(), passport.get().getStudent().getName());
    LOG.info(passport.get().getStudent().toString());
  }

  /***
   * O atributo courses não foi inicializado ao fazer o select do Student porque o
   * fetch type do atributo na entidade é LAZY por default. Portanto, ele é feito
   * forçando a inicialização do proxy do hibernate, pois apenas rodando o getter
   * não é recuperado os dados, acredito que por ser uma collection (lista).
   */
  @Test
  void should_getCourses_has_hibernateInitialization_in_find_method()
      throws NoSuchFieldException, SecurityException {
    Student student = studentRepo.findByIdWithCourses(20001L);

    assertThat(student).isNotNull();
    assertThat(student.getName()).isNotNull();
    assertEquals(3, student.getCourses().size());

    LOG.info(student.getCourses().toString());
  }

  /***
   * O atributo courses não foi inicializado ao fazer o select do Student porque o
   * fetch type do atributo na entidade é LAZY por default. Portanto, irá gerar
   * exception de collection, pois não estamos forçando a inicialização do proxy e
   * nem adicionando a @Transactional neste método, apesar de o find rodar numa
   * transação. Resultando deve ser oposto ao método
   * should_getCourses_has_hibernateInitialization_in_find_method.
   */
  @Test
  void should_not_getCourses_has_no_hibernateInitialization_in_find_method()
      throws NoSuchFieldException, SecurityException {
    Student student = studentRepo.findByIdWithCourses(20001L, false);

    assertThat(student).isNotNull();
    assertThat(student.getName()).isNotNull();
    assertThrows(
        LazyInitializationException.class,
        () -> {
          assertEquals(3, student.getCourses().size());
        });
  }

  @Test
  @DirtiesContext
  void should_save_new_student_and_courses() throws NoSuchFieldException, SecurityException {
    Student studentNew = StudentFactory.newBuiler().getSample();
    List<Course> courses = new ArrayList<>();

    IntStream.range(0, 9)
        .forEach(
            m -> {
              courses.add(CourseFactory.newBuiler().getSample());
            });
    studentRepo.saveStudentAndCourses(studentNew, courses);

    Student student = studentRepo.findByNameRetrievePassport(studentNew.getName());
    assertThat(student).isNotNull();

    student.setCourses(studentRepo.findAllStudentCourses(student.getId()));
    assertThat(student.getCourses().size()).isEqualTo(9);
    LOG.info(student.getCourses().toString());
  }

  @Test
  @DirtiesContext
  void should_update_student_and_save_courses() throws NoSuchFieldException, SecurityException {
    Student studentNew = StudentFactory.newBuiler().getSample();
    List<Course> courses = new ArrayList<>();

    studentNew.setId(studentRepo.save(studentNew));
    assertThat(studentNew.getId()).isGreaterThan(0);

    String newName = "JHONNY NOVO NOME";
    studentNew.setName(newName);
    IntStream.range(0, 9)
        .forEach(
            m -> {
              courses.add(CourseFactory.newBuiler().getSample());
            });

    studentRepo.saveStudentAndCourses(studentNew, courses);

    Student student = studentRepo.findByNameRetrievePassport(studentNew.getName());
    assertThat(student).isNotNull();
    assertEquals(newName, student.getName());

    student.setCourses(studentRepo.findAllStudentCourses(student.getId()));
    assertThat(student.getCourses().size()).isEqualTo(9);
    LOG.info(student.getCourses().toString());
  }

  @Test
  void should_get_all_students_with_a_such_passport_pattern() {
    List<Student> students = studentRepo.findAllStudentsByPassport("1234");
    assertEquals(3, students.size());
    LOG.info(students.toString());
  }
}
