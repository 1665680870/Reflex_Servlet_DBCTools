package com.reflex.Object;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class MethodParameterParse extends ParameterParse{
	
	public MethodParameterParse(InstanceReflexParse reflexParse) {
		super(reflexParse);
	}

	@SuppressWarnings(value="all")
	public Object[] parseObject(Method method,List<Object> objects,List<Object> names) throws Exception {
		if (objects==null) {
			System.err.println("\n***********值列表为空！************\n");
			return null;
		}
		if (method==null) {
			System.err.println("\n***********getMethod得到空方法！************\n***********请确认是否有该方法或对象是否正确！************\n");
			return null;
		}
		
		Parameter[] parameters=method.getParameters();	
		List<Class<?>> types=new ArrayList<>();
		
		for (int i = 0; i < parameters.length; i++) {
			types.add(parameters[i].getType());
		}
		
		Object[] objectS=new Object[parameters.length];
		Object[] objectR=parseObjs(types,objects,names);
		for (int i=0;i<objectS.length;i++) {
			if((i+1)>objectR.length){objectS[i]=null;continue;}
			objectS[i]=objectR[i];
		}
		
		types.clear();
		return objectS;
	}
}
