package br.blog.smarti.jpahibernate.repositories;

import java.lang.annotation.Annotation;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.blog.smarti.jpahibernate.entities.Student;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class StudentRepository {

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EntityManager em;

	public Student findById(Long id) {
		LOG.info("find student by id: {}", id);
		Student s = em.find(Student.class, id);
		return s;
	}

	public Student findByName(String name) {
		LOG.info("find student by name: {}", name);
		String sql = "select * from student where name = :name";
		return (Student) em.createNativeQuery(sql, Student.class).setParameter("name", name).getResultStream()
				.findFirst().orElse(null);
	}

	public Student findByNameWithPassport(String name) {
		LOG.info("find student with passoport by name: {}", name);
		Student s = this.findByName(name);
		s.setPassport(s.getPassport());
		return s;
	}

	public Long save(Student s) throws NoSuchFieldException, SecurityException {
		if (s.getId() == null) {
			LOG.info("saving student");

			if (isTransient("passport") && s.getPassport() != null) {
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

	public Student deleteById(Long id) {
		LOG.info("deleting student by id: " + id);
		Student s = findById(id);
		em.remove(s);
		return s;

	}

	/***
	 * Verifica se o atributo fieldName na classe é transient ou não. É transiente
	 * se não tiver annotations do tipo cascade persist ou all no objeto de
	 * relacionamento.
	 * 
	 * @param classe
	 * @param fieldName
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static boolean isTransient(String fieldName) throws NoSuchFieldException, SecurityException {
		boolean isTransient = true;
		Annotation[] annotations = Student.class.getDeclaredField(fieldName).getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof javax.persistence.OneToOne) {
				javax.persistence.OneToOne myAnnotation = (javax.persistence.OneToOne) annotation;

				try {
					if (myAnnotation.cascade()[0].equals(CascadeType.PERSIST)
							|| myAnnotation.cascade()[0].equals(CascadeType.ALL)) {
						isTransient = false;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}
		return isTransient;
	}
}
