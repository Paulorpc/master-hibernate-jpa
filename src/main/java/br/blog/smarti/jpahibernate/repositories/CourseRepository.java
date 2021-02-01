package br.blog.smarti.jpahibernate.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.blog.smarti.jpahibernate.entities.Course;

@Repository
@Transactional
@SuppressWarnings("unchecked")
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
		return (Course) em.createNamedQuery("getCourseById")
				   		  .setParameter("id", id)
				  		  .getResultStream()
				  		  .findFirst()
				  		  .orElse(null);
	}

	
	// using native query
	public Course findByName(String name) {
		LOG.info("find course by name: {}", name);
		String sql = "select * from course where name = :name";
		return (Course) em.createNativeQuery(sql, Course.class)
						  .setParameter("name", name)
						  .getResultStream()
						  .findFirst()
						  .orElse(null);
	}

	// using namedQuery
	public List<Course> findAll_NamedQuery() {
		LOG.info("find all courses using named query");
		return em.createNamedQuery("getAllCourses")
				 .getResultList();
	}

	public Long save(Course c) {
		if (c.getId() == null) {
			LOG.info("saving course");
			em.persist(c);
			
			// feito flush para forçar a persistência e retornar o ID. 
			em.flush();
		}
		else {
			LOG.info("updating course id: " + c.getId());
			em.merge(c);
		}
		return c.getId();
	}

	public Course deleteById(Long id) {
		LOG.info("deleting course by id: " +id);
		Course c = findById(id);
		em.remove(c);
		return c;

	}

	public Course playWithEntityManager() {
		Course c = new Course("Curso MODELO");
		em.persist(c);
		
		// o persist() faz o commit no banco ao final da transação, que neste caso está com o nome alterado.
		c.setName(c.getName() + ": ao fazer o setName() é persistido no banco se dentro da transação");
		
		Course courseNew = this.findById(c.getId());
		return courseNew;
	}
	
	public Course playWithEntityManager_forcingFlushDetach() {
		Course c = new Course("Curso MODELO");
		em.persist(c);
		
		// força persistencia no banco mesmo sem terminar a transação
		em.flush();
		
		// remove o rasteramento do objeto pelo EM, próximas alterações não devem ser persistidas ao finalizar a transação
		// o memso pode ser feito com o clear, porém ele remove o tracking de todos objetos do EM.
		em.detach(c);
		
		// o persist faz o commit no banco ao final da transação, que neste caso está com o nome alterado.
		c.setName(c.getName() + ": mesmo fazendo o setName(), não é persistido no banco");
		
		Course courseNew = this.findById(c.getId());
		return courseNew;
	}
	
	public Course playWithEntityManager_forcingRefresh() {
		Course c = new Course("Curso MODELO");
		em.persist(c);
		
		// força persistencia no banco mesmo sem terminar a transação
		em.flush();
		
		// o persist faz o commit no banco ao final da transação, que neste caso está com o nome alterado.
		c.setName(c.getName() + ": mesmo fazendo o setName(), não é persistido no banco");
		
		// Foi atualizado o estado do objeto mas não finalizou a transação fazendo o commit da alteração
		// como estamos fazendo o refresh do objeto, deve ser persistido o objeto original no final da transação. 
		em.refresh(c);
		
		Course courseNew = this.findById(c.getId());
		return courseNew;
	}
	
	public Course playWithEntityManager_forcingRefreshAfterFlush() {
		Course c = new Course("Curso MODELO");
		em.persist(c);
		
		// força persistencia no banco mesmo sem terminar a transação
		em.flush();
		
		// o persist faz o commit no banco ao final da transação, que neste caso está com o nome alterado.
		c.setName(c.getName() + ": fazendo o setName(), é persistido no banco");
		
		// forcando persistencia mesmo sem terminar a transação
		em.flush();
		
		// estamos fazendo o refresh do objeto, mas acamos de forçar a atualição dele no banco, portanto o select retorna o último
		// estado persistido que é o setname anterior.
		em.refresh(c);
		
		Course courseNew = this.findById(c.getId());
		return courseNew;
	}

}
