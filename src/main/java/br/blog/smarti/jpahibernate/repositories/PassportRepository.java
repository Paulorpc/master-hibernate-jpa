package br.blog.smarti.jpahibernate.repositories;

import br.blog.smarti.jpahibernate.entities.Passport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/***
 * REPOSITORY EXTENDENDO JpaRepository QUE JÁ IMPLEMENTA TODOS MÉTODOS BÁSICOS
 * ALÉM DE DAR SUPORTE PARA CRIAÇÃO DE QUERIES DE FORMA SIMPLES ATRAVÉS DO
 * PRÓPRIO NOME DO MÉTODO, POR EXEMPLO: findAllByNumberContaining(). TAMBÉM FOI
 * EXTENDIDO UMA INTERFACE PERSONALIZADA PARA CRIAÇÃO DE QUERIES MAIS COMPLEXAS
 * DE FORMA QUE SUA IMPLEMENTAÇÃO FIQUE ISOLADA, AINDA ASSIM, TODOS OS MÉTODOS
 * FICAM ATRELADOS A MESMA INTERFACE PassportRepository.
 */

@Repository
public interface PassportRepository
    extends JpaRepository<Passport, Long>, PassportRepositoryCustom {

  Optional<Passport> findByNumber(String number);

  List<Passport> findAllByNumberContaining(String number, Sort sort);
}
