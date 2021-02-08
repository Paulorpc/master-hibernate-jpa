package br.blog.smarti.jpahibernate.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.blog.smarti.jpahibernate.entities.Passport;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

@SpringBootTest
public class PassportRepositoryTest {

  private Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired PassportRepository passportRepo;

  @Test
  void should_find_passports_containing_number_sorted_by_number_desc() {
    Sort descNumber = Sort.by(Sort.Direction.DESC, "number");
    List<Passport> passports = passportRepo.findAllByNumberContaining("123", descNumber);
    assertThat(passports).isNotNull();
    assertEquals(40002L, passports.get(0).getId());
    LOG.info(passports.toString());
  }

  @Test
  void should_find_passports_containing_number_sorted_by_id() {
    Sort ascId = Sort.by(Sort.Direction.ASC, "id");
    List<Passport> passports = passportRepo.findAllByNumberContaining("123", ascId);
    assertThat(passports).isNotNull();
    assertEquals(40001L, passports.get(0).getId());
    LOG.info(passports.toString());
  }
}
