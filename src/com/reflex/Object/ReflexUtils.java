package com.reflex.Object;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
				else if(!field.getType().equals(List.class)){
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
	private List<Integer> getListIndex(List<Object> names){
		List<Integer> listIndex=new ArrayList<>();
	
		for (int i=0;i<names.size();i++) {
			if(names.get(i).toString().contains(".")){
				listIndex.add(i);
			}
		}
		return listIndex;
	}	
	
	
	public <T> T setServletByName(List<Object> names,List<Object> values,T objectEntity) throws Exception{
		setObjectByName(names, values, objectEntity);
		
		List<Field> fields = reflexParse.getFieldByTypeAdmin(List.class, objectEntity.getClass());
		if(fields==null)return objectEntity;
		for (Field field : fields) {
			ParameterizedType type=(ParameterizedType) field.getGenericType();
			Class<?> entityC=(Class<?>) type.getActualTypeArguments()[0];
			if(reflexParse.isContain(entityC))continue;
			
			List<Object> list=new ArrayList<>();
			List<Integer> listIndex=getListIndex(names);
	
			
			Map<Integer,List<Object>> nameM=new HashMap<>();
			Map<Integer,List<Object>> valueM=new HashMap<>();
			Map<Integer, Object> obj=new HashMap<>();
			
			
			for (Integer integer : listIndex) {
				String name=names.get(integer).toString();
				if(field.getName().equals(name.substring(0,name.indexOf(".")))){
					for(int i=0;i<Array.getLength(values.get(integer));i++){
						
						if(obj.get(i)==null){
							obj.put(i, entityC.newInstance());
							nameM.put(i, new ArrayList<>());
							valueM.put(i, new ArrayList<>());
						}
						nameM.get(i).add(name.substring(name.indexOf(".")+1));
						valueM.get(i).add(((Object[])values.get(integer))[i]);
						
					}
					
				}			
			}
			
			Set<Integer> keySet = obj.keySet();
			Iterator<Integer> iterator = keySet.iterator();
			while(iterator.hasNext()){
				Integer integer=iterator.next();
				list.add(setObjectByName(nameM.get(integer), valueM.get(integer), obj.get(integer)));
			}
			listIndex.clear();nameM.clear();valueM.clear();obj.clear();
			
			field.set(objectEntity, list);
		}
		
		
		return objectEntity;
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
		 return getObjectByNameValueImpMap(entity, map, 1);
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
	
	@SuppressWarnings("unused")
	public String ArrayToString(Object[] objects,String...split){
		if(objects==null||objects.length==0)return null;
		
		String split1=null;
		if(split1==null||split1.length()<1)split1=",";
		else split1=split[0];
		StringBuffer buffer=new StringBuffer();
		
		for (Object obj:objects) {
			buffer.append(obj.toString()+split1);
		}
		buffer.deleteCharAt(buffer.length()-1);
		
		String info=buffer.toString();
		buffer.setLength(0);
		
		return info;
	}
	
}
