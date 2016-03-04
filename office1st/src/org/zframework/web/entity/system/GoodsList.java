package org.zframework.web.entity.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="sys_dmg_goodslist")
public class GoodsList {
	private Integer id;
	private String goodsname;
	private String unit;
	private String type;
	private String remarks;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sys_dmg_goodslist")
	@SequenceGenerator(name = "seq_sys_dmg_goodslist", sequenceName = "seq_sys_dmg_goodslist")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="goodsname",nullable=false,length=50)
	public String getGoodsname() {
		return goodsname;
	}	
	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}
	@Column(name="unit",nullable=false,length=50)
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	@Column(name="type",nullable=false,length=50)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Column(name="remarks",nullable=false,length=50)
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
