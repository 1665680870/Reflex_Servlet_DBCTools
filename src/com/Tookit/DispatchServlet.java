package com.Servlet.Tookit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.other.Utils.WebServiceInfo;
import com.reflex.Object.InstanceReflexParse;

import DBConnect.DBCTools;

public class DispatchServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
    public InstanceReflexParse reflexParse;
    public ServletTookit servletTookit;
    public DBCTools dbcTools;
    public WebServiceInfo serviceInfo;
    private Map<Thread, RQRPF> map=null;
    
    public DispatchServlet() {
    	dbcTools = DBCTools.getDbcTools("default", "default.date");
    	reflexParse=InstanceReflexParse.getReflexParse();
    	servletTookit=ServletTookit.geServletTookit(reflexParse);
    	serviceInfo=WebServiceInfo.getServiceInfo();
    	map=new HashMap<Thread, RQRPF>();
    }
    public HttpServletRequest request(){return RQRPF().getRequest();}
    public HttpServletResponse response(){return RQRPF().getResponse();}
    public HttpSession session(){return request().getSession();}
    public RQRPF RQRPF(){return map.get(Thread.currentThread());}
    public void setDBCTools(DBCTools dbcTools){this.dbcTools=dbcTools;}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		try{
			if(!(doFilter(request, response))){response.sendRedirect(request.getHeader("Referer"));return;}
			
			map.put(Thread.currentThread(), new RQRPF(request, response,this));
			
			init(request,response);
			servletTookit.setServletMethod(map);		
			callBack(servletTookit.getMSG(map.get(Thread.currentThread()).getRequest()),response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			map.remove(Thread.currentThread());
		}	
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			if(!(doFilter(request, response))){response.sendRedirect(request.getHeader("Referer"));return;}
			
			map.put(Thread.currentThread(), new RQRPF(request, response,this));
			
			init(request,response);
			if (servletTookit.setServlet(map)) {
				map.get(Thread.currentThread()).getRequest().getSession().removeAttribute("upload");
			}
			callBack(servletTookit.getMSG(map.get(Thread.currentThread()).getRequest()),response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			map.remove(Thread.currentThread());
		}		
	}	
	public void callBack(Object returnValue,HttpServletResponse response) throws ServletException, IOException {
		if (returnValue!=null) {
			if (returnValue.toString().toLowerCase().indexOf("f:")!=-1) {
				request().getRequestDispatcher("../"+returnValue.toString().split("(?i)f:")[1].trim()).forward(request(), response());
			}else if (returnValue.toString().toLowerCase().indexOf("r:")!=-1){
				response().sendRedirect(request().getContextPath()+"/"+returnValue.toString().split("(?i)r:")[1].trim());
			}else{
				response.getWriter().write(returnValue.toString());
			}
		}
	}
	public void init(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {}
	public boolean doFilter(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {return true;}
}
