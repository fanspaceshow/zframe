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
 * 项目类型的实体类
 * @author xinyun.hu
 *
 */
@Entity
@Table(name = "sys_projecttype")
public class Projecttype {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_projecttype")
	@SequenceGenerator(name = "seq_sys_projecttype", sequenceName = "seq_sys_projecttype")
	private Integer id;//项目类型id主键
	@Column
	@NotNull
	@NotEmpty
	private String projecttype;//项目类型
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProjecttype() {
		return projecttype;
	}
	public void setProjecttype(String projecttype) {
		this.projecttype = projecttype;
	}
}
