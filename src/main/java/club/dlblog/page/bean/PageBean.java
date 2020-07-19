package club.dlblog.page.bean;

/**
 * @author machenike
 */
public class PageBean {

    /**
     * 总记录条数
     */
    private Integer total;

    /**
     * 每页限定条数
     */
    private Integer limit;

    /**
     * 当前页数
     */
    private Integer currentPage;

    /**
     * 起始位
     */
    private Integer start;

    /**
     * 总页数
     */
    private Integer PageCount;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
        this.autoCount();
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
        this.autoCount();
    }

    public Integer getCurrentPage() {
        return currentPage;

    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
        this.autoCount();
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getPageCount() {
        return PageCount;
    }

    public void setPageCount(Integer pageCount) {
        PageCount = pageCount;
    }

    public PageBean(Integer limit, Integer currentPage) {
        this.limit = limit;
        this.currentPage = currentPage;
        this.autoCount();
    }

    public PageBean() {
    }


    /**
     * 自动运算相关数值
     */
    public void autoCount(){
        if(limit!=null&&limit>0&&total!=null){
            PageCount = total/limit+1;
        }
        if(limit!=null&&limit>0&&currentPage>=1) {
            start = (currentPage - 1) * limit;
        }
    }

    /**
     * 构建分页对象
     * @param limit
     * @param currentPage
     * @return
     */
    public static PageBean buildPage(Integer limit, Integer currentPage){
      return  new PageBean(limit,currentPage);
    }

}
