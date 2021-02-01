package br.blog.smarti.jpahibernate.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Builder;

@Entity
@Table(name = "passport")
public class Passport {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String number;

	// mappedBy indica o owner relationship, ou seja, o atributo da entidade que
	// receberá a FK na tabela.
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "passport")
	private Student student;

	@CreationTimestamp
	private LocalDateTime createdDate;

	@UpdateTimestamp
	private LocalDateTime updatedDate;

	public Passport() {
		super();
	}

	@Builder
	public Passport(Long id, String number) {
		super();
		this.id = id;
		this.number = number;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	@Override
	public String toString() {
		return "Passport [id=" + id + ", number=" + number + "]";
	}
}
