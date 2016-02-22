package org.zframework.orm.query;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;

public class QueryParams {
	private PageBean pageBean;
	private List<Criterion> criterions = new ArrayList<Criterion>();
	private List<Order> orders = new ArrayList<Order>();
	private List<Projection> projections = new ArrayList<Projection>();
	public PageBean getPageBean() {
		return pageBean;
	}
	public void setPageBean(PageBean pageBean) {
		this.pageBean = pageBean;
	}
	public List<Criterion> getCriterions() {
		return criterions;
	}
	public void addCriterion(Criterion criterion) {
		this.criterions.add(criterion);
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void addOrders(Order order) {
		this.orders.add(order);
	}
	public List<Projection> getProjections() {
		return projections;
	}
	public void addProjection(Projection projection) {
		this.projections.add(projection);
	}
	public void clearCriterions(){
		this.criterions.clear();
	}
	public void clearOrders(){
		this.orders.clear();
	}
	public void clearProjections(){
		this.projections.clear();
	}
	public void clearAll(){
		this.criterions.clear();
		this.orders.clear();
		this.projections.clear();
	}
}
