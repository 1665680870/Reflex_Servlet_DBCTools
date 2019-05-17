package com.reflex.Object;

import java.util.List;


public class ParameterParse extends ParameterParseOBJ{
	private InstanceReflexParse reflexParse=null;
	public ParameterParse(InstanceReflexParse reflexParse) {
		this.reflexParse=reflexParse;
	}
	
	public Object initNull(Class<?> type,Object object){
		return init(type, object);
	}
	
	public Object parseObj(Class<?> type,Object value) {
		if (value==null) {
			return init(type, value);
		}
		if (type.isArray()) {
			if (value.getClass().isArray()) {
				return super.startParseS(type, value);
			}else {
				return super.startParseS1(type, value);
			}
		}else {
			if (value.getClass().isArray()) {
				return super.startParse1(type, value);
			}else {
				return super.startParse(type, value);	
			}
		}
	}
	@SuppressWarnings(value="all")
	public Object[] parseObjs(List<Class<?>> types,List<Object> values,List<Object>...names) throws Exception {
		if (values==null) {
			return null;
		}
		int typesCount=0;
		if(types!=null)typesCount=types.size();
		
		Object[] object=new Object[values.size()];
		for (int i = 0; i <values.size(); i++) {
			if (i<typesCount) {
				if(names==null||names.length==0||reflexParse.isContain(types.get(i))){
					if (values.get(i)==null) {
						object[i]=init(types.get(i), values.get(i));
					}else {					
						if (types.get(i).isArray()) {
							if (values.get(i).getClass().isArray()) {
								object[i]=super.startParseS(types.get(i), values.get(i));
							}else {
								object[i]=super.startParseS1(types.get(i), values.get(i));
							}
						}else {
							if (object.getClass().isArray()) {
								object[i]=super.startParse1(types.get(i), values.get(i));
							}else {
								object[i]=super.startParse(types.get(i), values.get(i));
							}
						}					
					}		
				}else{
					Object newInstance = types.get(i).newInstance();
					reflexParse.setServletByName(names[0], values, newInstance);
					object[i]=newInstance;
				}
			}else {
				object[i]=values.get(i);
			}
		}
		
		return object;		
	}
	
}
