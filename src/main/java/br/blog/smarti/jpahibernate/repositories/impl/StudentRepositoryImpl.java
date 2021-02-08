package br.blog.smarti.jpahibernate.repositories.impl;

import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Student;
import br.blog.smarti.jpahibernate.repositories.StudentRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class StudentRepositoryImpl implements StudentRepository {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired EntityManager em;

  public Student findById(Long id) {
    LOG.info("find student by id: {}", id);
    Student s = em.find(Student.class, id);
    return s;
  }

  public Student findByName(String name) {
    LOG.info("find student by name: {}", name);
    String sql = "from Student s where s.name = :name";
    return em.createQuery(sql, Student.class)
        .setParameter("name", name)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public Student findByNameRetrievePassport(String name) {
    LOG.info("find student by name retrieving passport: {}", name);
    Student s = this.findByName(name);
    Hibernate.initialize(s.getPassport());
    s.setPassport(s.getPassport());
    return s;
  }

  public Student findByIdWithCourses(Long id) {
    return this.findByIdWithCourses(id, true);
  }

  public Student findByIdWithCourses(Long id, boolean hibernateInitialize) {
    LOG.info("find student by id with courses by name: {}", id);
    Student s = this.findById(id);

    /***
     * Este comando força a inicialização do proxy do hibernate para recuperar os
     * dados. Se não utilizar este método, mesmo rodando o getter abaixo, não é
     * recuperado os dados por ser uma collection e irá dar um exception de no
     * session: failed to lazily initialize a collection.
     */
    if (hibernateInitialize) Hibernate.initialize(s.getCourses());

    s.setCourses(s.getCourses());
    return s;
  }

  public Long save(Student s) throws NoSuchFieldException, SecurityException {
    if (s.getId() == null) {
      LOG.info("saving student");

      if (StudentRepository.isTransient("passport") && s.getPassport() != null) {
        LOG.info("saving student passport (transient)");
        em.persist(s.getPassport());
      }

      em.persist(s);
      em.flush();
    } else {
      LOG.info("updating student id: " + s.getId());
      em.merge(s);
    }
    return s.getId();
  }

  public Student saveStudentAndCourse(Student s, Course c) {
    s.addCourse(c);
    c.addStudent(s);

    em.persist(s);
    em.persist(c);

    return s;
  }

  public Long saveStudentAndCourses(Student s, List<Course> courses)
      throws NoSuchFieldException, SecurityException {
    courses.forEach(
        c -> {
          s.addCourse(c);
          c.addStudent(s);
          em.persist(c);
        });
    return this.save(s);
  }

  public Student deleteById(Long id) {
    LOG.info("deleting student by id: " + id);
    Student s = findById(id);
    em.remove(s);
    return s;
  }

  /***
   * Course é transiente, fetch type LAZY, usado estratégia do
   * Hibernate.initialize() para forçar a inicialização do proxy e recuperar os
   * dados dos cursos do student.
   */
  public List<Course> findAllStudentCourses(Long studentId) {
    LOG.info("find all student's courses by student id: " + studentId);
    Student student = this.findById(studentId);
    Hibernate.initialize(student.getCourses());
    return student.getCourses();
  }

  /***
   * Retorna todos students com determinado passport (LIKE). Using JPQL to make easier
   * the query structure.
   */
  public List<Student> findAllStudentsByPassport(String passport) {
    LOG.info("find all students with a such passport pattern using like: " + passport);
    return em.createQuery(
            "from Student s where s.passport.number like '%" + passport + "%'", Student.class)
        .getResultList();
  }
}
