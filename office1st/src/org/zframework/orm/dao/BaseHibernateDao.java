package org.zframework.orm.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.zframework.core.support.ApplicationContextHelper;
import org.zframework.orm.datasource.DataSourceSwitcher;
/**
 * 数据库基础操作类的父类
 * 实现IBaseDao接口
 * @author ZENGCHAO
 *
 */
public abstract class BaseHibernateDao implements IBaseDao{
	public abstract Session getSession();
	public abstract Query getQuery(String hql);
	public abstract SQLQuery getSQLQuery(String sql);
	public abstract Criteria getCriteria(Class<?> clazz);
	public abstract SessionFactory getSessionFactory();
	
	/**
	 * 切换为指定数据源
	 * @param dataSourceName
	 */
	public void setDataSource(String dataSourceName){
		DataSourceSwitcher dataSource = ApplicationContextHelper.getInstance().getBean(DataSourceSwitcher.class);
		dataSource.setDataSource(dataSource.getDataSource(dataSourceName));
	}
	/**
	 * 恢复为默认数据源
	 * 无需手动调用
	 */
	public void restoreDefaultDataSource(){
		DataSourceSwitcher dataSource = ApplicationContextHelper.getInstance().getBean(DataSourceSwitcher.class);
		dataSource.restoreDefaultDataSource();
	}
}
