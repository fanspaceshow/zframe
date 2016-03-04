package org.zframework.web.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="sys_dmg_type")
public class Type {
	private Integer id;
	private String type;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_dmg_type")
	@SequenceGenerator(name = "seq_sys_dmg_type", sequenceName = "seq_sys_dmg_type")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="type",nullable=false,length=50)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
}
