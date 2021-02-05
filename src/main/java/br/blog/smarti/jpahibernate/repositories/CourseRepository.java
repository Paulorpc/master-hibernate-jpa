package br.blog.smarti.jpahibernate.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.blog.smarti.jpahibernate.entities.Course;
import br.blog.smarti.jpahibernate.entities.Review;

@Repository
@Transactional
public class CourseRepository {

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EntityManager em;

	public Course findById(Long id) {
		LOG.info("find course by id: {}", id);
		Course c = em.find(Course.class, id);
		return c;
	}

	// using namedQuery
	public Course findById_NamedQuery(Long id) {
		LOG.info("find course by id using named query: {}", id);
		return (Course) em.createNamedQuery("getCourseById").setParameter("id", id).getResultStream().findFirst()
				.orElse(null);
	}

	// using native query
	public Course findByName(String name) {
		LOG.info("find course by name: {}", name);
		String sql = "select * from course where name = :name";
		return (Course) em.createNativeQuery(sql, Course.class).setParameter("name", name).getResultStream().findFirst()
				.orElse(null);
	}

	// using namedQuery
	public List<Course> findAll_NamedQuery() {
		LOG.info("find all courses using named query");
		return em.createNamedQuery("getAllCourses").getResultList();
	}

	public Long save(Course c) {
		if (c.getId() == null) {
			LOG.info("saving course");

			em.persist(c);

			// feito flush para forçar a persistência e retornar o ID.
			em.flush();

			if (c.getReviews().size() > 0) {
				c.getReviews().forEach(r -> {
					/***
					 * ao modificar o curso que está dentro de uma transação irá dar erro de
					 * concorrência, além disso, não tem necessidade, pois não é retornardo o objeto
					 * curso com os reviews.
					 */
//					c.addReview(r);
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
	 * Salva os reviews de um determinado curso. Método criado para persistir os
	 * revies com o curso_id (fk), já que não foi definido o cascade, é um atributo
	 * transiente.
	 * 
	 * @param courseId
	 * @param reviews
	 * @return course com reviews
	 */
	public void saveCourseReviews(Long courseId, List<Review> reviews) {
		Course c = this.findById(courseId);
		
		reviews.forEach(r -> {
			r.setCourse(c);
			em.persist(r);
		});
	}

	/***
	 * Atributo transient, buscando review através do @Transactional. Utilizar o
	 * JPQL com join fetch é talvez a melhor forma de fazer o "getter" para
	 * recuperar os dados das instâncias que estão configuradas como tipo LAZY e
	 * evitar exception por no-session. Em temros de performance tb é interesante,
	 * pois é feito um único select. Outra opção é o Hibernate.initialization(), mas
	 * teste this.findCourseWithReviewsForcingHibernateInitialization() ele não
	 * funciounou, apesar de estar sendo usado para
	 * StudentRepository.findByIdWithCourses()
	 * 
	 * @param courseId
	 * @return course com reviews
	 */
	public Course findCourseWithReviews(Long courseId) {
		LOG.info("Find course with reviews by id: {}", courseId);

		StringBuilder sql = new StringBuilder();
		sql.append("from Course cs ");
		sql.append("join fetch cs.reviews ");
		sql.append("where cs.id = :courseId ");

		return (Course) em.createQuery(sql.toString(), Course.class).setParameter("courseId", courseId)
				.getResultStream().findFirst().orElse(null);
	}

	/***
	 * Atributo transient, buscando review através do @Transactional. O mesmo
	 * problema por LazyInitializationException para collection foi resolvido no
	 * StudentRepository.findByIdWithCourses() forçando a inicialização do proxy,
	 * mas por alguma razão aqui não funciona. Foi resolvido utilizando JOIN FETCH
	 * por JPQL em this.findCourseWithReviews()
	 * 
	 * @param courseId
	 * @return course com reviews
	 */
	public Course findCourseWithReviewsForcingHibernateInitialization(Long courseId) {
		LOG.info("Find course with reviews by id: {}", courseId);

		Course c = this.findById(courseId);
		Hibernate.initialize(c.getStudents());
		c.setStudents(c.getStudents());

		return c;
	}
	
}
