package org.zframework.web.entity.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="sys_dd")
public class DataDict implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 211354869513457218L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_dd")
	@SequenceGenerator(name = "seq_sys_dd", sequenceName = "seq_sys_dd")
	private int id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "typeid")
	private DataDictType dataDictType;
	@Column
	private String name;
	@Column
	@NotNull
	@NotEmpty
	private String value;
	@Column
	private String descript;
	@Column
	private int location;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DataDictType getDataDictType() {
		return dataDictType;
	}

	public void setDataDictType(DataDictType dataDictType) {
		this.dataDictType = dataDictType;
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

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}
	
	
}
