package com.reflex.Object;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import DBConnect.DBCTools;

public final class FieldFind {

	public FieldFind() {
		// TODO Auto-generated constructor stub
	}
	public Field getField(String fieldName,Object objectInstance) {
		if (objectInstance==null) {
			return null;
		}
		Field[] fields=objectInstance.getClass().getDeclaredFields();
		for (Field field : fields) {			
			if (field.getName().equals(fieldName)) {
				if (field.getModifiers()==1) {
					return field;
				}else {
					continue;
				}			
			}
		}
		return null;
	}
	public List<Field> getFieldAdmins(Object objectEntity) {
		if(objectEntity==null)return null;
		List<Field> objects=new ArrayList<>();

		for (Field field : objectEntity.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			objects.add(field);
		}
		
		return objects;				
	}
	
	
	
	
	public Field getFiledAdmin(String fieldName,Object objectInstance) {
		if (objectInstance==null) {
			return null;
		}
		Field[] fields=objectInstance.getClass().getDeclaredFields();
		for (Field field : fields) {			
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field;
			}
		}
		return null;
	}
	public List<Field> getFieldByTypeAdmin(Class<?> findType,Class<?> entity){
		if(entity==null||findType==null)return null;
		List<Field> fieldType=new ArrayList<>();
		Field[] fields=entity.getDeclaredFields();
		for (Field field : fields) {			
			if (field.getType().equals(findType)) {
				field.setAccessible(true);
				fieldType.add(field);
			}
		}
		if(fieldType.size()==0)fieldType=null;
		return fieldType;
	}
	
	public int setNullAll(Class<?> findType,Class<?> changeClass,Object entity,int count) throws Exception{

		List<Field> thisField = getFieldByTypeAdmin(DBCTools.class,changeClass);
		if(changeClass!=null)count=setNullAll(findType, changeClass.getSuperclass(),entity,count);
		else return count;
		
		if(thisField==null)return count;
		for(Field field:thisField){
			field.set(entity, null);		
			count++;
		}
		
		thisField.clear();
		return count;
	}
	
}
