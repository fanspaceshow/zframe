package org.zframework.web.entity.system;

public class OfficeOut {
	private Integer id;
	private String goodsname;
	private Integer warehouseamount;
	private String lastborrower;
	private String units;
	private Integer borwarehouseamount;
	public String getLastborrower() {
		return lastborrower;
	}
	public void setLastborrower(String lastborrower) {
		this.lastborrower = lastborrower;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGoodsname() {
		return goodsname;
	}
	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}
	public Integer getWarehouseamount() {
		return warehouseamount;
	}
	public void setWarehouseamount(Integer warehouseamount) {
		this.warehouseamount = warehouseamount;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public Integer getBorwarehouseamount() {
		return borwarehouseamount;
	}
	public void setBorwarehouseamount(Integer borwarehouseamount) {
		this.borwarehouseamount = borwarehouseamount;
	}
	
}
