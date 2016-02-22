package org.zframework.web.entity.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="sys_safeconfig")
public class SafeConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6339186932349547179L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_sys_safeConfig")
	@SequenceGenerator(name="seq_sys_safeConfig",sequenceName="seq_sys_safeConfig")
	private int id;
	@Column
	@NotNull
	@NotEmpty
	@Length(min=1,max=50)
	private String name;
	@Column
	@NotNull
	@NotEmpty
	@Length(min=1,max=200)
	private String value;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
