package org.zframework.web.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="sys_dmg_measureunits")
public class Measureunits {
	private Integer id;
	private String unit;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_dmg_measureunits")
	@SequenceGenerator(name = "seq_sys_dmg_measureunits", sequenceName = "seq_sys_dmg_measureunits")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="unit",nullable=false,length=50)
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}	
}
