package br.blog.smarti.jpahibernate.repositories;

import br.blog.smarti.jpahibernate.entities.Review;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class ReviewRepository {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired EntityManager em;

  public List<Review> findAllByCourseId(Long courseId) {
    LOG.info("find all reviews by course id: " + courseId);

    Query query = em.createQuery("from Review r where r.id = :courseId");
    query.setParameter("courseId", courseId);
    List<Review> reviews = query.getResultList();

    return reviews;
  }

  public Review deleteById(Long reviewId) {
    LOG.info("delete review by id: " + reviewId);
    Review r = em.find(Review.class, reviewId);
    em.remove(r);
    return r;
  }

  public List<Review> deleteAllByCourseId(Long courseId) {
    LOG.info("delete reviews by course id: " + courseId);
    StringBuilder sql = new StringBuilder();

    sql.append("select * ");
    sql.append("from review ");
    sql.append("where course_id = ");
    sql.append(courseId);

    List<Review> reviews = em.createNativeQuery(sql.toString(), Review.class).getResultList();
    reviews.forEach(r -> em.remove(r));
    return reviews;
  }
}
