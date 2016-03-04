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
@Table(name="sys_com")
public class Common implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2298947588028720419L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,generator="seq_sys_com")
	@SequenceGenerator(name="seq_sys_com",sequenceName="seq_sys_com")
	private int id;
	@Column
	@NotNull
	@NotEmpty
	@Length(min=0,max=50)
	private String name;
	@Column
	private String value;
	@Column
	private String descrip;
	@Column
	private int type;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDescrip() {
		return descrip;
	}
	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}
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
