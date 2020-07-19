package club.dlblog.page.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.util.List;

@Mapper
public interface UserDao {

    @Select(" SELECT\n" +
            "        username  username,\n" +
            "        dear_name dearName,\n" +
            "        telephone_num telephoneNum,\n"+
            "        email email\n"+
            "        FROM\n" +
            "        app_user\n")
    public List<UserBean> selUserInfoByUsername(@Param("bean") UserSearchBean bean);
}
