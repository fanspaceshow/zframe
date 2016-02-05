package org.zframework.web.entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "sys_dd_type")
public class DataDictType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2744477824926149782L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_sys_dd_type")
	@SequenceGenerator(name="seq_sys_dd_type",sequenceName="seq_sys_dd_type")
	private int id;
	@Column
	@NotNull
	@NotEmpty
	@Length(min=1,max=50)
	private String name;
	@Column
	private String descript;
	@Column
	private String dataType;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="dataDictType",orphanRemoval=true)
	@OrderBy("location asc")
	List<DataDict> datadicts = new ArrayList<DataDict>();
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

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public List<DataDict> getDatadicts() {
		return datadicts;
	}

	public void setDatadicts(List<DataDict> datadicts) {
		this.datadicts = datadicts;
	}
	
}
