package com.security.center;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.reflex.Object.InstanceReflexParse;

public class RolePermissionProving {
	private SecurityDataCenter dataCenter=null;
	private Map<Thread, Map<String, String>> map=null;
	private Map<Thread, List<String>> methodmap=null;
	public Map<Thread, Boolean> isProving=null;
	private InstanceReflexParse reflexParse=null;
	private static RolePermissionProving proving=null;
	
	private RolePermissionProving(){
		dataCenter=SecurityDataCenter.getDataCenter();
		map=new HashMap<>();
		methodmap=new HashMap<>();
		isProving=new HashMap<>();
		reflexParse=InstanceReflexParse.getReflexParse();
	}
	
	public synchronized static RolePermissionProving getRolePermission(){
		if(proving==null)proving=new RolePermissionProving();
		return proving;
	}
	
	public RolePermissionProving setRole(String role){
		getMap().put("role", role);
		return this;
	}
	public RolePermissionProving setUsername(String username){
		getMap().put("user", username);
		return this;
	}
	public RolePermissionProving setPermission(String permission){
		getMap().put("permission", permission);
		return this;
	}
	public RolePermissionProving setMethod(String...methodName){
		if(methodName==null)return this;
		for (String method : methodName) {
			getMethodMap().add(method);
		}
		return this;
	}
	public void setProving(){
		isProving.put(Thread.currentThread(), true);
	}
	
	public boolean proving(){
		try{
			if(map.get(Thread.currentThread())==null)return true;
			String username=getMap().get("user");
			if(username==null)return true;
			if(provingRole(getMap().get("role"), username)&&provingPermission(getMap().get("permission"), username)){return true;}
			
			return false;
		}finally{
			map.clear();methodmap.clear();
		}
		
	}
	
	private boolean provingRole(String role,String username){
		if(role==null)return true;
		String roleP=dataCenter.getRole(username);
		if(roleP==null)return false;
		else{
			if(roleP.equals(role))return true;
			else{return false;}
		}
	}
	private boolean provingPermission(String permission,String username){
		if(permission==null)return true;
		String permissionP=dataCenter.getPermission(username);
		if(permissionP==null)return false;
		else{
			if(reflexParse.isContainAll(permissionP, permission))return true;
			else{return false;}
		}
	}
	
	public boolean provingMethod(String methodname){
		if(methodname==null)return false;
		if(getMethodMap().contains(methodname))return true;
		return false;
	}
	
	
	public Map<String, String> getMap(){
		if(map.get(Thread.currentThread())==null)map.put(Thread.currentThread(), new HashMap<>());
		return map.get(Thread.currentThread());
	}
	public List<String> getMethodMap(){
		if(methodmap.get(Thread.currentThread())==null)methodmap.put(Thread.currentThread(), new ArrayList<>());
		return methodmap.get(Thread.currentThread());
	}
}
