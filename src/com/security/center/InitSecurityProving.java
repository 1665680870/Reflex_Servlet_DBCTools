package com.security.center;

import java.util.List;

public class InitSecurityProving {
	private SecurityDataCenter dataCenter=null;

	public InitSecurityProving(List<URP> urps){dataCenter=SecurityDataCenter.getDataCenter();setDate(urps);}
	
	private void setDate(List<URP> urps){
		for (int i=0;i<urps.size();i++) {
			URP urp=urps.get(i);
			dataCenter.setRP(urp.getUsername(),urp.getRole(),urp.getPermission());
		}
	}
	
}
