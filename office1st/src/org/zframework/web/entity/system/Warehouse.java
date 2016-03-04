package org.zframework.web.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="sys_dmg_warehousename")
public class Warehouse {
private Integer id;
private String warehousename;

@Id
@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_dmg_warehousename")
@SequenceGenerator(name = "seq_sys_dmg_warehousename", sequenceName = "seq_sys_dmg_warehousename")
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
@Column(name="warehousename",nullable=false,length=50)
public String getWarehousename() {
	return warehousename;
}
public void setWarehousename(String warehousename) {
	this.warehousename = warehousename;
}

}
