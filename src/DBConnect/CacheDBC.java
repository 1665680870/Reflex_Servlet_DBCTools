package DBConnect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheDBC {
	private List<Object> parent=null;
	private Map<String, Object> objects=null;
	private List<Object> objects2=null;
	private DBCTools dbcTools=null;
	private DBCenter dbCenter=null;
	private static CacheDBC cacheDBC=null;
	private boolean NotreStart=true;
	private CacheDBC(DBCenter dbCenter,DBCTools dbcTools) {
		this.dbcTools=dbcTools;
		parent=new ArrayList<>();
		this.dbCenter=dbCenter;
	}
	protected synchronized static CacheDBC getCacheDBC(DBCenter dbCenter,DBCTools dbcTools) {
		if (cacheDBC==null) {
			cacheDBC=new CacheDBC(dbCenter,dbcTools);
		}
		return cacheDBC;
	}
	public synchronized void setNotreStart(boolean NotreStart) {
		this.NotreStart=NotreStart;
	}
	public boolean CacheOpen(String tabel_view,String ColumIndexName,String... strings) {
		if (NotreStart) {
			if (dbCenter.getTables().contains(tabel_view.trim())) {
				return false;
			}
		}
		
		int cacheMax=100000;
		int curcache=dbcTools.countTable(tabel_view);
		int curIndex=0;
		int forC=(int) Math.ceil(curcache/(double)cacheMax);
		String colums="";
		for (String string : strings) {
			colums+=",T."+string;
		}
		
		ResultSet resultSet=null;
		for (int i = 0; i < forC; i++) {			
			if (curcache<cacheMax) {
				resultSet=dbcTools.selectQueryList("select T.id,T."+ColumIndexName+colums+" from "+tabel_view+" T limit "+curIndex+","+curcache);
				try {
					while (resultSet.next()) {
						Object object=resultSet.getObject(ColumIndexName);
						Object[] objects=new Object[strings.length];
						for (int i1=0;i1<objects.length;i1++) {
							objects[i1]=resultSet.getObject(strings[i1]);
						}
						int id=resultSet.getInt("id");						
						USER_PW user_PW=new USER_PW(object.toString(),id,tabel_view,objects);
						
						addFinaly(object, addNode(tabel_view, addParent(object)), user_PW);						
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					try {
						resultSet.close();
						dbcTools.CloseList();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
				
			}else {
				resultSet=dbcTools.selectQueryList("select T.id,T."+ColumIndexName+colums+" from "+tabel_view+" T limit "+curIndex+","+cacheMax);
				try {
					while (resultSet.next()) {
						Object object=resultSet.getObject(ColumIndexName);
						Object[] objects=new Object[strings.length];
						for (int i1=0;i1<objects.length;i1++) {
							objects[i1]=resultSet.getObject(strings[i1]);
						}
						int id=resultSet.getInt("id");
						USER_PW user_PW=new USER_PW(object.toString(),id,tabel_view,objects);
						
						addFinaly(object, addNode(tabel_view, addParent(object)), user_PW);		
						
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					try {
						resultSet.close();
						dbcTools.CloseList();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				curIndex+=cacheMax;
				curcache-=cacheMax;
			}	
		}

		
		if (NotreStart) {
			dbCenter.getTables().add(tabel_view.trim());
		}
		
		return true;
		
	}
	protected USER_PW getMapValue(String value,String...table) {		
		try{
			return getMap(value, table).get(value);
		}catch(Exception e){
			return null;
		}	
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, USER_PW> findMap(String value){
		int parentsize = value.toString().length();
		String S1 = value.toString().hashCode()+"";
		int mapsize = Integer.parseInt(S1.substring(S1.length()-2,S1.length()))+1; 
		
		Map<String, USER_PW> map = null;
		for(String table:dbCenter.getTables()){		
			try{
				map=(Map<String,USER_PW>)((List<Object>)((Map<String,Object>)parent.get(parentsize-1)).get(table)).get(mapsize-1);				
			}catch(Exception e){
				continue;
			}
		}
		return map;
	}
	
	
	@SuppressWarnings("unchecked")
	protected Map<String, USER_PW> getMap(String value,String...table) {
		int parentsize = value.toString().length();
		String S1 = value.toString().hashCode()+"";
		int mapsize = Integer.parseInt(S1.substring(S1.length()-2,S1.length()))+1; 
		if(table.length>0){	
			try{
				return (Map<String,USER_PW>)((List<Object>)((Map<String,Object>)parent.get(parentsize-1)).get(table[0])).get(mapsize-1);				
			}catch(Exception e){
				return null;
			}
		}else{
			return findMap(value);
		}
	}
	@SuppressWarnings("unchecked")
	protected void remove(String value,String table) {
		int parentsize = value.toString().length();
		String S1 = value.toString().hashCode()+"";
		int mapsize = Integer.parseInt(S1.substring(S1.length()-2,S1.length()))+1; 
		
		try {
			((Map<String,USER_PW>)((List<Object>)((Map<String,Object>)parent.get(parentsize-1)).get(table)).get(mapsize-1)).remove(value);
		} catch (Exception e) {
			System.err.println("\n*******Map为空！已自动跳过********\n");
		}
		
		
	}
	protected synchronized boolean removeTable(DBCTools dbcTools,String table) {
		String sql="select T."+ dbCenter.getTable_GetIndexName().get(table)+" from "+table+" T";
		ResultSet resultSet=dbcTools.selectQueryList(sql);
		try {
			while (resultSet.next()) {
				remove(resultSet.getObject( dbCenter.getTable_GetIndexName().get(table)).toString(),table);				
			}
			dbCenter.getTables().remove(table);
			
			return true;
		}catch(SQLException e){
			return false;
		}finally {
			try {
				resultSet.close();
				dbcTools.CloseList();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized void reStartCache(DBCTools dbcTools) {
		parent.clear();
		NotreStart=false;
		for (String table : dbCenter.getTables()) {
			CacheOpen(table, dbCenter.getTable_GetIndexName().get(table), dbCenter.getTable_GetName().get(table));
		}	
		NotreStart=true;
		System.gc();
	}
	
	protected void addMapValue(Object object,USER_PW user_PW,String table) {
		addFinaly(object, addNode(table, addParent(object)), user_PW);
	}
	private synchronized int addParent(Object object) {
		int size=object.toString().length();
		if (parent.size()<size) {
			int curSize=size-parent.size();
			for (int i = 0; i < curSize; i++) {
				parent.add(new HashMap<String, Object>());
			}
		}
		return size;
	}
	@SuppressWarnings("unchecked")
	private synchronized String addNode(String table,int sizeAdd) {
		objects=(Map<String, Object>)parent.get(sizeAdd-1);
		if(objects.get(table)==null){
			objects.put(table, new ArrayList<Object>());
		}
		
		return table;
	}
	@SuppressWarnings("unchecked")
	private synchronized void addFinaly(Object object,String table,USER_PW user_PW) {
		String S1 = object.toString().hashCode()+"";
		int C1 = Integer.parseInt(S1.substring(S1.length()-2,S1.length()))+1;
		objects2=(List<Object>)objects.get(table);
		if (objects2.size()<C1) {
			int curSize=C1-objects2.size();
			for (int i = 0; i < curSize; i++) {
				objects2.add(new HashMap<String,USER_PW>());
			}
		}
		((Map<String, USER_PW>)objects2.get(C1-1)).put(object.toString(), user_PW);
	}
	public synchronized void close(){
		parent.clear();
		parent=null;
		dbCenter.close();
	}
}
