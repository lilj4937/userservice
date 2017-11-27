package com.shcx.user.userservice.vo;

import java.util.List;

public class PageVo<T> {
	
	private List<T> data;//数据集合
	
	private int pageSize;//每页条数
	
	private int pageNo;//页码
	
	private long total;//总数
	
	private int pages;//页码总数
	

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
	
}
