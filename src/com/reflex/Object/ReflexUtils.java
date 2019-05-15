package com.reflex.Object;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflexUtils {
	private InstanceReflexParse reflexParse=null;
	public ReflexUtils(InstanceReflexParse reflexParse) {
		this.reflexParse=reflexParse;
	}
	
	private <T> T setObjectByName(List<Object> name,List<Object> value,T entity,Integer indexmap,Map<Integer, List<Object>> namemap,Map<Integer, List<Object>> valuemap) throws Exception{
		List<Field> fields=reflexParse.getFieldAdmins(entity);
		if(fields==null)return entity;
		int tongji=0;
		for (Field field : fields) {
			int index=name.indexOf(field.getName());
			if(index!=-1){
				tongji++;
				if(reflexParse.isContain(field.getType()))
					field.set(entity, reflexParse.parseObj(field.getType(), value.get(index)));
				else{
					Object newInstance = field.getType().newInstance();indexmap++;
					setObjectByName(namemap.get(indexmap), valuemap.get(indexmap), newInstance, indexmap, namemap,valuemap);
					field.set(entity, newInstance);
				}
				if(tongji==name.size())return entity;
			}
		}	
		fields.clear();
		return entity;
	}
	
	public <T> T setObjectByName(List<Object> names,List<Object> values,T entity) throws Exception{
		Map<String, Map<Integer, List<Object>>> map=getNameValueMap(names, values, entity);
		T object=setObjectByName(map.get("namemap").get(1), map.get("valuemap").get(1), entity, 1, map.get("namemap"),map.get("valuemap"));		
		
		map.clear();
		return object;
	}
	
	public Map<String, Map<Integer, List<Object>>> getNameValueMap(List<Object> names,List<Object> values,Object entity){
		Map<String, Map<Integer, List<Object>>> map=new HashMap<>();
		Map<Integer, List<Object>> namemap=new HashMap<>();

		Map<Integer, List<Object>> valuemap=new HashMap<>();
		
		for (int i=0;i<names.size();i++) {
			if(names.get(i)==null)continue;
			String[] nm=names.get(i).toString().split("[.]");
		
			for(int g=1;g<(nm.length+1);g++){
				if(namemap.get(g)==null){
					namemap.put(g, new ArrayList<>());
					valuemap.put(g, new ArrayList<>());
				}
			}				
			
			for (int j=0;j<nm.length;j++) {
				namemap.get((j+1)).add(nm[j]);
				valuemap.get((j+1)).add(values.get(i));
			}		
		}		
		map.put("namemap", namemap);
		map.put("valuemap", valuemap);
		return map;
	}
	
	
	private Map<String, List<Object>> getObjectByNameValueImp(Object entity,Map<String, List<Object>> namevalue) throws Exception{
		if(entity==null)return namevalue;
		Field[] fields = entity.getClass().getDeclaredFields();
		List<Object> names=namevalue.get("name");
		List<Object> values=namevalue.get("value");
		int index;
		for (Field field : fields) {
			field.setAccessible(true);
			index=names.size();
			if(!reflexParse.isContain(field.getType())){
				getObjectByNameValueImp(field.get(entity), namevalue);
				for (int i=index;i<names.size();i++) {
					names.set(i, field.getName()+"."+names.get(i));
				}
			}else{
				names.add(field.getName());
				values.add(field.get(entity));
			}
		}
		return namevalue;
	}
	private Map<String, Map<Integer, List<Object>>> getObjectByNameValueImpMap(Object entity,Map<String, Map<Integer, List<Object>>> namevalue,int index) throws Exception{
		if(entity==null)return namevalue;
		Field[] fields = entity.getClass().getDeclaredFields();
		Map<Integer, List<Object>> namemap=namevalue.get("namemap");
		Map<Integer, List<Object>> valuemap=namevalue.get("valuemap");
		List<Object> names=new ArrayList<>();
		List<Object> values=new ArrayList<>();

		for (Field field : fields) {
			field.setAccessible(true);

			if(!reflexParse.isContain(field.getType())){
				index++;
				getObjectByNameValueImpMap(field.get(entity), namevalue,index);
				names.add(field.getName());
				values.add(field.get(entity));
			}else{
				names.add(field.getName());
				values.add(field.get(entity));
			}
		}
		namemap.put(index, names);valuemap.put(index, values);
		return namevalue;
	}
	
	public Map<String, List<Object>> getObjectNameValue(Object entity) throws Exception{
		Map<String, List<Object>> namevalue=new HashMap<>();
		namevalue.put("name", new ArrayList<>());
		namevalue.put("value", new ArrayList<>());
		return getObjectByNameValueImp(entity, namevalue);
		
	}
	public Map<String, Map<Integer, List<Object>>> getObjectNameValueMap(Object entity) throws Exception{
		if(entity==null)return null;
		 Map<String, Map<Integer, List<Object>>> map=new HashMap<>();
		 map.put("namemap", new HashMap<Integer,List<Object>>());
		 map.put("valuemap", new HashMap<>());
		 return getObjectByNameValueImpMap(entity, map, 0);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> ObjToType(List<Object> value,Class<T> type){
		if(value==null)return null;
		List<T> values=new ArrayList<>();
		for (Object v : value) {
			values.add((T) reflexParse.parseObj(type, v));
		}	
		return values;		
	}
	
	public <T> List<Object> TypeToObj(List<T> value){
		if(value==null)return null;
		List<Object> objects=new ArrayList<>();
		for (T t : value) {
			objects.add(t);
		}
		return objects;
	}
	public List<Object> ArrayToList(Object[] objects){
		if(objects==null)return null;
		List<Object> list=new ArrayList<>();
		for (Object object : objects) {
			list.add(object);
		}
		return list;
	}
}
