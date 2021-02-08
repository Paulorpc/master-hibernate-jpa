package br.blog.smarti.jpahibernate.repositories;

import br.blog.smarti.jpahibernate.entities.Review;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository {

  public List<Review> findAllByCourseId(Long courseId);

  public Review deleteById(Long reviewId);

  public List<Review> deleteAllByCourseId(Long courseId);
}
