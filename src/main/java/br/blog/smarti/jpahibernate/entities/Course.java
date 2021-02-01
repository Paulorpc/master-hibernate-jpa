package br.blog.smarti.jpahibernate.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Builder;

@NamedQueries({
	@NamedQuery(
			name = "getAllCourses", 
			query = "SELECT c FROM Course c"),
	
	@NamedQuery(
			name = "getCourseById", 
			query = "SELECT c FROM Course c WHERE c.id = :id")
})


@Entity
@Table(name = "course")
public class Course {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	private String professor;

	@UpdateTimestamp
	private LocalDateTime updatedDate;

	@CreationTimestamp
	private LocalDateTime createdDate;

	public Course() {
	}

	public Course(String name) {
		super();
		this.name = name;
	}

	@Builder
	public Course(Long id, String name, String professor) {
		super();
		this.id = id;
		this.name = name;
		this.professor = professor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfessor() {
		return professor;
	}

	public void setProfessor(String professor) {
		this.professor = professor;
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + ", professor=" + professor + ", updatedDate=" + updatedDate
				+ ", createdDate=" + createdDate + "]";
	}
	
}
