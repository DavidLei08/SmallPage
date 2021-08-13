package club.dlblog.page.Interceptor;

import club.dlblog.page.helper.ShardingStrategy;
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
import java.util.HashSet;
import java.util.Set;

/**
 * 分表拦截器
 * @author machenike
 */
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class ShardingInterceptorPlugin implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(ShardingInterceptorPlugin.class);

    private static final String META_OBJECT_KEY_BOUND_SQL = "delegate.boundSql.sql";

    private static ThreadLocal<Object> SHARDINGG_SIGN = new ThreadLocal<Object>();

    private Set<String> shardingTables = new HashSet<String>();

    private ShardingStrategy shardingStrategy;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取命令handler取得
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //原来应该执行的sql吧
        String sql = statementHandler.getBoundSql().getSql();


        // 获取statementHandler包装类
        MetaObject MetaObjectHandler = SystemMetaObject.forObject(statementHandler);

        // 分离代理对象链
        while (MetaObjectHandler.hasGetter("h")) {
            Object obj = MetaObjectHandler.getValue("h");
            MetaObjectHandler = SystemMetaObject.forObject(obj);
        }

        while (MetaObjectHandler.hasGetter("target")) {
            Object obj = MetaObjectHandler.getValue("target");
            MetaObjectHandler = SystemMetaObject.forObject(obj);
        }

        //sql不满足分片请求时
        if (!isMatch(sql) || shardingTables.isEmpty()) {
            return invocation.proceed();
        }
        logger.debug("tableName match,start intercept");
        logger.debug(">> originSql:{}", sql);

        //获取meta对象取得
        MetaObject metaObject = MetaObject.forObject(
                statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                new DefaultReflectorFactory());

        String shardingSql = buildSql(sql, SHARDINGG_SIGN.get());
        logger.debug(">> shardingSql:{}", shardingSql);
        //重新分表sql
        MetaObjectHandler.setValue(META_OBJECT_KEY_BOUND_SQL, shardingSql);
        //推进拦截器调用链
        return invocation.proceed();
    }

    private String buildSql(String sql, Object sigin) {
        Set<String> tableSet = getSqlTables(sql);
        for (String table : tableSet) {
            sql = sql.replaceAll(table, shardingStrategy.doSharding(table, SHARDINGG_SIGN.get()));
        }
        return sql;
    }

    private boolean isMatch(String sql) {
        for (String tableName : shardingTables) {
            if (sql.contains(tableName)) {
                return true;
            }
        }
        return false;
    }


    private Set<String> getSqlTables(String sql) {
        Set<String> set = new HashSet<String>();
        for (String table : shardingTables) {
            if (sql.contains(table)) {
                set.add(table);
            }
        }
        return set;
    }

    public static void setCurrentSign(Object sigin) {
        SHARDINGG_SIGN.set(sigin);
    }

    public ShardingInterceptorPlugin() {
    }

    public ShardingInterceptorPlugin(Set<String> shardingTables, ShardingStrategy shardingStrategy) {
        this.shardingTables = shardingTables;
        this.shardingStrategy = shardingStrategy;
    }

    public Set<String> getShardingTables() {
        return shardingTables;
    }

    public void setShardingTables(Set<String> shardingTables) {
        this.shardingTables = shardingTables;
    }

    public ShardingStrategy getShardingStrategy() {
        return shardingStrategy;
    }

    public void setShardingStrategy(ShardingStrategy shardingStrategy) {
        this.shardingStrategy = shardingStrategy;
    }
}
