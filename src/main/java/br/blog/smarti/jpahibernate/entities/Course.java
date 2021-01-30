package br.blog.smarti.jpahibernate.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;

@Entity
@Table(name="course")
@Builder
public class Course {
	
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	
	public Course() {}
	
	public Course(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public Course(String name) {
		super();
		this.name = name;
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

	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + "]";
	}
	
	
	
}
