package br.blog.smarti.jpahibernate.repositories.impl;

import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Review;
import br.blog.smarti.jpahibernate.entities.Student;
import br.blog.smarti.jpahibernate.repositories.CourseRepository;
import br.blog.smarti.jpahibernate.repositories.StudentRepository;
import java.util.List;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(isolation = Isolation.READ_COMMITTED)
public class CourseRepositoryImpl implements CourseRepository {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired EntityManager em;

  @Autowired StudentRepository studentRepo;

  public List<Course> findAll() {
    return em.createQuery("from Course c", Course.class).getResultList();
  }

  public Course findById(Long id) {
    LOG.info("find course by id: {}", id);
    Course c = em.find(Course.class, id);
    return c;
  }

  public Course findByIdRetrieveStudents(Long id) {
    LOG.info("find course by id with students retrived: {}", id);
    Course c = em.find(Course.class, id);
    c.setStudents(c.getStudents());
    return c;
  }

  /***
   * FindAll using NAMED QUERY
   */
  public Course findById_NamedQuery(Long id) {
    LOG.info("find course by id using named query: {}", id);
    return (Course)
        em.createNamedQuery("getCourseById", Course.class)
            .setParameter("id", id)
            .getResultStream()
            .findFirst()
            .orElse(null);
  }

  public Course findByName(String name) {
    LOG.info("find course by name: {}", name);
    String sql = "from Course c where c.name = :name";
    return em.createQuery(sql, Course.class)
        .setParameter("name", name)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  /***
   * using namedQuery
   */
  public List<Course> findAll_NamedQuery() {
    LOG.info("find all courses using named query");
    return em.createNamedQuery("getAllCourses", Course.class).getResultList();
  }

  public Long save(Course c) {
    if (c.getId() == null) {
      LOG.info("saving course");

      em.persist(c);

      // feito flush para forçar a persistência e retornar o ID.
      em.flush();

      if (c.getReviews().size() > 0) {
        c.getReviews()
            .forEach(
                r -> {
                  /***
                   * ao modificar o curso que está dentro de uma transação irá dar erro de concorrência,
                   * além disso, não tem necessidade, pois não é retornardo o objeto curso com os reviews.
                   */
                  // c.addReview(r);
                  r.setCourse(c);
                  em.persist(r);
                });
        em.flush();
      }
    } else {
      LOG.info("updating course id: " + c.getId());
      em.merge(c);
    }
    return c.getId();
  }

  public Course deleteById(Long id) {
    LOG.info("deleting course by id: " + id);
    Course c = findById(id);
    em.remove(c);
    return c;
  }

  public Course playWithEntityManager() {
    Course c = new Course("Curso MODELO");
    em.persist(c);

    // o persist() faz o commit no banco ao final da transação, que neste caso está
    // com o nome alterado.
    c.setName(c.getName() + ": ao fazer o setName() é persistido no banco se dentro da transação");

    Course courseNew = this.findById(c.getId());
    return courseNew;
  }

  public Course playWithEntityManager_forcingFlushDetach() {
    Course c = new Course("Curso MODELO");
    em.persist(c);

    // força persistencia no banco mesmo sem terminar a transação
    em.flush();

    // remove o rasteramento do objeto pelo EM, próximas alterações não devem ser
    // persistidas ao finalizar a transação
    // o memso pode ser feito com o clear, porém ele remove o tracking de todos
    // objetos do EM.
    em.detach(c);

    // o persist faz o commit no banco ao final da transação, que neste caso está
    // com o nome alterado.
    c.setName(c.getName() + ": mesmo fazendo o setName(), não é persistido no banco");

    Course courseNew = this.findById(c.getId());
    return courseNew;
  }

  public Course playWithEntityManager_forcingRefresh() {
    Course c = new Course("Curso MODELO");
    em.persist(c);

    // força persistencia no banco mesmo sem terminar a transação
    em.flush();

    // o persist faz o commit no banco ao final da transação, que neste caso está
    // com o nome alterado.
    c.setName(c.getName() + ": mesmo fazendo o setName(), não é persistido no banco");

    // Foi atualizado o estado do objeto mas não finalizou a transação fazendo o
    // commit da alteração
    // como estamos fazendo o refresh do objeto, deve ser persistido o objeto
    // original no final da transação.
    em.refresh(c);

    Course courseNew = this.findById(c.getId());
    return courseNew;
  }

  public Course playWithEntityManager_forcingRefreshAfterFlush() {
    Course c = new Course("Curso MODELO");
    em.persist(c);

    // força persistencia no banco mesmo sem terminar a transação
    em.flush();

    // o persist faz o commit no banco ao final da transação, que neste caso está
    // com o nome alterado.
    c.setName(c.getName() + ": fazendo o setName(), é persistido no banco");

    // forcando persistencia mesmo sem terminar a transação
    em.flush();

    // estamos fazendo o refresh do objeto, mas acamos de forçar a atualição dele no
    // banco, portanto o select retorna o último
    // estado persistido que é o setname anterior.
    em.refresh(c);

    Course courseNew = this.findById(c.getId());
    return courseNew;
  }

  /***
   * Salva os reviews de um determinado curso. Método criado para persistir os revies com o curso_id
   * (fk), já que não foi definido o cascade, é um atributo transiente.
   */
  public void saveCourseReviews(Long courseId, List<Review> reviews) {
    Course c = this.findById(courseId);

    reviews.forEach(
        r -> {
          r.setCourse(c);
          em.persist(r);
        });
  }

  /***
   * SOLUÇÃO PARA PROBLEMA N+1
   *
   * Utilizar o JPQL com join fetch é talvez a melhor forma de recuperar os dados das instâncias que
   * estão configuradas como LAZY e evitar exception por no-session. Em termos de performance tb é
   * interesante, pois é feito um único select. Outra opção é o Entity GRaph.
   */
  public Course findCourseRetrieveReviews_JoinFetch(Long courseId) {
    LOG.info("Find course by id retrieving reviews (join fetch): {}", courseId);

    StringBuilder sql = new StringBuilder();
    sql.append("from Course cs ");
    sql.append("join fetch cs.reviews ");
    sql.append("where cs.id = :courseId ");

    return (Course)
        em.createQuery(sql.toString(), Course.class)
            .setParameter("courseId", courseId)
            .getResultStream()
            .findFirst()
            .orElse(null);
  }

  /***
   * SOLUÇÃO PARA PROBLEMA N+1
   *
   * Utilizar o EntityGraph é talvez a melhor forma de recuperar os dados das instâncias que estão
   * configuradas como LAZY e evitar exception por no-session. Em termos de performance tb é
   * interesante, pois é feito um único select. Outra opção é o Join Fetch.
   */
  public Course findCourseRetrieveReviews_EntityGraph(Long courseId) {
    LOG.info("Find course by id retrieving reviews (entity graph): {}", courseId);

    EntityGraph<Course> eg = em.createEntityGraph(Course.class);
    eg.addSubgraph("reviews");

    Course c =
        em.createQuery("from Course c where id = : id", Course.class)
            .setParameter("id", courseId)
            .setHint("javax.persistence.loadgraph", eg)
            .getSingleResult();

    return c;
  }

  /***
   * SOLUÇÃO PARA PROBLEMA N+1 (PARCIALMENTE)
   *
   * Utilizar o Hibernate.Initialization para recuperar os dados das instâncias que estão
   * configuradas como LAZY e evitar exception por no-session é simples. Em termos de performance tb
   * é interesante, pois é feito apenas dois selects, um ao carregar o objeto principal e novamente
   * ao carregar o objeto lazy como o initialization. Para resolver o problema de N+1 as melhores
   * soluções são Join Fetch e Entity Graph.
   */
  public Course findCourseRetrieveReviews_HibernateInitialization(Long courseId) {
    LOG.info("Find course by id retrieving reviews (hibernate.initialization): {}", courseId);

    Course c = this.findById(courseId);
    Hibernate.initialize(c.getReviews());
    c.setReviews(c.getReviews());

    return c;
  }

  /***
   * Find all courses that there is no students matriulated. Using JPQL to make easier the query
   * structure.
   */
  public List<Course> findAllCoursesWithoutStudents() {
    LOG.info("Find all courses without students");
    return em.createQuery("from Course c where c.students is empty", Course.class).getResultList();
  }

  /***
   * Find all courses with more than N students joined. Using JPQL to make easier the query
   * structure.
   */
  public List<Course> findAllCoursesWithMoreThanStudents(int n) {
    LOG.info("Find all courses with more than {} students", n);
    return em.createQuery("from Course c where size(c.students) > :n", Course.class)
        .setParameter("n", n)
        .getResultList();
  }

  /***
   * Find all courses with more than N students joined. Using JPQL to make easier the query
   * structure.
   */
  public List<Course> findAllCoursesOrderedByStudents() {
    LOG.info("Find all courses ordered by students");
    return em.createQuery("from Course c order by size(c.students)", Course.class).getResultList();
  }

  /***
   * Find all students associated in a course. Student list is transient, so using initialize
   * retrieve data from hibernate.
   */
  public List<Student> findAllStudentsByCourseId(Long courseId) {
    LOG.info("Find all students by course id: {}", courseId);
    Course course = this.findById(courseId);
    Hibernate.initialize(course.getStudents());
    return course.getStudents();
  }

  /***
   * Find all courses with students retrived. Using JPQL to make easier the query structure.
   */
  public List<Course> findAllRetrieveStudents() {
    LOG.info("Find all courses with students retrived");
    return em.createQuery("from Course c left join fetch c.students ", Course.class)
        .getResultList();
  }

  public Long saveCourseAndStudents(Course c, List<Student> students) throws Exception {
    students.forEach(
        s -> {
          c.addStudent(s);
          s.addCourse(c);

          try {
            studentRepo.save(s);
          } catch (NoSuchFieldException | SecurityException e) {
            c.removeStudent(s);
            LOG.error("Erro ao gravar student em curso. {}, {}", s.toString(), c.toString());
            e.printStackTrace();
          }
        });
    return this.save(c);
  }
}
