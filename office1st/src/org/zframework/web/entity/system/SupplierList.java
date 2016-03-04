package org.zframework.web.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="sys_dmg_supplierlist")
public class SupplierList {
private Integer id;
private String suppliername;
private String addr;
private String personname;
private String phone;
private String email;
@Id
@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_dmg_supplierlist")
@SequenceGenerator(name = "seq_sys_dmg_supplierlist", sequenceName = "seq_sys_dmg_supplierlist")
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
@Column(name="suppliername",nullable=false,length=50)
public String getSuppliername() {
	return suppliername;
}
public void setSuppliername(String suppliername) {
	this.suppliername = suppliername;
}
@Column(name="addr",nullable=false,length=50)
public String getAddr() {
	return addr;
}
public void setAddr(String addr) {
	this.addr = addr;
}
@Column(name="personname",nullable=false,length=50)
public String getPersonname() {
	return personname;
}
public void setPersonname(String personname) {
	this.personname = personname;
}
@Column(name="phone",nullable=false,length=50)
public String getPhone() {
	return phone;
}
public void setPhone(String phone) {
	this.phone = phone;
}
@Column(name="email",nullable=false,length=50)
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}

}
