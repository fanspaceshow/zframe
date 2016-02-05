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
 * in 2016/02/04
 */
@Entity
@Table(name = "sys_company")
public class CompanyType {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_company")
	@SequenceGenerator(name = "seq_sys_company", sequenceName = "seq_sys_company")
	private Integer id;//id主键
	
	@Column
	@NotNull
	@NotEmpty
	private String company;//公司名

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	
}
