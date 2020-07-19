package club.dlblog.page.config;

import club.dlblog.page.Interceptor.PageInterceptorPlugin;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PageAutoConfig {

    @Value("${club.dlblog.page.enabled}")
    private  boolean  pageEnabled;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Bean
    public Interceptor initPageInterceptor(){
        Interceptor pageInterceptor = new PageInterceptorPlugin();
        if(pageEnabled) {
            sqlSessionFactory.getConfiguration().addInterceptor(pageInterceptor);
        }
        return  pageInterceptor;
    }

}
