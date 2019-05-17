package com.Servlet.Tookit;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RQRPF {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Object object;
	private long fileMaxSize=-1;
	private Map<Integer, FileType> map;

	public RQRPF(HttpServletRequest request, HttpServletResponse response,Object object) {
		this.request = request;
		this.response = response;
		this.object=object;
		map=new HashMap<>();
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public Object getObject() {
		return object;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public long getFileMaxSize() {
		return fileMaxSize;
	}
	public void setFileMaxSize(long fileMaxSize) {
		this.fileMaxSize = fileMaxSize;
	}
	public Map<Integer, FileType> getFileMap(){
		return this.map;
	}
	public void setFileType(Integer curfileIndex,boolean isContain,String...fileType) {
		//curfileIndex如果为空或0指所有文件按这种类型过滤,如果设置了其他值均无效,以null和0为准;
		map.put(curfileIndex, new FileType(isContain,fileType));
	}
	class FileType{
		public boolean contain;
		public String[] fileType;
		public FileType(boolean isContain,String...fileType) {
			this.contain=isContain;this.fileType=fileType;
		}
	}
}
