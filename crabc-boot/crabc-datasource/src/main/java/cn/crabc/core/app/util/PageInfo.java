package cn.crabc.core.app.util;

import com.github.pagehelper.PageSerializable;

import java.util.List;

/**
 * 分页对象
 *
 * @author yuqf
 *
 */
public class PageInfo<T> extends PageSerializable<T> {
    private int pageNum;
    private int pageSize;

    public PageInfo() {
    }

    public PageInfo(List<? extends T> list, int pageNum, int pageSize) {
        super(list);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
