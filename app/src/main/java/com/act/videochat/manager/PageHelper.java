package com.act.videochat.manager;

import java.util.List;

public class PageHelper<T> {

    private List<T> allData; // 所有数据
    private int perPage = 10; // 每页条目
    private int currentPage = 1;// 当前页
    private int pageNum = 1; // 页码
    private List<T> childData;// 子数据
    private int allNum;// 总共条目

    public PageHelper(List<T> datas, int perPage) {
        this.allData = datas;
        if (perPage > 0)
            this.perPage = perPage;
        // 如果数据大于10条
        if (allData != null && allData.size() > perPage) {
            childData = allData.subList(0, perPage - 1);
        }
        allNum = allData.size();
        // 如果总数能除断perPage，页数就是余数，否则+1
        pageNum = allNum % perPage == 0 ? (allNum / perPage) : (allNum / perPage + 1);
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public boolean hasNextPage() {// 是否有下一页
        return currentPage < pageNum;
    }

    public void nextPage() {// 下一页
        currentPage = hasNextPage() ? currentPage + 1 : pageNum;
    }


    /**
     * 获得当前数据
     *
     * @return
     */
    public List<T> currentList() {
        if (allData.size() > perPage) {
            if (currentPage == 1) {
                childData = allData.subList(0, perPage);
            } else if (currentPage == pageNum) {
                childData = allData.subList(perPage * (pageNum - 1), allNum);
            } else {
                childData = allData.subList(perPage * (currentPage - 1), perPage * currentPage);
            }
            return childData;
        } else {
            return allData;
        }
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

}
