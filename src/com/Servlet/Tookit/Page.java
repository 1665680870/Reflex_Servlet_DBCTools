package com.Servlet.Tookit;

import java.util.List;

import DBConnect.DBCTools;

public class Page<T> {
	private int countPage;
	private int size;
	private List<T> element;
	private DBCTools dbcTools;
	private Class<T> entity;
	public DBCTools getDbcTools() {
		return dbcTools;
	}
	public void setDbcTools(DBCTools dbcTools) {
		this.dbcTools = dbcTools;
	}
	public Class<T> getEntity() {
		return entity;
	}
	public void setEntity(Class<T> entity) {
		this.entity = entity;
	}
	public void setTableName(String tableName) {
		TableName = tableName;
	}
	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setElement(List<T> element) {
		this.element = element;
	}
	public void setCountEle(int countEle) {
		this.countEle = countEle;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	private String TableName;
	private int countEle;
	private int curPage;
	public Page(DBCTools dbcTools,int size,Class<T> entity,String...tableName) {
		this.size=size;
		this.dbcTools=dbcTools;
		this.entity=entity;
		this.curPage=1;
		if(tableName.length>0){
			TableName=tableName[0];
			countPage=(int) Math.ceil(dbcTools.countTable(TableName)/(size+0.0));
		}else{
			TableName=entity.getSimpleName();
			countPage=(int) Math.ceil(dbcTools.countTable(TableName)/(size+0.0));
		}
		countEle=dbcTools.countTable(TableName);
		jump(curPage);
	}
	public int getSize(){
		return size;
	}
	public int getCurPage(){
		return curPage;
	}
	public int getCountPage(){
		return countPage;
	}
	public int getCountEle(){
		return countEle;
	}
	public List<T> getElement() {
		return element;
	}
	
	public void jump(int curPage){
		if(curPage<1)curPage=1;
		if(curPage>countPage)curPage=countPage;
		this.curPage=curPage;
		if(curPage==countPage){
			if(dbcTools.countTable(TableName)%size==0){
				element=dbcTools.getListByNameLimit(entity, (curPage-1)*size, size, TableName);
			}else{
				element=dbcTools.getListByNameLimit(entity, (curPage-1)*size, dbcTools.countTable(TableName)%size, TableName);
			}
		}else{
			element=dbcTools.getListByNameLimit(entity, (curPage-1)*size, size, TableName);
		}
	}
	
	
}
