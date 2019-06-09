package com.Servlet.Tookit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.reflex.Object.InstanceReflexParse;
import com.security.center.RolePermissionProving;

public class ServletTookit {
	private InstanceReflexParse reflexParse=null;
	private RolePermissionProving proving=null;
	private static ServletTookit servletTookit=null;
	public Map<String, HttpSession> httpSession=null;
	public Map<String, Object> repository=null;
	
	private ServletTookit(InstanceReflexParse reflexParse) {
		this.reflexParse=reflexParse;
		proving=RolePermissionProving.getRolePermission();
		httpSession=new HashMap<>();
		repository=new HashMap<>();

	}
	public synchronized static ServletTookit geServletTookit(InstanceReflexParse reflexParse) {
		if (servletTookit==null) {
			servletTookit=new ServletTookit(reflexParse);
		}
		return servletTookit;
	}
	
	public void saveStory(String onlyId,Object object){
		repository.put(onlyId, object);
	}
	public Object getStory(String onlyId){
		return repository.get(onlyId);
	}
	public void removeStory(String onlyId){
		repository.remove(onlyId);
	}
	
	
	
	public List<Object> getServletValues(HttpServletRequest request,boolean isGet) {
		List<Object> objects=new ArrayList<>();
		Enumeration<String> attributeNames = request.getParameterNames();
		while (attributeNames.hasMoreElements()) {
			String[] strings=request.getParameterValues(attributeNames.nextElement());
			if(isGet){
				for (int i=0;i<strings.length;i++) {
					try {
						strings[i]=new String(strings[i].getBytes("iso-8859-1"), "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			objects.add(strings);
		}
		
		return objects;				
	}
	
	public List<Object> getServletNames(HttpServletRequest request) {
		List<Object> objects=new ArrayList<>();
		Enumeration<String> attributeNames = request.getParameterNames();
		while (attributeNames.hasMoreElements()) {
			objects.add(attributeNames.nextElement());
		}
		
		return objects;				
	}
	
	
	public void setServletByName(HttpServletRequest request,Object objectEntity) throws Exception {
		List<Object> names=getServletNames(request);
		List<Object> values=getServletValues(request, (boolean)request.getAttribute("$isGet"));
		
		reflexParse.setServletByName(names, values, objectEntity);
		
	}
	


	public void setServletUploadByName(HttpSession session,Object objectEntity) throws Exception {
		List<Object> values=((ServletUpload)session.getAttribute("upload")).getValues();
		List<Object> names=((ServletUpload)session.getAttribute("upload")).getNames();

		reflexParse.setServletByName(names, values, objectEntity);
	}
	
	
	public void setServletMethod(Map<Thread,RQRPF> map) throws Exception {	
		HttpServletRequest request=map.get(Thread.currentThread()).getRequest();
		HttpServletResponse response=map.get(Thread.currentThread()).getResponse();
		response.setContentType("text/html;charset=utf-8");
		if(request.getAttribute("$isGet")!=null)
			request.setAttribute("$isGet", false);
		else
			request.setAttribute("$isGet", true);
		
		String action=request.getRequestURL().toString();
		action=action.substring(action.lastIndexOf("/")+1,action.length());	
		Boolean isproving=proving.isProving.get(Thread.currentThread());
		if(isproving==null)isproving=false;
		if(isproving){
			if(proving.provingMethod(action)){
				if(proving.proving()){
					enterMethod(action, map.get(Thread.currentThread()).getObject(), request);
					return;
				}else{
					return;
				}
			}
		}
		enterMethod(action, map.get(Thread.currentThread()).getObject(), request);
		
	}
	private void enterMethod(String action,Object object,HttpServletRequest request)throws Exception{
		Method method=reflexParse.getMethod(action, object);
		
		request.setAttribute("returnValue", method.invoke(object, reflexParse.parseObjectMethod(method, getServletValues(request,(boolean)request.getAttribute("$isGet")),getServletNames(request))));
	}
	
	
	public Object getMSG(HttpServletRequest request) {		
		return request.getAttribute("returnValue");
	}
	public String getMethodName(HttpServletRequest request){
		String action=request.getRequestURL().toString();
		return action.substring(action.lastIndexOf("/")+1,action.length());		
	}
	public boolean setServlet(Map<Thread,RQRPF> map) throws Exception {
		HttpServletRequest request=map.get(Thread.currentThread()).getRequest();
		HttpServletResponse response=map.get(Thread.currentThread()).getResponse();
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		request.setAttribute("$isGet", false);
		
		String action=request.getRequestURL().toString();
		action=action.substring(action.lastIndexOf("/")+1,action.length());		
		
		Boolean isproving=proving.isProving.get(Thread.currentThread());
		if(isproving==null)isproving=false;
		if(isproving){
			if(proving.provingMethod(action)){
				if(proving.proving()){
					return enterServlet(action, map, request);
				}else{
					return false;
				}
			}
		}
		
		return enterServlet(action, map, request);
	}
	private boolean enterServlet(String action,Map<Thread,RQRPF> map,HttpServletRequest request) throws Exception{
		Method method=reflexParse.getMethod(action, map.get(Thread.currentThread()).getObject());

		try {		
			Class.forName("org.apache.commons.fileupload.disk.DiskFileItemFactory");
			
			request.getSession().setAttribute("upload", new ServletUpload());//各个用户不同的上传对象，类似于用List<ServletUpload>添加对象;
			if (((ServletUpload)request.getSession().getAttribute("upload")).pdMultipart(map,this)) {
				request.setAttribute("returnValue",method.invoke(map.get(Thread.currentThread()).getObject(), reflexParse.parseObjectMethod(method, ((ServletUpload)request.getSession().getAttribute("upload")).getValues(),((ServletUpload)request.getSession().getAttribute("upload")).getNames()))); 
				return true;
			}else {
				return true;
			}
					
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("\n*********没有文件上传依赖包！**********\n");
			setServletMethod(map);
			return false;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
	}
	
	
	public boolean servletDownload(String fileName,HttpServletRequest request,HttpServletResponse response){
		File file=new File(request.getServletContext().getRealPath(fileName));
		if(!file.exists()){return false;}
		
		try {
			String fileN=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileN, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		InputStream inputStream=null;
		try {
			inputStream=new FileInputStream(file);
			byte[] bs=new byte[1024];
			int len;
			while((len=inputStream.read(bs))!=-1){
				response.getOutputStream().write(bs,0,len);
				response.getOutputStream().flush();
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	public String[] startServletWrite(HttpServletRequest request,String...filePaths) {
		return ((ServletUpload)request.getSession().getAttribute("upload")).startServletWrite(filePaths);
	}
	public long getServletCurProcess(HttpServletRequest request) {
		return ((ServletUpload)request.getSession().getAttribute("upload")).getServletCurProcess();
	}
	public long getServletCurFileSize(HttpServletRequest request) {
		return ((ServletUpload)request.getSession().getAttribute("upload")).getServletCurFileSize();
	}
	public int getServletCurFile(HttpServletRequest request) {
		return ((ServletUpload)request.getSession().getAttribute("upload")).getServletCurFile();
	}
	public long getServletFileSize(HttpServletRequest request,int index){
		return ((ServletUpload)request.getSession().getAttribute("upload")).getServletFileSize(index);
	}
	public int getServletCurCountFile(HttpServletRequest request){
		return ((ServletUpload)request.getSession().getAttribute("upload")).getServletCurCountFile();
	}
}
