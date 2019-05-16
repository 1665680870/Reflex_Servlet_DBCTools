package DBConnect;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.reflex.Object.InstanceReflexParse;

public class UpdateDBC {
	private DBCTools dbcTools=null;
	private InstanceReflexParse reflexParse=null;
	public UpdateDBC(DBCTools dbcTools) {
		this.dbcTools=dbcTools;
		this.reflexParse=InstanceReflexParse.getReflexParse();
	}
	
	private void setObjectByName(List<Object> name,List<Object> value,Object entity,String TableName,Integer indexmap,Map<Integer, List<Object>> namemap,Map<Integer, List<Object>> valuemap,Map<String, String> tableId,Map<String, Integer> mainId) throws Exception{
		ResultSet resultSet=dbcTools.selectQueryList("desc "+TableName);
		synchronized(this){
			if(resultSet==null)createTable(TableName, entity);
			resultSet=dbcTools.selectQueryList("desc "+TableName);
		}
		
		boolean flag=true;
		String tableValue="",tableField="";
		int isId=-1;
		for(int i=0;i<value.size();i++){
			if(value.get(i)==null)continue;
			if(reflexParse.isContain(value.get(i).getClass())){
				while (resultSet.next()) {
					if(name.get(i).equals(resultSet.getString("field"))){
						if("id".equalsIgnoreCase(name.get(i).toString())){
							if(value.get(i)==null||"0".equals(value.get(i).toString())){
								isId=-1;
							}else{
								isId=i;
							}
						}else{
							if(value.get(i)==null)continue;
							if (flag) {
								tableValue+="'"+value.get(i)+"'";
								tableField+=name.get(i);
								flag=false;
							}else {
								tableValue+=",'"+value.get(i)+"'";
								tableField+=","+name.get(i);
							}
						}
						break;
					}else if(resultSet.getString("field").contains(".")){					
						tableId.put(resultSet.getString("field"), TableName.toLowerCase());
					}
				}
			}else{
				indexmap++;
				setObjectByName(namemap.get(indexmap), valuemap.get(indexmap),value.get(i), value.get(i).getClass().getSimpleName(), indexmap, namemap,valuemap,tableId,mainId);
				indexmap--;
			}
		}
		
		if (isId!=-1) {
			String valueV = "";

			String[] tableV=tableValue.split(",");
			String[] tableF=tableField.split(",");
			
				for (int j = 0; j < tableF.length; j++) {
					valueV+=tableF[j]+"="+tableV[j]+",";
				}
				valueV=valueV.substring(0, valueV.trim().length()-1);
										
			dbcTools.update("update "+TableName+" set "+valueV+" where id="+value.get(isId).toString());
			
		}else{
			dbcTools.update("insert into "+TableName+" ("+tableField+") values ("+tableValue+")",TableName);
			mainId.put(TableName.toLowerCase(), dbcTools.getId());
		}
		
	}
	public void setObjectByName(Object entity,String...tableName) throws Exception{
		String TableName=getTableName(entity.getClass(), tableName);
		
		Map<String, Map<Integer, List<Object>>> objectByNamemapValuemap = reflexParse.getObjectNameValueMap(entity);
		Map<String, String> tableId=new HashMap<>();
		Map<String, Integer> mainId=new HashMap<>();
		Map<Integer, List<Object>> namemap=objectByNamemapValuemap.get("namemap");
		Map<Integer, List<Object>> valuemap=objectByNamemapValuemap.get("valuemap");
		setObjectByName(namemap.get(1), valuemap.get(1),entity, TableName, 1, namemap, valuemap,tableId,mainId);
		
		if(tableId.size()>0){
			
			Set<String> keySet = tableId.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()){
				String left = iterator.next();
				
				int Cid=mainId.get(tableId.get(left));
		
				int Pid=mainId.get(left.substring(0, left.indexOf(".")).toLowerCase());
				
				Object value=dbcTools.getInfoById(left.substring(left.indexOf(".")+1), Pid, left.substring(0, left.indexOf(".")));

				
				if(value!=null)
					dbcTools.update("update "+tableId.get(left)+" set `"+left+"`='"+value+"' where id="+Cid);
			}
		}
		
		dbcTools.CloseList();
		dbcTools.removeId();
		tableId.clear();mainId.clear();
		objectByNamemapValuemap.clear();
	}
	
	public <T> T getSingeByName(Integer id,T entity,String...tableName) throws Exception{
		String TableName=getTableName(entity.getClass(), tableName);
		if(TableName==null||id==null)return null;
		
		Map<String, List<Object>> map = parsefield(TableName, id);
		return reflexParse.setObjectByName(map.get("name"),map.get("value"),entity);
	}
	
	private Map<String, List<Object>> parsefield(String tableName,Integer id){
		Map<String, List<Object>> map=new HashMap<>();
		
		Object[] valuesa = dbcTools.getSingInfoOneList("*", id, tableName);
		List<Object> values = reflexParse.ArrayToList(valuesa);
		
		
		return parseListName(map,dbcTools.getDescNameSize(tableName),dbcTools.getDescName(tableName), 1, "",values,id);

	}
	
	
	private Map<String, List<Object>> parseListName(Map<String, List<Object>> contain,int size, List<Object> names,int index,String pre,List<Object> values,int id){
		List<Object> nms=new ArrayList<>();
		for (int i=0;i<size;i++) {
			String value=names.get(i).toString();
			if(value.contains(".")){
				if("id".equalsIgnoreCase(value.substring(value.lastIndexOf(".")+1))&&values.get(i)!=null){
					nms.add(value.substring(value.indexOf(".")+1));
					pre+=value.substring(0, value.indexOf(".")+1);
					parseListName(contain, 1,names, ++index,pre,values,(int)values.get(i));index--;
				}
				names.remove(i);
				values.remove(i);
				i--;size--;
			}
			
			String[] tablesplit=pre.split("[.]");
			if(index==1)continue;
			String tableName=tablesplit[tablesplit.length-1];
			List<Object> descName = dbcTools.getDescName(tableName);
			for (Object name : descName) {
				names.add(pre+name);
			}
			Object[] tableValue = dbcTools.getSingInfoOneList("*", id, tableName);
			for (Object v: tableValue) {
				values.add(v);
			}
			pre="";
			nms.clear();
		}
		contain.put("name", names);
		contain.put("value",values);
		return contain;
	}
	
	
	
	private String getTableName(Class<?> entity,String...tableName){
		if(entity==null){
			return null;
		}
		if (tableName.length>0) {
			return tableName[0];
		}else {
			return entity.getSimpleName();
		}
	}
	
	
	
	
	private void createTable(String tableName,Object entity) {		
		Field[] fields=entity.getClass().getDeclaredFields();
		String sqlField="";
		for (int i = 0; i < fields.length; i++) {
			if (!("id".equalsIgnoreCase(fields[i].getName()))) {
				if (fields[i].getType().equals(int.class)||fields[i].getType().equals(Integer.class)) {
					sqlField+=fields[i].getName()+" int(11) DEFAULT NULL,";
				}else if(fields[i].getType().equals(double.class)||fields[i].getType().equals(Double.class)){
					sqlField+=fields[i].getName()+" double(16,2) DEFAULT NULL,";
				}else{
					sqlField+=fields[i].getName()+" varchar(255) DEFAULT NULL,";
				}
				
			}
		}
			
		String sql="CREATE TABLE "+tableName+" ("+
				"`id` int(11) NOT NULL AUTO_INCREMENT,"+sqlField+
				"PRIMARY KEY (`id`)"+
				") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
		dbcTools.update(sql);
	}
	
	private <T> List<T> getListByNameMoudel(Class<T> entity,int index,int size,String...tableName){
		String TableName=getTableName(entity, tableName);
		List<T> ts=new ArrayList<>();
		
		ResultSet resultSet=null;
		if(size==0){
			resultSet=dbcTools.selectQueryList("select id from "+TableName);	
		}else{
			resultSet=dbcTools.selectQueryList("select id from "+TableName+" limit "+index+","+size);	
		}
		
		try {
			while(resultSet.next()){
				ts.add(getSingeByName(resultSet.getInt("id"), entity.newInstance(), TableName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ts;
	}
	
	
	public <T> List<T> getListByName(Class<T> entity,String...tableName){
		return getListByNameMoudel(entity, 0, 0, tableName);
	}
	public <T> List<T> getListByNameLimit(Class<T> entity,int index,int size,String...tableName){
		return getListByNameMoudel(entity, index, size, tableName);
	}
	
}
	

