package br.blog.smarti.jpahibernate.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.hibernate.LazyInitializationException;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import br.blog.smarti.jpahibernate.builders.StudentFactory;
import br.blog.smarti.jpahibernate.entities.Passport;
import br.blog.smarti.jpahibernate.entities.Student;

@SpringBootTest
@DirtiesContext
public class StudentRepositoryTest {

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StudentRepository studentRepo;

	@Autowired
	private PassportRepository passportRepo;

	@SuppressWarnings("deprecation")
	@Rule
	private ExpectedException exception = ExpectedException.none();

	/***
	 * Foi feito uma "brincadeira" no StudentRepository para verificar se o objeto
	 * filho passport (fk) é transiente ou não. Se ele for transiente, ou seja, não
	 * foi adicionado o cascade.PERSIST para fazer a persistencia dos objetos
	 * filhos, então é feita a persistência do objeto manualmente. Sendo assim,
	 * sendo transiente ou não nunca irá falhar ao persistir um Student mesmo com o
	 * objeto passport setado.
	 */
	@Test
	void saving_student_with_passport() throws NoSuchFieldException, SecurityException {
		Student studentNew = StudentFactory.newBuiler().getSample();
		studentRepo.save(studentNew);

		Student student = studentRepo.findByNameWithPassport(studentNew.getName());
		assertThat(student).isNotNull();
		assertThat(student.getPassport()).isNotNull();
	}

	/***
	 * É esperado exception ao fazer o student.getPassport(), pois é necessários
	 * estar numa transação para que o hibernate possa recuperar os dados do
	 * database automaticamente. Lembrando que o atributo passport não é populado ao
	 * fazer o select do Student porque o fetch type do atributo na entidade está
	 * configurado para LAZY
	 */
	@Test
	void should_not_getPassport_doesnt_have_transaction() throws NoSuchFieldException, SecurityException {
		Student studentNew = StudentFactory.newBuiler().getSample();
		studentRepo.save(studentNew);

		Student student = studentRepo.findByName(studentNew.getName());
		assertThat(student).isNotNull();
		assertEquals(studentNew.getName(), student.getName());

		exception.expect(LazyInitializationException.class);

		student.getPassport();
	}

	/***
	 * O atributo passport não foi inicializado ao fazer o select do Student porque
	 * o fetch type do atributo na entidade está configurado para LAZY. Portanto,
	 * ele é feito ao fazer a chamada do método .getStudent(), para isso é
	 * necessário a transação. Ao alterar para EAGER, fazendo a inicialização de um
	 * Student, também seria populado o atributo passport.
	 */
	@Test
	@Transactional
	void should_getPassport_have_transaction() throws NoSuchFieldException, SecurityException {
		Student studentNew = StudentFactory.newBuiler().getSample();
		studentRepo.save(studentNew);

		Student student = studentRepo.findByName(studentNew.getName());
		assertThat(student).isNotNull();
		assertEquals(studentNew.getName(), student.getName());

		Passport studentPassport = student.getPassport();
		assertThat(studentPassport).isNotNull();
		LOG.info(studentPassport.toString());
	}

	/***
	 * neste caso é recuperado os dados do passport de forma manual, no entanto não
	 * é necessário uma transação como no caso acima.
	 */
	@Test
	void should_getPassport_doesnt_have_transaction() throws NoSuchFieldException, SecurityException {
		Student studentNew = StudentFactory.newBuiler().getSample();
		studentRepo.save(studentNew);

		Student student = studentRepo.findByName(studentNew.getName());
		assertThat(student).isNotNull();
		assertEquals(studentNew.getName(), student.getName());

		Passport studentPassport = passportRepo.findByNumber(studentNew.getPassport().getNumber());
		student.setPassport(studentPassport);

		assertThat(student.getPassport()).isNotNull();
		LOG.info(studentPassport.toString());
	}

	/***
	 * Teste de pegar o Student no objeto passport pelo relacionamento bidirecional.
	 * o atributo student não foi inicializado ao fazer o select do passport porque
	 * o fetch type do atributo na entidade está configurado para LAZY. Para isso
	 * foi criado um método no repositório que está dentro de uma transação que após
	 * fazer o select do passport, faz o get do student e o seta no passport.
	 */
	@Test
	void should_getStudent_have_transaction_in_find_method() throws NoSuchFieldException, SecurityException {
		Student studentNew = StudentFactory.newBuiler().getSample();
		studentRepo.save(studentNew);

		Passport passport = passportRepo.findByNumberWithStudent(studentNew.getPassport().getNumber());
		assertThat(passport).isNotNull();
		assertThat(passport.getStudent()).isNotNull();

		LOG.info(passport.getStudent().toString());
	}

}
