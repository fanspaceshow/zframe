package org.zframework.orm.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import com.nethsoft.zhxq.core.util.ObjectUtil;

/**
 * 分页Bean
 * 
 * @author ZENGCHAO
 * 
 */
public class PageBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4462058285749649947L;
	private int totalCount = 0;// 总记录数
	private int pageCount = 0;// 总页数
	private int rows = 20;// 分页大小
	private int page = 1;// 当前页数
	private boolean isFirstPage = false;// 是否为第一页
	private boolean isEndPage = false;// 是否为最后一页
	private String sort;
	private String order;
	private List<Criterion> criterions = new ArrayList<Criterion>();
	private List<Order> orders = new ArrayList<Order>();
	// 只在使用HQL语句查询时才会生效
	private Object[] params = null;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageCount() {
		if (totalCount % rows == 0)
			pageCount = totalCount / rows;
		else
			pageCount = (totalCount / rows) + 1;
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * 分页大小
	 * 
	 * @return
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * 分页大小
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isFirstPage() {
		if (page == 1 || page == 0)
			isFirstPage = true;
		return isFirstPage;
	}

	public void setFirstPage(boolean isFirstPage) {
		this.isFirstPage = isFirstPage;
	}

	public boolean isEndPage() {
		if (page >= pageCount)
			isEndPage = true;
		return isEndPage;
	}

	public void setEndPage(boolean isEndPage) {
		this.isEndPage = isEndPage;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Order getOrderBy() {
		if (sort == null || sort.trim().length() == 0 || order == null || order.trim().length() == 0)
			return null;
		else
			return this.order.trim().toLowerCase().equals("desc") ? Order.desc(sort) : Order.asc(sort);
	}

	/**
	 * 添加查询条件
	 * 
	 * @param criterion
	 */
	public void addCriterion(Criterion criterion) {
		this.criterions.add(criterion);
	}

	public List<Criterion> getCriterions() {
		return criterions;
	}

	/**
	 * 获取查询条件数组
	 * 
	 * @return
	 */
	public Criterion[] getCriterionsArray() {
		Criterion[] array = new Criterion[this.criterions.size()];
		for (int i = 0; i < criterions.size(); i++) {
			array[i] = criterions.get(i);
		}
		return array;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
	}

	public List<Order> getOrders() {
		return this.orders;
	}

	/**
	 * 获取排序条件数组
	 * 
	 * @return
	 */
	public Order[] getOrdersArray() {
		Order[] array = new Order[this.orders.size()];
		for (int i = 0; i < orders.size(); i++) {
			array[i] = orders.get(i);
		}
		return array;
	}

	/**
	 * 获取HQL语句参数列表 只在使用HQL语句查询时才会生效
	 * 
	 * @return
	 */
	public Object[] getParams() {
		return params;
	}

	/**
	 * 设置HQL语句参数列表 只在使用HQL语句查询时才会生效
	 * 
	 * @return
	 */
	public void setParams(Object... params) {
		this.params = params;
	}

	/**
	 * 增加HQL参数
	 * 
	 * @param param
	 */
	public void addParam(Object param) {
		int len = 0;
		if(ObjectUtil.isNotNull(this.params))
			len = this.params.length;
		Object[] p = new Object[len + 1];
		for (int i = 0; i < len; i++)
			p[i] = params[i];
		p[len] = param;
		this.params = p;
	}
}
