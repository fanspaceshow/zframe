package org.zframework.core.factory.config;

import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.DecUtil;

/**
 * Spring配置文件加载器
 * 继承自Spring的PropertyPlaceholderConfigurer
 * 重写processProperties方法，实现spring加载之前对参数进行处理
 * 如用户名密码加密等.
 * 创建人：ZENGCHAO
 * 创建时间：‎2012‎-12‎-03‎ 上午10:47:25
 * @version 1.0
 *
 */
public class PropertyPlaceholderConfigurerExt extends PropertyPlaceholderConfigurer{
	private Properties _hibernateProps = null;
	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		this._hibernateProps = props;
		Enumeration<?> enuProp = props.keys();
		DecUtil des = new DecUtil();// 实例化一个对像
		des.genKey(ApplicationCommon.DEC_KEY);// 生成密匙
		while(enuProp.hasMoreElements()){
			String name = enuProp.nextElement().toString();
			if(name.endsWith(".username") || name.endsWith(".password")){
				String val = props.getProperty(name);
				props.setProperty(name, des.getDesString(val));
			}
		}
		super.processProperties(beanFactoryToProcess, props);
	}
	
	/**
	 * 获取hibernate配置文件信息
	 * @return
	 */
	public Properties getHibernateProperties(){
		return _hibernateProps;
	}
}
