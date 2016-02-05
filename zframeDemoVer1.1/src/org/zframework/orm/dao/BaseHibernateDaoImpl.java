package org.zframework.orm.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.ReflectUtil;
import org.zframework.orm.query.Condition;
import org.zframework.orm.query.PageBean;
import org.zframework.orm.query.QueryParams;

@SuppressWarnings("unchecked")
public class BaseHibernateDaoImpl extends BaseHibernateDao{
	@Autowired
	private SessionFactory sessionFactory;
	/**
	 * 获取Session对象
	 * @return
	 */
	public Session getSession(){
		//事务必须是开启的，否则获取不到
		return sessionFactory.getCurrentSession();
	}
	/**
	 * 获取查询对象Query
	 * @return
	 */
	public Query getQuery(String hql){
		Query query = getSession().createQuery(hql);
		//TODO 测试时关闭Query缓存
		//query.setCacheable(true);
		return query;
	}
	/**
	 * 获取查询对象SQLQuery
	 * @param sql
	 * @return
	 */
	public SQLQuery getSQLQuery(String sql){
		return getSession().createSQLQuery(sql);
	}
	/**
	 * 获取查询对象Criteria
	 * @return
	 */
	public Criteria getCriteria(Class<?> clazz){
		Criteria criteria = getSession().createCriteria(clazz);
		//TODO 测试时关闭Criteria缓存
		//criteria.setCacheable(true);
		return criteria;
	}
	/**
	 * 获取SessionFactory对象
	 * @return
	 */
	public SessionFactory getSessionFactory(){
		checkSessionFactory();
		return sessionFactory;
	}
	/**
	 * 检查sessionFactory对象是否为空
	 */
	private void checkSessionFactory(){
		if(sessionFactory == null){
			throw new NullPointerException("未注入SessionFactory，请检查配置文件!");
		}
	}
	/**
	 * 保存
	 * @param <M>
	 * @param 持久化对象
	 * @return 生成的ID
	 */
	public <M> Serializable save(M m){
		Serializable id = 0;
		id = getSession().save(m);
		return id;
	}
	/**
	 * 保存
	 * @param <M>
	 * @param 持久化对象
	 */
	public <M> void persist(M m) {
		getSession().persist(m);
	}
	/**
	 * 保存或者更新
	 * @param <M>
	 * @param 持久化对象
	 */
	public <M> void saveOrUpdate(M m) {
		getSession().saveOrUpdate(m);
	}
	/**
	 * 删除
	 * @param <M>
	 * @param m
	 */
	public <M> void delete(M m){
		getSession().delete(m);
	}
	/**
	 * 根据ID删除
	 * @param <M>
	 * @param id
	 * @param m
	 */
	public <M> void delete(Class<M> clazz,Serializable id){
		M m = (M) getSession().get(clazz, id);
		if(m!=null)
			getSession().delete(m);
	}
	/**
	 * 删除
	 * @param <M>
	 * @param 实体对象名称
	 * @param 实体对象
	 */
	public <M> void delete(String entityName, Object obj) {
		getSession().delete(entityName,obj);
	}
	/**
	 * 批量删除
	 */
	public <M> void deletes(Class<M> clazz, Serializable... ids) {
		Query query = this.getQuery("delete from "+clazz +" where id in ?");
		query.setParameter(0, ids);
		query.executeUpdate();
	}
	/**
	 * 批量删除
	 */
	public <M> void deletes(Class<M> clazz, Collection<Serializable> ids) {
		Query query = this.getQuery("delete from "+clazz +" where id in ?");
		query.setParameter(0, ids);
		query.executeUpdate();
	}
	/**
	 * 更新
	 * @param <M>
	 * @param m
	 */
	public <M> void update(M m){
		getSession().update(m);
	}
	/**
	 * 更新
	 * @param <M>
	 * @param m
	 */
	public <M> void update(String entityName,Object obj){
		getSession().update(entityName,obj);
	}
	/**
	 * 更新
	 * @param <M>
	 * @param m
	 */
	public <M> void merge(M m){
		getSession().merge(m);
	}
	/**
	 * 刷新,将对象与数据库同步
	 */
	public <M> void refresh(M m){
		getSession().refresh(m);
	}
	/**
	 * 执行批处理语句.如 之间insert, update, delete 等.
	 * @param hql语句
	 * @param 参数列表
	 * @return
	 */
	public int execteBulk(final String hql) {
		Query query = getQuery(hql);
		Object result = query.executeUpdate();
		return result == null ? 0 : ((Integer) result).intValue();
	}
	/**
	 * 执行批处理语句.如 之间insert, update, delete 等.
	 * @param hql语句
	 * @param 参数列表
	 * @return
	 */
    public int execteBulk(final String hql, final Object[] paramlist) {
        Query query = getQuery(hql);
        setParameters(query, paramlist);
        Object result = query.executeUpdate();
        return result == null ? 0 : ((Integer) result).intValue();
    }
    /**
     * 执行批处理语句.如 之间insert, update, delete 等.
     * @param SQL语句
     * @param 参数列表
     * @return 
     */
    public int execteNativeBulk(final String natvieSQL) {
    	Query query = getSQLQuery(natvieSQL);
    	Object result = query.executeUpdate();
    	return result == null ? 0 : ((Integer) result).intValue();
    }
    /**
     * 执行批处理语句.如 之间insert, update, delete 等.
     * @param SQL语句
     * @param 参数列表
     * @return 
     */
    public int execteNativeBulk(final String natvieSQL, final Object[] paramlist) {
        Query query = getSQLQuery(natvieSQL);
        setParameters(query, paramlist);
        Object result = query.executeUpdate();
        return result == null ? 0 : ((Integer) result).intValue();
    }
	/**
	 * 根据ID获取对象
	 * 先查询缓存，没有的话，在查询数据库，没有数据则返回null
	 * @param <M>
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <M> M get(Class<M> clazz,Serializable id){
		return (M) getSession().get(clazz, id);
	}
	/**
	 * 根据条件获取对象
	 * 如果有多条符合数据，则只返回首条数据
	 * @param <M>
	 * @param clazz
	 * @param criterions
	 * @return
	 */
	public <M> M getBy(Class<M> clazz,Criterion...criterions){
		Criteria criteria = getCriteria(clazz);
		for(Criterion criterion : criterions)
			criteria.add(criterion);
		return (M) criteria.setMaxResults(1).uniqueResult();
	}
	/**
	 * 查询单个实体
	 * @param clazz
	 * @param criterion
	 * @param order
	 * @return
	 */
	public <M> M getBy(Class<M> clazz,Criterion criterion,Order order){
		Criteria criteria = getCriteria(clazz);
		criteria.add(criterion);
		criteria.addOrder(order);
		return (M) criteria.setMaxResults(1).uniqueResult();
	}
	/**
	 * 查询单个实体
	 * @param clazz
	 * @param criterions
	 * @param orders
	 * @return
	 */
	public <M> M getBy(Class<M> clazz,Criterion[] criterions,Order[] orders){
		Criteria criteria = getCriteria(clazz);
		for(Criterion criterion : criterions)
			criteria.add(criterion);
		for(Order order : orders)
			criteria.addOrder(order);
		return (M) criteria.setMaxResults(1).uniqueResult();
	}
	/**
	 * 查询单个实体
	 */
	public <M> M getBy(Class<M> clazz, Condition condition) {
		Criteria criteria = getCriteria(clazz);
		for(Criterion criterion : condition.getCriterions())
			criteria.add(criterion);
		for(Order order : condition.getOrders())
			criteria.addOrder(order);
		return (M) criteria.setMaxResults(1).uniqueResult();
	}
	/**
	 * 根据HQL查询单个对象
	 * @param <M>
	 * @param clazz
	 * @param hql
	 * @return
	 */
	public <M> M getByHQL(Class<M> clazz,String hql, Object...params){
		Query query = getQuery(hql);
		setParameters(query, params);
		Object obj = query.setMaxResults(1).uniqueResult();
		if(ObjectUtil.isNull(obj))
			return null;
		if(!obj.getClass().equals(clazz)){
			String[] columns = hql.substring(hql.indexOf("select")+6,hql.indexOf("from")).split(",");
			if(obj instanceof Object[]){
				try {
					M m = clazz.newInstance();
					Object[] values = (Object[]) obj;
					for(int i=0;i<columns.length;i++){
						ReflectUtil.setFieldValue(m,columns[i],values[i]);
					}
					obj = m;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}else{
				try {
					M m = clazz.newInstance();
					ReflectUtil.setFieldValue(m, columns[0], obj);
					obj = m;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return (M) obj;
	}
	
	/**
	 * 查询单个实体
	 */
	public <M> M getByNativeSQL(Class<M> clazz, String sql, Object... params) {
		SQLQuery query = getSQLQuery(sql);
		query.addEntity(clazz);
		setParameters(query, params);
		return (M) query.setMaxResults(1).uniqueResult();
	}
	
	/**
	 * 根据SQL查询单个对象
	 * @param <M>
	 * @param clazz
	 * @param sql
	 * @return
	 */
	public <M> M getByNativeSQL(Class<M> clazz,String sql){
		SQLQuery query = getSQLQuery(sql);
		query.addEntity(clazz);
		return (M) query.setMaxResults(1).uniqueResult();
	}
	
	/**
	 * 根据ID获取对象
	 * 先查询一级缓存，不存在的话再查询二级缓存，不存在的话查询数据库，数据库中没有数据则抛异常
	 * @param <M>
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <M> M load(Class<M> clazz,Serializable id){
		return (M) getSession().load(clazz, id);
	}
	/**
	 * 获取全部
	 * @param <M>
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz){
		Criteria criteria = getCriteria(clazz);
		return criteria.list();
	}
	/**
	 * 分页获取数据
	 * @param <M>
	 * @param 分页Bean
	 */
	public <M> List<M> list(Class<M> clazz, PageBean pageBean) {
		Criteria criteria = getCriteria(clazz);
		if(pageBean.getPage() <= 1)
			criteria.setFirstResult(0);
		else
			criteria.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
		criteria.setMaxResults(pageBean.getRows());
		if(pageBean.getOrderBy()!=null){
			criteria.addOrder(pageBean.getOrderBy());
		}
		if(pageBean.getOrders().size()>0){
			for(Order order : pageBean.getOrders()){
				criteria.addOrder(order);
			}
		}
		if(pageBean.getCriterions().size()>0){
			for(Criterion criterion : pageBean.getCriterions()){
				criteria.add(criterion);
			}
		}
		return criteria.list();
	}
	/**
	 * 根据指定查询条件查询
	 * @param <M>
	 * @param 任意数量criterion
	 * @param clazz
	 * @return 对象集合
	 */
	public <M> List<M> list(Class<M> clazz,Criterion... criterions){
		Criteria criteria = getCriteria(clazz);
		for(Criterion criterion : criterions)
			criteria.add(criterion);
		return criteria.list();
	}
	/**
	 * 查询集合并排序
	 * @param <M>
	 * @param clazz
	 * @param orders 排序条件集合
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Order...orders){
		Criteria criteria = getCriteria(clazz);
		for(Order order : orders)
			criteria.addOrder(order);
		return criteria.list();
	}
	/**
	 * 根据指定查询条件查询，并排序
	 * @param <M>
	 * @param clazz
	 * @param criterion查询条件
	 * @param order 排序条件
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz, Criterion criterion, Order order){
		Criteria criteria = getCriteria(clazz);
		criteria.add(criterion);
		criteria.addOrder(order);
		return criteria.list();
	}
	/**
	 * 根据指定条件集合查询，并按照排序条件集合排序
	 * @param <M>
	 * @param clazz
	 * @param criterions 查询条件集合
	 * @param orders 排序条件集合
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,Criterion[] criterions,Order[] orders){
		Criteria criteria = getCriteria(clazz);
		for(Criterion criterion : criterions)
			criteria.add(criterion);
		for(Order order : orders)
			criteria.addOrder(order);
		return criteria.list();
	}
	/**
	 * 根据指定条件集合查询，并按照排序条件集合排序
	 * @param <M>
	 * @param clazz
	 * @param criterions 查询条件集合
	 * @param orders 排序条件集合
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz,List<Criterion> criterions,List<Order> orders){
		Criteria criteria = getCriteria(clazz);
		for(Criterion criterion : criterions)
			criteria.add(criterion);
		for(Order order : orders)
			criteria.addOrder(order);
		return criteria.list();
	}
	/**
	 * 根据HQL语句获取集合
	 * @param clazz
	 * @param hql
	 * @param params
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz, String hql, Object... params) {
		Query query = getQuery(hql);
		if(ObjectUtil.isNotNull(params)){
			setParameters(query, params);
		}
		return query.list();
	}
	/**
	 * 根据HQL语句获取集合
	 * @param clazz
	 * @param hql
	 * @param params
	 * @return
	 */
	public <M> List<M> list(Class<M> clazz, String hql, List<Object> params){
		Query query = getQuery(hql);
		int length = params.size();
		for(int i=0;i<length;i++){
			query.setParameter(i, params.get(i));
		}
		return query.list();
	}
	
	/**
	 * 根据HQL语句获取集合
	 */
	public <M> List<M> list(Class<M> clazz, String hql, PageBean pageBean, Object...params) {
		Query query = getQuery(hql);
		if(pageBean.getPage()<=1)
			query.setFirstResult(0);
		else
			query.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
		query.setMaxResults(pageBean.getRows());
		if(ObjectUtil.isNotNull(params))
			setParameters(query, params);
		return query.list();
	}
	
	/**
	 * 根据封装的查询类来查询结果
	 */
	public <M> List<M> list(Class<M> clazz,QueryParams queryParams){
		Criteria criteria = getCriteria(clazz);
		if(queryParams.getPageBean()!=null){
			PageBean pageBean = queryParams.getPageBean();
			if(pageBean.getPage() <= 1)
				criteria.setFirstResult(0);
			else
				criteria.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
			criteria.setMaxResults(pageBean.getRows());
			if(pageBean.getOrderBy()!=null){
				criteria.addOrder(pageBean.getOrderBy());
			}
			if(pageBean.getOrders().size()>0){
				for(Order order : pageBean.getOrders()){
					criteria.addOrder(order);
				}
			}
			if(pageBean.getCriterions().size()>0){
				for(Criterion criterion : pageBean.getCriterions()){
					criteria.add(criterion);
				}
			}
		}
		for(Criterion criterion : queryParams.getCriterions()){
			criteria.add(criterion);
		}
		for(Order order : queryParams.getOrders()){
			criteria.addOrder(order);
		}
		if(queryParams.getProjections().size() > 0){
			ProjectionList projList = Projections.projectionList();
			for(Projection proj : queryParams.getProjections()){
				projList.add(proj);
			}
			criteria.setProjection(projList);
		}
		return criteria.list();
	}
	/**
	 * 获取集合
	 * 仅获取对象中的某些字段
	 * 其余字段为null值
	 * @param clazz
	 * @param 需要查询字段
	 * @return
	 */
	public <M> List<M> listPartColumns(Class<M> clazz,String...columnNames){
		if(ObjectUtil.isNull(columnNames)) return list(clazz);
		List<M> result = null;
		StringBuffer column = new StringBuffer();
		for(String col : columnNames){
			column.append(","+col);
		}
		String hql = "SELECT "+column.substring(1)+" FROM "+clazz.getSimpleName();
		Query query = getQuery(hql);
		List<Object[]> list = query.list();
		if(list!=null){
			result = new ArrayList<M>();
			for(Object[] objs : list){
				//通过反射 设置对象的值
				try {
					M m = clazz.newInstance();
					for(int i=0;i<columnNames.length;i++){
						ReflectUtil.setFieldValue(m, columnNames[i], objs[i]);
					}
					result.add(m);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
			}
		}
		return result;
	}
	/**
	 * 获取集合 分页
	 * 仅获取对象中的某些字段
	 * 其余字段为null值
	 * @param clazz
	 * @param 需要查询字段
	 * @return
	 */
	public <M> List<M> listPartColumns(Class<M> clazz,PageBean pageBean,String...columnNames){
		if(ObjectUtil.isNull(columnNames)) return list(clazz, pageBean);
		List<M> result = null;
		StringBuffer column = new StringBuffer();
		for(String col : columnNames){
			column.append(","+col);
		}
		String hql = "SELECT "+column.substring(1)+" FROM "+clazz.getSimpleName();
		Query query = getQuery(hql);
		//设置分页数据
		if(pageBean.getPage()<=1)
			query.setFirstResult(0);
		else
			query.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
		query.setMaxResults(pageBean.getRows());
		List<Object[]> list = query.list();
		if(list!=null){
			result = new ArrayList<M>();
			for(Object[] objs : list){
				//通过反射 设置对象的值
				try {
					M m = clazz.newInstance();
					for(int i=0;i<columnNames.length;i++){
						ReflectUtil.setFieldValue(m, columnNames[i], objs[i]);
					}
					result.add(m);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
			}
		}
		return result;
	}
	/**
	 * 查询集合
	 */
	public <M> List<M> list(Class<M> clazz, Condition condition) {
		Criteria criteria = getCriteria(clazz);
		for(Order order : condition.getOrders()){
			criteria.addOrder(order);
		}
		for(Criterion criterion : condition.getCriterions()){
			criteria.add(criterion);
		}
		return criteria.list();
	}
	/**
	 * 查询集合
	 */
	public <M> List<M> list(Class<M> clazz, Condition condition, PageBean pageBean) {
		Criteria criteria = getCriteria(clazz);
		if(pageBean.getPage() <= 1)
			criteria.setFirstResult(0);
		else
			criteria.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
		criteria.setMaxResults(pageBean.getRows());
		if(pageBean.getOrderBy()!=null){
			criteria.addOrder(pageBean.getOrderBy());
		}
		if(pageBean.getOrders().size()>0){
			for(Order order : pageBean.getOrders()){
				criteria.addOrder(order);
			}
		}
		if(pageBean.getCriterions().size()>0){
			for(Criterion criterion : pageBean.getCriterions()){
				criteria.add(criterion);
			}
		}
		for(Order order : condition.getOrders()){
			criteria.addOrder(order);
		}
		for(Criterion criterion : condition.getCriterions()){
			criteria.add(criterion);
		}
		return criteria.list();
	}
	
	/**
	 * 根据hql语句查询，结果反射成实体
	 */
	public <M> List<M> listForEntity(Class<M> clazz, String hql, Object...params){
		List<M> list = null;
		if(ObjectUtil.isNull(params)){
			list = list(clazz, hql);
		}else{
			list = list(clazz, hql, params);
		}
		List<M> entitys = parseEntity(clazz, hql, list);
		return entitys;
	}
	/**
	 * 根据hql语句查询，结果反射成实体
	 */
	public <M> List<M> listForEntity(Class<M> clazz, String hql, PageBean pageBean, Object...params) {
		if(ObjectUtil.isNull(params) || ObjectUtil.isEmpty(params))
			params = pageBean.getParams();
		List<M> list = null;
		if(ObjectUtil.isNull(params)){
			list = list(clazz, hql, pageBean);
		}else{
			list = list(clazz, hql, pageBean, params);
		}
		List<M> entitys = parseEntity(clazz, hql, list);
		return entitys;
	}
	
	/**
	 * 获取单个字段的值
	 * @param clazz
	 * @param hql
	 * @return
	 */
	public <M> List<M> listSingleColumn(Class<?> clazz,Class<M> m,String columnName){
		String hql = "SELECT "+columnName+" FROM "+clazz.getSimpleName();
		return listSingleColumn(m,hql,null);
	}
	
	/**
	 * 获取单个字段的值
	 * @param clazz
	 * @param hql
	 * @return
	 */
	public <M> List<M> listSingleColumn(Class<M> m,String hql,Object[] queryParams){
		Query query = getQuery(hql);
		setParameters(query, queryParams);
		return query.list();
	}
	
	/**
	 * 根据SQL语句查询
	 * @param <M>
	 * @param clazz
	 * @param sql
	 * @return
	 */
	public <M> List<M> listByNativeSQL(Class<M> clazz,String sql){
		SQLQuery query = getSQLQuery(sql);
		query.addEntity(clazz);
		return query.list();
	}
	/**
	 * 根据SQL语句查询，带参数列表
	 * @param <M>
	 * @param clazz
	 * @param sql语句
	 * @param 参数列表
	 * @return
	 */
	public <M> List<M> listByNativeSQL(Class<M> clazz,String sql,Object[] params){
		SQLQuery query = getSQLQuery(sql);
		query.addEntity(clazz);
		setParameters(query, params);
		return query.list();
	}
	
	/**
	 * 根据SQL语句获取集合带参数列表并分页
	 * @param <M>
	 * @param clazz
	 * @param sql
	 * @param 参数列表
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public <M> List<M> listByNativeSQL(Class<M> clazz,String sql,Object[] params,PageBean pageBean){
		SQLQuery query = getSQLQuery(sql);
		setParameters(query, params);
		if(pageBean.getPage()<=1)
			query.setFirstResult(0);
		else
			query.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
		query.setMaxResults(pageBean.getPage());
		query.addEntity(clazz);
		return query.list();
	}
	/**
	 * 获取全部数量
	 * @param <M>
	 * @param clazz
	 * @return
	 */
	public <M> int count(Class<M> clazz){
		Query query = getQuery("select count(*) from "+clazz.getSimpleName());
		Long total = (Long) query.uniqueResult();
		return total.intValue();
	}
	/**
	 * 根据指定查询条件查询
	 */
	public <M> int count(Class<M> clazz, Criterion... criterions) {
		Criteria criteria = getCriteria(clazz);
		for(Criterion criterion : criterions)
			criteria.add(criterion);
		criteria.setProjection(Projections.rowCount());
		Long total = (Long) criteria.uniqueResult();
		return total.intValue();
	}
	/**
	 * 根据HQL语句获取记录数
	 */
	public <M> int count(Class<M> clazz, String hql, Object...params) {
		Query query = getQuery(hql);
		setParameters(query, params);
		Long total = (Long) query.uniqueResult();
		return total.intValue();
	}
	/**
	 * 根据SQL语句获取记录数
	 */
	public <M> int countByNativeSQL(String sql) {
		SQLQuery query = getSQLQuery(sql);
		Object obj = query.uniqueResult();
		if(obj instanceof java.math.BigDecimal){
			return ((java.math.BigDecimal)obj).intValue();
		}else if(obj instanceof java.math.BigInteger){
			return ((java.math.BigInteger)obj).intValue();
		}else{
			return ((Long)obj).intValue();
		}
	}
    /**
	 * 根据SQL语句获取集合
	 * 返回数据结构为每行一个MAP集合，map集合的键为数据库字段名，值为字段值
	 * @param sql
	 * @return
	 * @see queryForList
	 */
	@Deprecated
	public List<Map<String, Object>> listByNativeSql(String sql) {
		Query query = getSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}
	
	/**
	 * 根据SQL语句获取集合
	 * 返回数据结构为每行一个MAP集合，map集合的键为数据库字段名，值为字段值
	 * @param sql
	 * @return
	 * @see queryForList
	 */
	@Deprecated
	public List<Map<String, Object>> listByNativeSql(String sql, PageBean pageBean) {
		Query query = getSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if(pageBean.getPage()<=1)
			query.setFirstResult(0);
		else
			query.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
		return query.list();
	}
	
	public List<Object> listByNativeSqlForList(String sql) {
		Query query = getSQLQuery(sql);
		return query.list();
	}
	/**
	 * 分页，返回List集合，集合中已数组的形式存放
	 */
	public List<Object> listByNativeSqlForList(String sql, PageBean pageBean) {
		Query query = getSQLQuery(sql);
		if(pageBean.getPage()<=1)
			query.setFirstResult(0);
		else
			query.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
		return query.list();
	}
	
	public Object getByNativeSqlForObject(String sql) {
		Query query = getSQLQuery(sql);
		return query.uniqueResult();
	}
	
	public List<Map<String, Object>> queryForList(String sql) {
		return listByNativeSql(sql);
	}
	/**
	 * 分页获取
	 */
	public List<Map<String, Object>> queryForList(String sql, PageBean pageBean) {
		Query query = getSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if(pageBean.getPage()<=1)
			query.setFirstResult(0);
		else
			query.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
		return query.list();
	}
	
	public String queryForString(String sql) {
		return getByNativeSqlForObject(sql).toString();
	}
	
	public int queryForInt(String sql) {
		return Integer.parseInt(getByNativeSqlForObject(sql).toString());
	}
	
	public long queryForLong(String sql) {
		return Long.parseLong(getByNativeSqlForObject(sql).toString());
	}
	
	
	/****************************工具方法**********************************/
	/**
	 * 解析参数
	 * @param clazz
	 * @param hql
	 * @param list
	 * @return
	 */
	private <M> List<M> parseEntity(Class<M> clazz, String hql, List<M> list) {
		List<M> entitys = new ArrayList<M>();
		for(Object obj : list){
			if(!obj.getClass().equals(clazz)){
				String[] columns = hql.substring(hql.indexOf("select")+6,hql.indexOf("from")).split(",");
				if(obj instanceof Object[]){
					try {
						M m = clazz.newInstance();
						Object[] values = (Object[]) obj;
						for(int i=0;i<columns.length;i++){
							ReflectUtil.setFieldValue(m,columns[i],values[i]);
						}
						obj = m;
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}else{
					try {
						M m = clazz.newInstance();
						ReflectUtil.setFieldValue(m, columns[0], obj);
						obj = m;
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			entitys.add((M) obj);
		}
		return entitys;
	}
	/**
     * 绑定参数
     * @param query
     * @param 参数列表
     */
    private void setParameters(Query query, Object[] paramlist) {
        if (paramlist != null) {
            for (int i = 0; i < paramlist.length; i++) {
                if(paramlist[i] instanceof Date) {
                    query.setTimestamp(i, (Date)paramlist[i]);
                } else {
                    query.setParameter(i, paramlist[i]);
                }
            }
        }
    }
}
