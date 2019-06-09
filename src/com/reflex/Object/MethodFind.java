package com.reflex.Object;

import java.lang.reflect.Method;

public final class MethodFind {
	public MethodFind() {
		// TODO Auto-generated constructor stub
	}
	public Method getMethod(String methodName,Object objectInstance) {
		if (objectInstance==null) {
			return null;
		}
		Method[] method=objectInstance.getClass().getDeclaredMethods();
		for (Method method2 : method) {			
			if (method2.getName().equals(methodName)) {
				if (method2.getModifiers()==1) {
					return method2;
				}else {
					continue;
				}			
			}
		}
		return null;
	}
	public Method getMethodAdmin(String methodName,Object objectInstance) {
		if (objectInstance==null) {
			return null;
		}
		Method[] method=objectInstance.getClass().getDeclaredMethods();
		for (Method method2 : method) {			
			if (method2.getName().equals(methodName)) {
				method2.setAccessible(true);
				return method2;
			}
		}
		return null;
	}
}
