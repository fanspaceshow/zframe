# zframe
该资源作为我个人学习之用。下面是该资源原版地址。


  原地址：
    http://www.oschina.net/p/zframe

前言

基于SpringMVC+Hibernate+EasyUI的快速开发框架，开放所有源代码

使用说明

    数据库初始化文件在：webapps/resources/framework/doc/db里
    service-context.xml中配置jdbc_mysql.properties或者jdbc_oracle.properties，主要是将不同数据库的配置分开。需要用哪个数据库就在xml文件中配置哪个配置文件即可。
    数据库链接配置文件在：src/org/framework/conf/jdbc_mysql.properties中修改
    用户名密码加密处理，加密工具在lib下的 genpass.jar，双击运行
    项目为Eclipse项目，下载下来之后，可以直接导入Eclipse/MyEclipse
    默认用户名：superadmin
    密码：pass1234

框架特点

    DDD模式，并从底层支持数据库的读写分离
    MVC架构，设计精巧，使用简单
    遵循COC原则，零配置，无xml
    支持动态插件机制，运行时可以动态加载各种插件
    前端采用EasyUI，进行了大量的封装，改造
    完成了权限管理的大部分功能
    界面支持多窗口，窗口之间可以自由传值
    支持在线创建数据库表
    还有更多已经不记得的特点。。。
    
  
