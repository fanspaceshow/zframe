package org.zframework.orm.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.zframework.core.support.ApplicationContextHelper;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.nethsoft.zhxq.core.util.ObjectUtil;

public final class MemcachedClientUtil {
	private static MemCachedClient cachedClient = null;
	private static void init(){
		if(ObjectUtil.isNull(cachedClient)){
			Properties prop = getProp();
			
			cachedClient = new MemCachedClient();
			//获取连接池实例
			SockIOPool pool = SockIOPool.getInstance();
			
			//服务器列表 及权重
			String[] servers = {prop.getProperty("url","cache.nethsoft.com")+":"+prop.getProperty("port", "11211")};
			Integer[] weights = {Integer.parseInt(prop.getProperty("weight", "3"))};
			
			// 设置服务器信息
			pool.setServers(servers);
			pool.setWeights(weights);
			
			// 设置初始化连接数、最小连接数、最大连接数、最大处理时间
			pool.setInitConn(Integer.parseInt(prop.getProperty("initConn", "10")));
			pool.setMinConn(Integer.parseInt(prop.getProperty("minConn", "10")));
			pool.setMaxConn(Integer.parseInt(prop.getProperty("maxConn", "10")));
			pool.setMaxIdle(Integer.parseInt(prop.getProperty("maxIdle", "3600000")));
			
			//设置连接池守护线程的睡眠时间
			pool.setMaintSleep(Integer.parseInt(prop.getProperty("maintSleep", "60")));
			
			//设置TCP参数，连接超时
			pool.setNagle(false);
			pool.setSocketTO(60);
			pool.setSocketConnectTO(0);
			
			// 初始化连接池
			pool.initialize();
			
		}
	}
	
	public static MemCachedClient getClient(){
		init();
		return cachedClient;
	}
	public static Properties getProp(){
		Properties prop = new Properties();
		try {
			File file = ApplicationContextHelper.getInstance().getApplicationContext().getResource("classpath:org/zframework/conf/memcached.properties").getFile();
			InputStream is = new FileInputStream(file);
			prop.load(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	public static void saveProp(Properties prop){
		try {
			File file = ApplicationContextHelper.getInstance().getApplicationContext().getResource("classpath:org/zframework/conf/memcached.properties").getFile();
			OutputStream out = new FileOutputStream(file);
			prop.store(out, "");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean testProp(Properties prop){
		try {
			MemCachedClient cc = new MemCachedClient();
			//获取连接池实例
			SockIOPool pool = SockIOPool.getInstance();
			
			//服务器列表 及权重
			String[] servers = {prop.getProperty("url","cache.nethsoft.com")+":"+prop.getProperty("port", "11211")};
			Integer[] weights = {Integer.parseInt(prop.getProperty("weight", "3"))};
			
			// 设置服务器信息
			pool.setServers(servers);
			pool.setWeights(weights);
			
			// 设置初始化连接数、最小连接数、最大连接数、最大处理时间
			pool.setInitConn(Integer.parseInt(prop.getProperty("initConn", "10")));
			pool.setMinConn(Integer.parseInt(prop.getProperty("minConn", "10")));
			pool.setMaxConn(Integer.parseInt(prop.getProperty("maxConn", "10")));
			pool.setMaxIdle(Integer.parseInt(prop.getProperty("maxIdle", "3600000")));
			
			//设置连接池守护线程的睡眠时间
			pool.setMaintSleep(Integer.parseInt(prop.getProperty("maintSleep", "60")));
			
			//设置TCP参数，连接超时
			pool.setNagle(false);
			pool.setSocketTO(60);
			pool.setSocketConnectTO(0);
			
			// 初始化连接池
			pool.initialize();
			
			return cc.set("test", true);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return false;
	}
	public static boolean test(){
		init();
		return cachedClient.set("test", true);
	}
	public static boolean rebulidClient(){
		cachedClient = null;
		init();
		return true;
	}
	/**
	 * 设置正在处于刷新缓存状态
	 * 路由发现处于刷新缓存状态时，调用数据库数据，保持服务不中断
	 * @return
	 */
	public static boolean setRefreshing(boolean c){
		return cachedClient.set("refreshing", c);
	}
	/**
	 * 判断是否处于刷新缓存状态，或者缓存服务器不可用状态
	 * @return
	 */
	public static boolean isRefreshing(){
		if(!test()){//如果缓存服务器不可用，标记成刷新状态，调用数据库数据
			return true;
		}else{
			return (Boolean) cachedClient.get("refreshing");
		}
	}
}
