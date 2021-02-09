package br.blog.smarti.jpahibernate.repositories.impl;

import br.blog.smarti.jpahibernate.entities.Review;
import br.blog.smarti.jpahibernate.repositories.ReviewRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class ReviewRepositoryImpl implements ReviewRepository {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired EntityManager em;

  public List<Review> findAllByCourseId(Long courseId) {
    LOG.info("find all reviews by course id: {}", courseId);
    TypedQuery<Review> query = em.createQuery("from Review r where r.id = :courseId", Review.class);
    query.setParameter("courseId", courseId);
    return query.getResultList();
  }

  public Review deleteById(Long reviewId) {
    LOG.info("delete review by id: {}", reviewId);
    Review r = em.find(Review.class, reviewId);
    em.remove(r);
    return r;
  }

  /***
   * Não faz muito sentido fazer desta forma. Ver: deleteAllByCourseId2.
   * Using NATIVE QUERY
   */
  public List<Review> deleteAllByCourseId(Long courseId) {
    LOG.info("delete reviews by course id: {}", courseId);
    StringBuilder sql = new StringBuilder();

    sql.append("select * ");
    sql.append("from review ");
    sql.append("where course_id = :courseId");

    Query query = em.createNativeQuery(sql.toString(), Review.class);
    List<Review> reviews = query.setParameter("courseId", courseId).getResultList();
    reviews.forEach(r -> em.remove(r));
    return reviews;
  }

  /***
   * Using NATIVE QUERY
   */
  public void deleteAllByCourseId2(Long courseId) {
    LOG.info("delete reviews by course id: {}", courseId);
    StringBuilder sql = new StringBuilder();

    sql.append("delete from review ");
    sql.append("where course_id = :courseId");

    em.createNativeQuery(sql.toString(), Review.class)
        .setParameter("courseId", courseId)
        .executeUpdate();
  }
}
