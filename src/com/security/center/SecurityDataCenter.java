package com.security.center;

import java.util.HashMap;
import java.util.Map;

public class SecurityDataCenter {
	private Map<String, String> rMap=null;
	private Map<String, String> pMap=null;
	private static SecurityDataCenter dataCenter=null;
	private SecurityDataCenter() {
		rMap=new HashMap<>();
		pMap=new HashMap<>();
	}
	
	public synchronized static SecurityDataCenter getDataCenter(){
		if(dataCenter==null)dataCenter=new SecurityDataCenter();
		return dataCenter;
	}
	
	public String getRole(String username){
		return rMap.get(username);
	}
	public String getPermission(String role){
		return pMap.get(role);
	}
	public void setRole(String username,String role){
		rMap.put(username, role);
	}
	public void setPermission(String role,String permission){
		pMap.put(role, permission);
	}
	public void setRP(String username,String role,String permission){
		rMap.put(username, role);
		pMap.put(rMap.get(username), permission);
	}
	public Map<String, String> getRoleMap(){
		return rMap;
	}
	public Map<String, String> getPermissionMap(){
		return pMap;
	}

}
