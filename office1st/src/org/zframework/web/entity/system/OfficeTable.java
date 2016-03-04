package org.zframework.web.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;



/**
 * 
 * @author tianming.fan
 *
 */
/**
 * hibernate 的字段名建议用以下两种写法
 * 1.驼峰命名法
 * 2.全部小写
 * 不要有下划线，首字母最好不要大写。
 * US_goodsName XXXXX   不能这么写
 */
@Entity
@Table(name="sys_dmg_officedepot")
public class OfficeTable {
	private Integer id;
	private String goodsname;
	private Integer warehouseamount;
	private String units;
	private String warehousename;
	private String supplier;
	private String types;
	private String pictures;
	private String thewarehousepeople;
	private String thestoragetime;
	private String lastborrower;
	private String lastborrowtime;
	private String remarks;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_dmg_officedepot")
	@SequenceGenerator(name = "seq_sys_dmg_officedepot", sequenceName = "seq_sys_dmg_officedepot")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="GOODSNAME",nullable=false,length=50)
	public String getGoodsname() {
		return goodsname;
	}
	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}
	@Column(name="WAREHOUSEAMOUNT",nullable=false,length=50)
	public Integer getWarehouseamount() {
		return warehouseamount;
	}
	public void setWarehouseamount(Integer warehouseamount) {
		this.warehouseamount = warehouseamount;
	}
	@Column(name="UNITS",nullable=false,length=50)
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	@Column(name="warehousename",nullable=false,length=50)
	public String getWarehousename() {
		return warehousename;
	}
	public void setWarehousename(String warehousename) {
		this.warehousename = warehousename;
	}
	@Column(name="supplier",nullable=false,length=50)
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	@Column(name="types",nullable=false,length=50)
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	@Column(name="PICTURES",nullable=false,length=50)
	public String getPictures() {
		return pictures;
	}
	public void setPictures(String pictures) {
		this.pictures = pictures;
	}
	@Column(name="THEWAREHOUSEPEOPLE",nullable=false,length=50)
	public String getThewarehousepeople() {
		return thewarehousepeople;
	}
	public void setThewarehousepeople(String thewarehousepeople) {
		this.thewarehousepeople = thewarehousepeople;
	}
	@Column(name="THESTORAGETIME",nullable=false,length=50)
	public String getThestoragetime() {
		return thestoragetime;
	}
	public void setThestoragetime(String thestoragetime) {
		this.thestoragetime = thestoragetime;
	}
	@Column(name="LASTBORROWER",nullable=false,length=50)
	public String getLastborrower() {
		return lastborrower;
	}
	public void setLastborrower(String lastborrower) {
		this.lastborrower = lastborrower;
	}
	@Column(name="LASTBORROWTIME",nullable=false,length=50)
	public String getLastborrowtime() {
		return lastborrowtime;
	}
	public void setLastborrowtime(String lastborrowtime) {
		this.lastborrowtime = lastborrowtime;
	}
	@Column(name="REMARKS",nullable=false,length=50)
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}



/**
@Entity
@Table(name = "sys_officedepot")
public class OfficeTable {
@Id
@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_officedepot")
@SequenceGenerator(name = "seq_sys_officedepot", sequenceName = "seq_sys_officedepot")
private Integer id;
@Column
@NotBlank
@Length(min=0,max=50)
private String goodsName;
@Column
@NotNull
private Integer warehouseAmount;
@Column
@NotBlank
@Length(min=0,max=50)
private String units;
@Column
@NotBlank
@Length(min=0,max=50)
private String warehouseName;
@Column
@NotBlank
@Length(min=0,max=50)
private String supplier;
@Column
@NotBlank
@Length(min=0,max=50)
private String types;
@Column
@Length(min=0,max=50)
private String pictures;
@Column
@NotBlank
@Length(min=0,max=50)
private String theWarehousePeople;
@Column
@NotBlank
@Length(min=0,max=50)
private String theStorageTime;
@Column
@NotBlank
@Length(min=0,max=50)
private String lastBorrower;
@Column
@NotBlank
@Length(min=0,max=50)
private String lastBorrowTime;
@Column
@Length(min=0,max=50)
private String remarks;


public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
public String getGoodsName() {
	return goodsName;
}
public void setGoodsName(String goodsName) {
	this.goodsName = goodsName;
}
public Integer getWarehouseAmount() {
	return warehouseAmount;
}
public void setWarehouseAmount(Integer warehouseAmount) {
	this.warehouseAmount = warehouseAmount;
}
public String getUnits() {
	return units;
}
public void setUnits(String units) {
	this.units = units;
}
public String getWarehouseName() {
	return warehouseName;
}
public void setWarehouseName(String warehouseName) {
	this.warehouseName = warehouseName;
}
public String getSupplier() {
	return supplier;
}
public void setSupplier(String supplier) {
	this.supplier = supplier;
}
public String getTypes() {
	return types;
}
public void setTypes(String types) {
	this.types = types;
}
public String getPictures() {
	return pictures;
}
public void setPictures(String pictures) {
	this.pictures = pictures;
}
public String getTheWarehousePeople() {
	return theWarehousePeople;
}
public void setTheWarehousePeople(String theWarehousePeople) {
	this.theWarehousePeople = theWarehousePeople;
}
public String getTheStorageTime() {
	return theStorageTime;
}
public void setTheStorageTime(String theStorageTime) {
	this.theStorageTime = theStorageTime;
}
public String getLastBorrower() {
	return lastBorrower;
}
public void setLastBorrower(String lastBorrower) {
	this.lastBorrower = lastBorrower;
}
public String getLastBorrowTime() {
	return lastBorrowTime;
}
public void setLastBorrowTime(String lastBorrowTime) {
	this.lastBorrowTime = lastBorrowTime;
}
public String getRemarks() {
	return remarks;
}
public void setRemarks(String remarks) {
	this.remarks = remarks;
}
*/
}
