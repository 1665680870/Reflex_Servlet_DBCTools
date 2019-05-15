package com.Servlet.Tookit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.reflex.Object.InstanceReflexParse;

import DBConnect.DBCTools;

public class Enter implements Filter{
	private Map<Thread, Boolean> filter;
	 public InstanceReflexParse reflexParse;
	 public ServletTookit servletTookit;
	 public DBCTools dbcTools;
	public Enter() {
		dbcTools = DBCTools.getDbcTools("default", "default.date");
    	reflexParse=InstanceReflexParse.getReflexParse();
    	servletTookit=ServletTookit.geServletTookit(reflexParse);
		filter=new HashMap<>();
	}
	public void setDBCTools(DBCTools dbcTools) {this.dbcTools = dbcTools;}
	
	
	@Override
	public void destroy() {}

	public final boolean setFilter(boolean filter) {
		this.filter.put(Thread.currentThread(), filter);
		return filter;
	}

	public void init(HttpServletRequest request,HttpServletResponse response)throws Exception{}
	
	public final boolean enter(HttpServletRequest request,String...urls){
		for(String string:urls){
			if(request.getRequestURL().toString().contains(request.getContextPath()+"/"+string))return true;
		}
		return false;
	}
	public final boolean notEnter(HttpServletRequest request,String...urls){
		for(String string:urls){
			if(request.getRequestURL().toString().contains(request.getContextPath()+"/"+string))return false;
		}
		return true;
	}
	public final boolean contain(HttpServletRequest request,String...urls){
		for(String string:urls){
			if(request.getRequestURL().toString().contains(string))return true;
		}
		return false;
	}
	public final boolean notContain(HttpServletRequest request,String...urls){
		for(String string:urls){
			if(request.getRequestURL().toString().contains(string))return false;
		}
		return true;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {	
		try{			
			filter.put(Thread.currentThread(), true);
			init((HttpServletRequest) request, (HttpServletResponse) response);	
			
			if(filter.get(Thread.currentThread())){
				chain.doFilter(request, response);
			}else{
				return;
			}
		}catch(Error e){
			Erro(e, (HttpServletRequest)request, (HttpServletResponse)response);
		}catch(Exception e){
			Exception(e, (HttpServletRequest)request, (HttpServletResponse)response);
		}finally{
			filter.remove(Thread.currentThread());
		}
		

	}	
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		

	}
	public void Erro(Error e,HttpServletRequest request,HttpServletResponse response){}
	public void Exception(Exception e,HttpServletRequest request,HttpServletResponse response){}
	
}
