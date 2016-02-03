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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="sys_unittype")
public class UnitType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6431237310853824807L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_sys_unittype")
	@SequenceGenerator(name="seq_sys_unittype",sequenceName="seq_sys_unittype")
	private int id;
	@Column
	@NotNull
	@NotEmpty
	@Length(min=1,max=20)
	private String name;
	@Column
	private String descript;										  
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="unitType")
	private List<Unit> units = new ArrayList<Unit>();
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
	public List<Unit> getUnits() {
		return units;
	}
	public void setUnits(List<Unit> units) {
		this.units = units;
	}
}
