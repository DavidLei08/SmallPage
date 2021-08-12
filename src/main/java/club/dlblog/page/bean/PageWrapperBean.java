package club.dlblog.page.bean;

public class  PageWrapperBean {

    PageBean page = new PageBean();

    public PageWrapperBean(PageBean page) {
        this.page = page;
    }

    public PageBean getPage(){
        return this.page;
    }

    public void setPage(PageBean bean){
         this.page = bean;
    }


}
