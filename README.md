
![在这里插入图片描述](https://imgconvert.csdnimg.cn/aHR0cHM6Ly93d3cuZGxibG9nLmNsdWIvZmlsZS8yMDIwMDQyNS9welBtaUhzay5wbmc?x-oss-process=image/format,png)
# SmallPage
轻量级分页组件，基于入参类型


## 核心依赖
``` xml
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>2.1.0</version>
		</dependency>
```

## 功能描述
- 自动拦截进行分页
- 基于Dao层方法入参类型
- 自动count总条数

## 核心类 - PageInterceptorPlugin
- 使用方式，只需将Dao查询方法的入参Bean继承于PageWrapperBean，设定Page对象则可以实现分页，分页完成后的信息也保存在Page对象中
- 入参类型可以parameterType 定义，也可以@Param("")传入，多个入参时，只要其中一个入参类型为PageWrapperBean的上转类型，就可以实现分页
- 入参类型不为PageWrapperBean的上转类型时，直接推进拦截链
