package com.reflex.Object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public class InstanceReflexParse {
	//实例化this就创建
	private MethodParameterParse parameterParseMethod=null;
	private MethodFind methodFind=null;
	private FieldFind fieldFind=null;
	private ParameterParse parameterParse=null;
	private ReflexUtils reflexUtils=null;
	
	
	private InstanceReflexParse(){
		parameterParseMethod=new MethodParameterParse(this);
		methodFind=new MethodFind();
		fieldFind=new FieldFind();
		parameterParse=new ParameterParse(this);
		reflexUtils=new ReflexUtils(this);
	}	
	
	
	//得到实例
	private static InstanceReflexParse reflexParse=null;
	public synchronized static InstanceReflexParse getReflexParse() {
		if (reflexParse==null) {
			reflexParse=new InstanceReflexParse();
		}
		return reflexParse;
	}
	
	
	
	//反射
	@SuppressWarnings(value="all")
	public Object[] parseObjectMethod(Method method,List<Object> values,List<Object> names) throws Exception {
		return parameterParseMethod.parseObject(method,values,names);
	}
	
	
	public Method getMethod(String methodName,Object objectInstance) {
		return methodFind.getMethod(methodName,objectInstance);
	}
	public Method getMethodAdmin(String methodName,Object objectInstance) {
		return methodFind.getMethodAdmin(methodName,objectInstance);
	}
	
	
	public Field getField(String fieldName,Object objectInstance) {
		return fieldFind.getField(fieldName, objectInstance);
	}
	public Field getFiledAdmin(String fieldName,Object objectInstance) {
		return fieldFind.getFiledAdmin(fieldName, objectInstance);
	}
	public List<Field> getFieldByTypeAdmin(Class<?> findType,Class<?> entity){
		return fieldFind.getFieldByTypeAdmin(findType, entity);
	}
	public Integer setNullAll(Class<?> findType,Class<?> Entity,Object entity,Integer count) throws Exception{
		return fieldFind.setNullAll(findType, Entity, entity, count);
	}
	public List<Field> getFieldAdmins(Object objectEntity) {
		return fieldFind.getFieldAdmins(objectEntity);
	}
	
	
	public Object parseObj(Class<?> type,Object value) {
		return parameterParse.parseObj(type, value);
	}
	@SuppressWarnings(value="all")
	public Object[] parseObjs(List<Class<?>> types,List<Object> values,List<Object>...names) throws Exception {
		return parameterParse.parseObjs(types, values,names);
	}
	public boolean isContain(Type type){
		return parameterParse.isContain(type);
	}
	public Object initNull(Class<?> type,Object value){
		return parameterParse.initNull(type, value);
	}
	
	public <T> T setObjectByName(List<Object> names,List<Object> value,T entity) throws Exception{
		return reflexUtils.setObjectByName(names, value, entity);
	}
	public <T> T setServletByName(List<Object> names,List<Object> values,T objectEntity) throws Exception{
		return reflexUtils.setServletByName(names, values, objectEntity);
	}
	public Map<String, List<Object>> getObjectNameValue(Object entity) throws Exception{
		return reflexUtils.getObjectNameValue(entity);
	}
	public Map<String, Map<Integer, List<Object>>> getObjectNameValueMap(Object entity) throws Exception{
		return reflexUtils.getObjectNameValueMap(entity);
	}
	public <T> List<T> ObjToType(List<Object> value,Class<T> type){
		return reflexUtils.ObjToType(value, type);
	}
	public <T> List<Object> TypeToObj(List<T> value){
		return reflexUtils.TypeToObj(value);
	}
	public List<Object> ArrayToList(Object[] objects){
		return reflexUtils.ArrayToList(objects);
	}
	public String ArrayToString(Object[] objects,String...split){
		return reflexUtils.ArrayToString(objects, split);
	}
	public boolean isContainAll(String main,String fit){
		return reflexUtils.isContainAll(main, fit);
	}
}
