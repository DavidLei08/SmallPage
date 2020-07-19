package club.dlblog.page.Interceptor;

import club.dlblog.page.bean.PageBean;
import club.dlblog.page.bean.PageWrapperBean;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;

/**
 * @author machenike
 */
@Intercepts(@Signature(type = StatementHandler.class,method = "prepare",args={Connection.class,Integer.class}))
public class PageInterceptorPlugin  implements Interceptor {

    private static final String META_OBJECT_KEY_SQL_ID  = "delegate.mappedStatement.id";

    private static final String META_OBJECT_KEY_BOUND_SQL = "delegate.boundSql.sql";

    private static final Logger logger = LoggerFactory.getLogger(PageInterceptorPlugin.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取命令handler取得
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //获取入参handler
        ParameterHandler parameterHandler = statementHandler.getParameterHandler();
        //获取参数对象
       Object parameterObject = parameterHandler.getParameterObject();
       PageBean pageBean =  getPageBean(parameterObject);
       ThreadLocal threadLocal = new ThreadLocal();
       if(pageBean!=null) {
           logger.debug("parameter type match,start intercept");
           //获取meta对象取得
           MetaObject metaObject = MetaObject.forObject(
                   statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY,SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                   new DefaultReflectorFactory());
           //获取当前sqlId
           String sqlId = (String)metaObject.getValue(META_OBJECT_KEY_SQL_ID);
           //原来应该执行的sql吧
           String sql = statementHandler.getBoundSql().getSql();
           logger.debug("-originSql:" + sql);
           Connection connection = (Connection) invocation.getArgs()[0];

           String countSql = bulidCountSql(sql);
           logger.debug("-countSql:" + countSql);

           //渲染参数
           PreparedStatement preparedStatement = connection.prepareStatement(countSql);
           //条件交给mybatis
           parameterHandler.setParameters(preparedStatement);
           //让mybatis执行这个sql
           ResultSet resultSet = preparedStatement.executeQuery();
           int count = 0;
           if (resultSet.next()) {
               count = resultSet.getInt(1);
           }
           resultSet.close();
           preparedStatement.close();

           //limit 1 ,10  十条数据   总共可能有100   count 要的是 后面的100
           pageBean.setTotal(count);

           //拼接分页语句(limit) 并且修改mysql本该执行的语句
           String pageSql = buildPageSql(pageBean, sql);
           logger.debug("-pageSql:" + pageSql);
           //重新绑定分页sql
           metaObject.setValue(META_OBJECT_KEY_BOUND_SQL, pageSql);
       }
        //推进拦截器调用链
        return invocation.proceed();
    }

    /**
     * 入参对象中取得分页对象
     * @param o
     * @return
     */
    private  PageBean getPageBean(Object o){
        PageWrapperBean pageWrapperBean = null;
        PageBean pageBean = null;
        //类型判断
        //入参为Map 上转类型
        if(o instanceof  Map){
            //迭代Map
            Map<String,Object> paramMap = (Map<String, Object>) o;
            Set<String> keySet = paramMap.keySet();
            for(String key:keySet){
                Object valueObject = paramMap.get(key);
                if(valueObject instanceof PageWrapperBean){
                    pageWrapperBean = (PageWrapperBean) valueObject;
                    break;
                }
            }
          //入参为  PageWrapperBean 上转类型
        } else  if(o instanceof  PageWrapperBean){
            pageWrapperBean = (PageWrapperBean) o;
        }
        pageBean = pageWrapperBean.getPage();
        return  pageBean;
    }

    /**
     * 构建记录条数countSql
     * @param originSql
     * @return
     */
    private String bulidCountSql(String originSql){
        //优化一下就是讲select 到from 之间的字符串替换为 count(1) 即可，这里仅为了方便
        String countSql = "select count(1) from ("+originSql+") a";
        return  countSql;
    }

    /**
     * 构建分页sql
     * @param pageBean
     * @param originSql
     * @return
     */
    private String buildPageSql(PageBean pageBean,String originSql){
        //拼接分页语句(limit) 并且修改mysql本该执行的语句
        String pageSql = originSql+" limit "+pageBean.getStart()+","+pageBean.getLimit();
        return pageSql;
    }
}
