package club.dlblog.page;

import club.dlblog.page.bean.PageBean;
import club.dlblog.page.dao.UserDao;
import club.dlblog.page.dao.UserSearchBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmallPageApplicationTests {

	@Autowired
	private UserDao userDao;

	@Test
	void contextLoads() {
		UserSearchBean searchBean = new UserSearchBean();
		PageBean pageBean = new PageBean(10,1);
		searchBean.setPage(pageBean);
		userDao.selUserInfoByUsername(searchBean);
	}

}
