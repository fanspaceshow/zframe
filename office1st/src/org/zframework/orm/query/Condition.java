package org.zframework.orm.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * 查询条件类
 * @author zenghao
 *
 */
public class Condition {
	private List<Criterion> criterions = new ArrayList<Criterion>();//查询条件
	private List<Order> orders = new ArrayList<Order>();//排序
	
	/**
	 * 等于
	 * @param name
	 * @param value
	 * @return
	 */
	public Condition eq(String property ,Object value){
		this.criterions.add(Restrictions.eq(property, value));
		return this;
	}
	
	/**
	 * ID等于
	 * @param value
	 * @return
	 */
	public Condition idEq(Object value){
		this.criterions.add(Restrictions.idEq(value));
		return this;
	}
	
	/**
	 * 不等于 <>
	 * @param property
	 * @param value
	 * @return
	 */
	public Condition ne(String property ,Object value){
		this.criterions.add(Restrictions.ne(property, value));
		return this;
	}
	
	/**
	 * 不等于
	 * @param not
	 * @return
	 */
	public Condition not(Criterion not){
		this.criterions.add(Restrictions.not(not));
		return this;
	}
	
	/**
	 * 大于 >
	 * @param property
	 * @param value
	 * @return
	 */
	public Condition gt(String property ,Object value){
		this.criterions.add(Restrictions.gt(property, value));
		return this;
	}
	
	/**
	 * 大于等于 >=
	 * @param property
	 * @param value
	 * @return
	 */
	public Condition ge(String property ,Object value){
		this.criterions.add(Restrictions.ge(property, value));
		return this;
	}
	
	/**
	 * 小于 <
	 * @param property
	 * @param value
	 * @return
	 */
	public Condition lt(String property ,Object value){
		this.criterions.add(Restrictions.lt(property, value));
		return this;
	}
	
	/**
	 * 小于等于 <=
	 * @param property
	 * @param value
	 * @return
	 */
	public Condition le(String property ,Object value){
		this.criterions.add(Restrictions.le(property, value));
		return this;
	}
	
	/**
	 * 为空
	 * @param property
	 * @return
	 */
	public Condition isNull(String property){
		this.criterions.add(Restrictions.isNull(property));
		return this;
	}
	
	/**
	 * 不为空
	 * @param property
	 * @return
	 */
	public Condition isNotNull(String property){
		this.criterions.add(Restrictions.isNotNull(property));
		return this;
	}
	/**
	 * 为空
	 * @param property
	 * @return
	 */
	public Condition isEmpty(String property){
		this.criterions.add(Restrictions.isEmpty(property));
		return this;
	}
	
	/**
	 * 不为空
	 * @param property
	 * @return
	 */
	public Condition isNotEmpty(String property){
		this.criterions.add(Restrictions.isNotEmpty(property));
		return this;
	}
	
	/**
	 * 相似
	 * @param property
	 * @param value
	 * @param 匹配方式 MatchMode.ANYWHERE 全匹配 MatchMode.START开始 MatchMode.END 结束 MatchMode.EXACT精确匹配
	 * @return
	 */
	public Condition like(String property, String value, MatchMode mode){
		this.criterions.add(Restrictions.like(property, value, mode));
		return this;
	}
	
	/**
	 * 逻辑和
	 * @param criterions
	 * @return
	 */
	public Condition and(Criterion...criterions){
		this.criterions.add(Restrictions.and(criterions));
		return this;
	}
	
	/**
	 * 逻辑或
	 * @param criterions
	 * @return
	 */
	public Condition or(Criterion...criterions){
		this.criterions.add(Restrictions.or(criterions));
		return this;
	}
	
	/**
	 * 等于列表中的某个值
	 * @param property
	 * @param values
	 * @return
	 */
	public Condition in(String property, Object...values){
		this.criterions.add(Restrictions.in(property, values));
		return this;
	}
	
	/**
	 * 等于列表中的某个值
	 * @param property
	 * @param values
	 * @return
	 */
	public Condition in(String property, Collection<?> values){
		this.criterions.add(Restrictions.in(property, values));
		return this;
	}
	
	/**
	 * 不等于列表中的某个值
	 * @param property
	 * @param values
	 * @return
	 */
	public Condition notIn(String property, Object...values){
		this.criterions.add(Restrictions.not(Restrictions.in(property, values)));
		return this;
	}
	
	/**
	 * 不等于列表中的某个值
	 * @param property
	 * @param values
	 * @return
	 */
	public Condition notIn(String property, Collection<?> values){
		this.criterions.add(Restrictions.not(Restrictions.in(property, values)));
		return this;
	}
	
	/**
	 * 在两值之间
	 * @param property
	 * @param x
	 * @param y
	 * @return
	 */
	public Condition between(String property, Object x, Object y){
		this.criterions.add(Restrictions.between(property, x, y));
		return this;
	}
	
	/**
	 * 不在在两值之间
	 * @param property
	 * @param x
	 * @param y
	 * @return
	 */
	public Condition notBetween(String property, Object x, Object y){
		this.criterions.add(Restrictions.not(Restrictions.between(property, x, y)));
		return this;
	}
	
	/**
	 * 拼接sql语句查询
	 * @param sql
	 * @return
	 */
	public Condition sql(String sql){
		this.criterions.add(Restrictions.sqlRestriction(sql));
		return this;
	}
	
	/**
	 * 升序
	 * @param property
	 * @return
	 */
	public Condition asc(String property){
		this.orders.add(Order.asc(property));
		return this;
	}
	
	/**
	 * 降序
	 * @param property
	 * @return
	 */
	public Condition desc(String property){
		this.orders.add(Order.desc(property));
		return this;
	}

	public List<Criterion> getCriterions() {
		return criterions;
	}

	public List<Order> getOrders() {
		return orders;
	}
	
	/*=============静态方法=============*/
	public static Condition NEW(){
		return new Condition();
	}
}
