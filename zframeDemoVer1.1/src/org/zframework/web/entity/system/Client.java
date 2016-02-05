package org.zframework.web.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
/**
 * 
 * @author tianming.fan
 * in 2016/01/25
 */
@Entity
@Table(name = "sys_client")
public class Client {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_client")
	@SequenceGenerator(name = "seq_sys_client", sequenceName = "seq_sys_client")
	private Integer id;//项目id主键
	@Column
	@NotNull
	@NotEmpty
	private String name;//姓名
	@Column
	@NotNull
	@NotEmpty
	private String company;//公司
	@Column
	@NotNull
	private Integer age;//年龄
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
}
