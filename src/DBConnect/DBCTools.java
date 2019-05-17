package DBConnect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.reflex.Object.InstanceReflexParse;

public class DBCTools {
	private String SRC;
	private String onlyId;
	private String DRIVER="com.mysql.jdbc.Driver";
	private Connection CONNECTION=null;
	private Statement STATEMENT=null;
	private Map<Thread, List<Statement>> Mstatements=null;
	private Map<String, ResultSet> sqlcache=null;
	private Map<String, Statement> sqlcacheStatement=null;
	private DBCenter dbCenter=null;
	private List<Object> list=null;
	private UpdateDBC updateDBC=null;
	private static DBCTools tDbcTools=null;
	private CacheDBC cacheDBC=null;
	private InstanceReflexParse reflexParse=null;
	private String password,user,port,database,host;
	private static int poolsize=10;
	private static File fileParent=null;
	private boolean isReconnect=false;
	private Map<Thread, Integer> tableId=null;
	static{
		String path=DBCTools.class.getResource("/").getPath().substring(1);
		try {
			path=URLDecoder.decode(path,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fileParent=new File(path+File.separator+"DBC_DATE");
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		File file=new File(fileParent+File.separator+"DBCTools_core.date");
		File defaultFile=new File(fileParent+File.separator+"default.date");
		BufferedWriter writer=null;
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					writer=new BufferedWriter(new FileWriter(file));
					writer.write("10");
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					try {
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if (!defaultFile.exists()) {
			try {
				defaultFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					writer=new BufferedWriter(new FileWriter(defaultFile));
					writer.write("<DBC-host></DBC-host>\r\n"+
							"<DBC-port></DBC-port>\r\n"+
							"<DBC-database></DBC-database>\r\n"+
							"<DBC-username></DBC-username>\r\n"+
							"<DBC-password></DBC-password>");
					writer.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					try {
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(file));
			try {
				poolsize=Integer.parseInt(reader.readLine().trim());
				if (poolsize>50) {
					poolsize=50;
				}else if (poolsize<1) {
					poolsize=1;
				}
			} catch (Exception e) {
				e.printStackTrace();
				file.delete();
				System.exit(-1);
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (@SuppressWarnings("hiding") IOException e) {
			e.printStackTrace();
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}	
	public void setIsReconnect(boolean isReconnect){
		this.isReconnect=isReconnect;
	}
	
	private DBCTools(String onlyID) {
		Mstatements=new HashMap<>();
		dbCenter=new DBCenter();
		updateDBC=new UpdateDBC(this);
		sqlcache=new HashMap<>();
		sqlcacheStatement=new HashMap<>();
		reflexParse=InstanceReflexParse.getReflexParse();
		this.onlyId=onlyID;
		list=new ArrayList<>();
		tableId=dbCenter.getTableId();
	}
	
	public void sqlCacheClear(){
		sqlcacheStatement.clear();
		sqlcache.clear();
	}
	
	private void selectPluls(){
		if(isReconnect){
			reconnection();		
		}
	}
	
	
	public ResultSet selectQueryAllCache(String tableViewName,Integer...fieldSize) {
		if (isNotConnectNull()) {
			try {		
				selectPluls();
				
				if(sqlcache.containsKey(tableViewName))return sqlcache.get(tableViewName);
				else{
					Statement statement=getStatement();
					sqlcacheStatement.put(tableViewName, statement);
					sqlcache.put(tableViewName,statement.executeQuery(parseSQL("select * from "+tableViewName,fieldSize)));
					return sqlcache.get(tableViewName);
				}					

				
			} catch (SQLException e) {
				return null;
			}
		}else {
			return null;
		}
	}
	public ResultSet selectQueryAllList(String tableViewName,Integer...fieldSize) {
		if (isNotConnectNull()) {
			try {
				selectPluls();
				
				String sql=parseSQL("select * from "+tableViewName,fieldSize);
				if (Mstatements.get(Thread.currentThread())!=null) {
					Mstatements.get(Thread.currentThread()).add(CONNECTION.createStatement());
					return Mstatements.get(Thread.currentThread()).get(Mstatements.get(Thread.currentThread()).size()-1).executeQuery(sql);
				}else {
					Mstatements.put(Thread.currentThread(),new ArrayList<Statement>());
					Mstatements.get(Thread.currentThread()).add(CONNECTION.createStatement());
					return Mstatements.get(Thread.currentThread()).get(Mstatements.get(Thread.currentThread()).size()-1).executeQuery(sql);
				}
			} catch (SQLException e) {
				return null;
			}
		}else {
			return null;
		}
	}
	
	private String parseFormat(String oldString) {
		oldString=oldString.replace("'", "");
		return oldString.replace("\"", "");
	}
	
	public int countTable(String tableViewName) {
		if (isNotConnectNull()) {
			Statement statement=getStatement();
			ResultSet resultSet=null;
			try {						
				resultSet=statement.executeQuery("select count(*) as C from "+tableViewName);
				if (resultSet.next()) {					
					int count=resultSet.getInt("C");
					return count;
				}else {
					return 0;
				}				
			} catch (SQLException e) {
				return 0;
			} finally {
				closeQuery(statement, resultSet);
			}
		}else {
			return 0;
		}
	}
	public ResultSet selectQueryList(String sql,Integer...fieldSize) {
		if (isNotConnectNull()) {
			try {
				selectPluls();
				
				sql=parseSQL(sql,fieldSize);
				if (Mstatements.get(Thread.currentThread())!=null) {
					Mstatements.get(Thread.currentThread()).add(CONNECTION.createStatement());
					return Mstatements.get(Thread.currentThread()).get(Mstatements.get(Thread.currentThread()).size()-1).executeQuery(sql);
				}else {
					Mstatements.put(Thread.currentThread(),new ArrayList<Statement>());
					Mstatements.get(Thread.currentThread()).add(CONNECTION.createStatement());
					return Mstatements.get(Thread.currentThread()).get(Mstatements.get(Thread.currentThread()).size()-1).executeQuery(sql);
				}
			} catch (SQLException e) {
				return null;
			}
		}else {
			return null;
		}
	}
	
	public boolean reconnection(){
		close(true);
		return createConnect(host, database, port, user, password);
	}
	
	
	
	public ResultSet selectQueryCache(String sql,Integer...fieldSize) {
		if (isNotConnectNull()) {
			try {
				selectPluls();
				
				if(sqlcache.containsKey(sql))return sqlcache.get(sql);
				else{
					Statement statement=getStatement();
					sqlcacheStatement.put(sql, statement);
					sqlcache.put(sql,statement.executeQuery(parseSQL(sql,fieldSize)));
					return sqlcache.get(sql);
				}
			} catch (SQLException e) {
				return null;
			}
		}else {
			return null;
		}
	}
	public Statement getStatement() {
		if (isNotConnectNull()) {
			try {
				return CONNECTION.createStatement();
			} catch (SQLException e) {
				return null;
			}
		}else {
			return null;
		}	
	}

	public void CloseStatement(Statement statement) {
		if (statement!=null) {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public boolean callProcedure(String procedure) {
		if (isNotConnectNull()) {
			CallableStatement callableStatement=null;
			try {
			    callableStatement=CONNECTION.prepareCall(procedure);
				return callableStatement.execute();
			} catch (SQLException e) {
				return false;
			}finally {
				try {
					CONNECTION.commit();
					callableStatement.close();
					sqlCacheClear();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}else {
			return false;
		}
	}
	private void setId(String tableName){
		if (isNotConnectNull()) {
			Statement statement=getStatement();
			ResultSet resultSet=null;
			try {						
				resultSet=statement.executeQuery("select Max(id) as M from "+tableName);
				if (resultSet.next()) {					
					tableId.put(Thread.currentThread(), resultSet.getInt("M"));
				}				
			} catch (SQLException e) {
				return;
			} finally {
				closeQuery(statement, resultSet);
			}
		}
	}
	public int getId(){
		return tableId.get(Thread.currentThread());
	}
	public void removeId(){
		tableId.remove(Thread.currentThread());
	}
	
	public int update(String sql,String tableName,Integer fieldSize){
		return update(parseSQL(sql,fieldSize), tableName);
	}
	
	public int update(String sql){
		return update(sql, new String[]{});
	}
	
	public int update(String sql, String...tableName) {
		if (isNotConnectNull()) {
			if (cacheDBC!=null) {
				return flushCache(parseSQL(sql));
			}else {
				try {				
					int i=0;
					synchronized(this){
						if(tableName!=null&&tableName.length>0){
							i=STATEMENT.executeUpdate(parseSQL(sql));
							CONNECTION.commit();
							setId(tableName[0]);
						}else{
							i=STATEMENT.executeUpdate(parseSQL(sql));
							CONNECTION.commit();
						}
					}					
					return i;
				} catch (SQLException e) {
					e.printStackTrace();
					return 0;
				}finally {
					sqlCacheClear();
				}
			}
			
		}else {
			return 0;
		}
	}
	public int update(String sql,Integer...fieldSize){
		return update(parseSQL(sql,fieldSize), fieldSize);
	}

	public int deleteId(Integer id,String tableName){
		return delete("id="+id.toString(), tableName);		
	}
	public int delete(String condition,String tableName,Integer...fieldSize){
		if(isNotConnectNull()){
			return update("delete from "+tableName+" where "+condition,fieldSize);		
		}else{
			return 0;
		}
	}
	public Object getInfoById(String columName,int id,String tableName){
		return getSingeInfo(columName,"id="+id,tableName,1);		
	}
	
	
	@SuppressWarnings("finally")
	public Object getSingeInfo(String columName,String condition,String tableName,Integer...fieldSize){
		if(isNotConnectNull()){
				Object object=null;				
				Statement statement=getStatement();
				ResultSet resultSet=selectQuery(statement,"select "+columName+" from "+tableName+" where "+condition+" group by "+columName,fieldSize);
				try {
					if(resultSet.next()){
						object=resultSet.getObject(columName);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					closeQuery(statement, resultSet);
					return object;
				}
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("finally")
	public Object[] getSingInfoOneList(String columName,int id,String tableName){
		if(isNotConnectNull()){
			Object[] object=null;				
			Statement statement=getStatement();
			ResultSet resultSet=selectQuery(statement,"select "+columName+" from "+tableName+" where id="+id+" group by id");
			
			Object[] colums=columName.split("[,]");
			
			if("*".equals(colums[0])){
				object=new Object[getDescNameSize(tableName)];
				List<Object> descName = getDescName(tableName);
				colums= descName.toArray();
			}
			else{ object=new Object[(colums.length)];}
			int index=object.length;
			
			try {
				int tj=0;
				if(resultSet.next()){
					for (int i=0;i<index;i++) {
						object[i]=resultSet.getObject(colums[i].toString());
					}
					tj++;
				}
				if(tj==0)return null;
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				closeQuery(statement,resultSet);
				return object;
			}
		}else{
			return null;
		}
	}
	
	
	@SuppressWarnings("finally")
	public Object[] getArrayInfo(String columName,String condition,String tableName){
		if(isNotConnectNull()){
			Object[] object=new Object[countTable(tableName+" where "+condition)];
			Statement statement=getStatement();
			ResultSet resultSet=selectQuery(statement,"select "+columName+" from "+tableName+" where "+condition);
			try {
				int i=0;
				while(resultSet.next()){
					object[i]=resultSet.getObject(columName);
					i++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				closeQuery(statement, resultSet);
				return object;
			}
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updateList(List<String> sqList,List<Integer>...fieldSize){
		if (isNotConnectNull()) {			
			try {
				boolean is=false;
				if(fieldSize!=null&&fieldSize.length>0)is=true;
				for (int i = 0; i < sqList.size(); i++) {
					if(is)STATEMENT.addBatch(parseSQL(sqList.get(i),fieldSize[0].get(i)));
					else STATEMENT.addBatch(parseSQL(sqList.get(i)));
					if (i%5000==0) {
						STATEMENT.executeBatch();
						CONNECTION.commit();
					}
				}
				STATEMENT.executeBatch();
				CONNECTION.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				sqlCacheClear();
				if (cacheDBC!=null) {
					cacheDBC.reStartCache(this);
				}
			}
		}
	}
	public static String parseSQL(String sql,Integer...fieldSize){
		if(fieldSize==null||fieldSize.length==0)return sql;
		
		int count=countEncoding(sql,"'");
		if(count/2!=fieldSize[0])return "";
		
		return sql;
	}
	
	private static int countEncoding(String value,String en){
		int count=0;
		while(value.indexOf(en)!=-1){
			count++;
		}
		return count;
	}
	
	
	public List<Object> getDescName(String tableName) {
		List<Object> names=new ArrayList<>();
		Statement st=getStatement();
		ResultSet resultSet = selectQuery(st,"desc "+tableName);
		
		try {
			while(resultSet.next()){
				names.add(resultSet.getObject("field"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeQuery(st, resultSet);
		}

		return names;
	}
	public ResultSet selectQueryAll(Statement statement, String tableName,Integer...fieldSize){
		if(isNotConnectNull()){
			ResultSet resultSet=null;
			try {
				resultSet=statement.executeQuery(parseSQL("select * from "+tableName,fieldSize));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return resultSet;
		}else{
			return null;
		}
	}
	public ResultSet selectQuery(Statement statement,String sql,Integer...fieldSize){
		if(isNotConnectNull()){
			ResultSet resultSet=null;
			try {
				resultSet=statement.executeQuery(parseSQL(sql,fieldSize));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return resultSet;
		}else{
			return null;
		}
	}
	
	public void closeQuery(Statement st,ResultSet rs){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(st!=null){
			try {
				st.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	public int getDescNameSize(String tableName){
		Statement statement=getStatement();
		ResultSet resultSet=selectQueryList("desc "+tableName);
		int size=0;
		
		try {
			while(resultSet.next()){
				size++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeQuery(statement, resultSet);
		}

		return size;
	}
	
	
	public <T> List<T> getListByName(Class<T> entity,String...tableName){
		return updateDBC.getListByName(entity, tableName);
	}
	public <T> List<T> getListByNameLimit(Class<T> entity,int index,int size,String...tableName){
		return updateDBC.getListByNameLimit(entity, index, size, tableName);
	}
	public <T> T getSingeByName(Integer id,T entity,String...tableName) throws Exception{
		return updateDBC.getSingeByName(id, entity, tableName);
	}
	

	public void setObjectByName(Object entity,String...tableName) throws Exception{
		updateDBC.setObjectByName(entity, tableName);
	}
	
	//操作缓存数据
	public boolean CacheOpen(String tabel_view,String ColumIndexName,String... columNames) {
		cacheDBC=CacheDBC.getCacheDBC(dbCenter,this);
		if (cacheDBC.CacheOpen(tabel_view,ColumIndexName,columNames)) {
			dbCenter.getTable_GetName().put(tabel_view.trim(), columNames);			
			dbCenter.getTable_GetIndexName().put(tabel_view.trim(), ColumIndexName.trim());
			return true;
		}else {
			return false;
		}
		
	}
	public void closeCache_C(){
		cacheDBC.close();
		cacheDBC=null;
	}
	public boolean ProvingUO_C(String value,String[] values,String... table) {
		if (isCacheOpen()) {
			USER_PW user_PW=cacheDBC.getMapValue(value,table);
			if (user_PW==null) {
				return false;
			}
			if (values==null||values.length==0) {
				return true;
			}
			
			long object1=0;
			for (int i = 0; i < user_PW.getColums().length; i++) {
				object1+=(user_PW.getColum(i)+"").hashCode();
			}
			long object2=0;
			for (String string : values) {
				object2+=(string+"").hashCode();
			}
			if (object2==object1) {
				return true;
			}else {
				return false;
			}
		}
		return false;
			
	}
	public boolean ProvingUO_C(String value,String[] columName,String[] values,String...table) {
		if (isCacheOpen()) {
			USER_PW user_PW=cacheDBC.getMapValue(value,table);
			if (user_PW==null) {
				return false;
			}
			if (columName==null||values==null||columName.length==0||values.length==0) {
				return true;
			}
			
			String object1="";
			boolean flag=true;
			Map<String, String> valueServer=new HashMap<>();
			for (int i=0;i<columName.length;i++) {
				int index=-1;
				for (int j = 0; j < dbCenter.getTable_GetName().get(user_PW.getTable()).length; j++) {
					if (columName[i].equalsIgnoreCase(dbCenter.getTable_GetName().get(user_PW.getTable())[j])) {
						index=j;break;
					}
				}
				if (index!=-1) {
					object1+=user_PW.getColum(index);
				}else {
					if (flag) {
						ResultSet resultSet=selectQueryList("select * from "+user_PW.getTable()+" where id="+user_PW.getId());
						try {
							if (resultSet.next()) {
								for (int j = 1; j < resultSet.getMetaData().getColumnCount()+1; j++) {
									valueServer.put(resultSet.getMetaData().getColumnName(j), resultSet.getObject(j).toString());
								}
							}
							object1+=valueServer.get(columName[i]);
						} catch (SQLException e) {
							e.printStackTrace();
						}finally {
							CloseList();
						}
						flag=false;
					}else {
						object1+=valueServer.get(columName[i]);
					}			
				}
				
			}
		
			valueServer.clear();
			valueServer=null;			
			
			String object2="";
			for (String string : values) {
				object2+=string;
			}
			
			if (object2.equals(object1)) {
				return true;
			}else {
				return false;
			}
		}
		return false;		
	}
	
	public int getCacheId_C(String value,String...table) {
		if (isCacheOpen()) {
			return cacheDBC.getMapValue(value,table).getId();
		}
		return 0;
	}
	public Object[] getColum_C(String value,String[] columNames,String...table) {
		if (isCacheOpen()) {
			if(columNames==null)return null;
			Object[] objects=new Object[columNames.length];
			for (int i = 0; i < objects.length; i++) {
				objects[i]=getColum_C(value, columNames[i],table);
			}
			return objects;
		}
		return null;
	}
	public Object getColum_C(String value,String columName,String...table) {
		if (isCacheOpen()) {
			if (columName==null) {
				return null;
			}
			
			USER_PW user_PW=cacheDBC.getMapValue(value,table);
			if (user_PW==null) {
				return null;
			}		
			if (columName.equalsIgnoreCase(dbCenter.getTable_GetIndexName().get(user_PW.getTable()))) {
				return user_PW.getUsername();
			}			
			
			int index=-1;
			for (int i=0;i<dbCenter.getTable_GetName().get(user_PW.getTable()).length;i++) {	
				if (columName.equalsIgnoreCase(dbCenter.getTable_GetName().get(user_PW.getTable())[i])) {
					index=i;break;
				}
			}
			if (index!=-1){
				return user_PW.getColum(index);
			}else {
				ResultSet resultSet=selectQueryList("select T."+columName+" from "+user_PW.getTable()+" T where id="+user_PW.getId());
				try {
					if (resultSet.next()) {
						return resultSet.getObject(columName);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally {
					CloseList();
				}
				
			}
		}		
		return null;
	}

	private int flushCache(String sql) {
		String value=sql.trim().split(" ")[0];
		if ("update".equalsIgnoreCase(value)) {
			return updateCache(sql.trim(), sql);
		}else if ("delete".equalsIgnoreCase(value)) {
			return deleteCache(sql.trim().split("(?i)from")[1].trim(),sql);
		}else if ("insert".equalsIgnoreCase(value)) {
			//System.out.println(sql);
			return insertCache(sql.trim().split("(?i)into")[1].trim(), sql);
		}else {
			return 0;
		}
		
	}

	private int updateCache(String sql,String sourceSQL) {
		Statement statement=getStatement();
		String table=sql.substring(sql.indexOf(" "),sql.indexOf("set")).trim();
		if (dbCenter.getTable_GetIndexName().get(table)==null) {		
			try {
				return statement.executeUpdate(sourceSQL);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					CONNECTION.commit();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CloseStatement(statement);
			}
		}
		int upsize = 0;
		
		if (sql.toLowerCase().indexOf("where")!=-1) {					
			
			sql="select * from "+table+" where "+sql.split("(?i)where")[1].trim();
			ResultSet resultSet=selectQueryList(sql);			
			try {
				if (list.size()>0) {
					list.clear();
				}
				while (resultSet.next()) {				
					USER_PW user_PW=cacheDBC.getMapValue(resultSet.getObject(dbCenter.getTable_GetIndexName().get(table)).toString(),table);
					user_PW.setId(resultSet.getInt("id"));
					list.add(user_PW);		
					cacheDBC.remove(user_PW.getUsername(),table);
				}				
				upsize=statement.executeUpdate(sourceSQL);
				CONNECTION.commit();
				if (upsize>0) {
					for (Object object : list) {
						sql="select * from "+table+" where id="+((USER_PW)object).getId();
						resultSet=selectQueryList(sql);
						if (resultSet.next()) {
							((USER_PW)object).setUsername(resultSet.getObject(dbCenter.getTable_GetIndexName().get(table)).toString());
							
							Object[] colums=new Object[dbCenter.getTable_GetName().get(table).length];
							for (int i = 0; i < colums.length; i++) {
								colums[i]=resultSet.getObject(dbCenter.getTable_GetName().get(table)[i]);
							}
										
							((USER_PW)object).setColums(colums);						
						}
						cacheDBC.addMapValue(((USER_PW)object).getUsername(), (USER_PW)object,table);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				if (list.size()>0) {
					list.clear();
				}
				CloseList();
				CloseStatement(statement);
			}		
			
		}else {
			cacheDBC.removeTable(this, table);
			try {
				upsize=statement.executeUpdate(sourceSQL);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					CONNECTION.commit();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CloseStatement(statement);
			}
			if (upsize>0) {
				cacheDBC.CacheOpen(table, dbCenter.getTable_GetIndexName().get(table), dbCenter.getTable_GetName().get(table));
			}
		}
		return upsize;
	}
	@SuppressWarnings("finally")
	private int deleteCache(String sql,String sourceSQL) {
		Statement statement=getStatement();
		String table=sql.substring(0,sql.indexOf(" ")).trim();
		if (dbCenter.getTable_GetIndexName().get(table)==null) {
			try {
				return statement.executeUpdate(sourceSQL);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					CONNECTION.commit();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CloseStatement(statement);
			}
		}
		
		sql="select * from "+sql;
		ResultSet resultSet=selectQueryList(sql);
		
		int upsize = 0;
		
		try {
			if (list.size()>0) {
				list.clear();
			}
			while (resultSet.next()) {
				list.add(resultSet.getObject(dbCenter.getTable_GetIndexName().get(table)));		
			}
			upsize=statement.executeUpdate(sourceSQL);
			CONNECTION.commit();
			if (upsize>0) {
				for (Object object : list) {
					cacheDBC.remove(object.toString(),table);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {			
				CloseList();
				CloseStatement(statement);
			}finally {
				return upsize;
			}
		}
			
	}
	private int insertCache(String sql,String sourceSQL) {
		Statement statement=getStatement();
		String table=sql.substring(0,sql.indexOf("(")).trim();
		if (dbCenter.getTable_GetIndexName().get(table)==null) {			
			try {
				return statement.executeUpdate(sourceSQL);
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				try {
					CONNECTION.commit();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CloseStatement(statement);
			}
		}
		int upsize=0;
		if (sql.split("(?i)values")[0].indexOf("(")!=-1) {
			String value=null;
			String[] names=sql.substring(sql.indexOf("(")+1,sql.indexOf(")")).split(",");
			String[] valuse=sql.split("(?i)values")[1].trim().substring(1,sql.split("(?i)values")[1].trim().length()-1).split(",");
			for (int i=0;i<names.length;i++) {
				if (dbCenter.getTable_GetIndexName().get(table).equalsIgnoreCase(names[i].trim())) {
					value=valuse[i].trim();break;
				}			
			}
			try {
				upsize=statement.executeUpdate(sourceSQL);
				CONNECTION.commit();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (upsize>0) {
				sql="select * from "+table+" where "+dbCenter.getTable_GetIndexName().get(table)+"="+value;
				ResultSet resultSet=selectQueryList(sql);
				try {
					if (resultSet.next()) {
						int id=resultSet.getInt("id");
						Object[] colums=new Object[dbCenter.getTable_GetName().get(table).length];
						for (int i = 0; i < colums.length; i++) {
							colums[i]=resultSet.getObject(dbCenter.getTable_GetName().get(table)[i]);
						}
						
						cacheDBC.addMapValue(parseFormat(value), new USER_PW(parseFormat(value), id, table, colums),table);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					CloseList();
					CloseStatement(statement);
				}		
			}		
		}else {
			int id=Integer.parseInt(sql.substring(sql.indexOf("(")+1,sql.indexOf(")")).split(",")[0].trim());
			
			try {
				upsize=statement.executeUpdate(sourceSQL);
				CONNECTION.commit();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (upsize>0) {
				sql="select * from "+table+" where id="+id;
				ResultSet resultSet=selectQueryList(sql);
				try {
					if (resultSet.next()) {
						String value=resultSet.getObject(dbCenter.getTable_GetIndexName().get(table)).toString();
						Object[] colums=new Object[dbCenter.getTable_GetName().get(table).length];
						for (int i = 0; i < colums.length; i++) {
							colums[i]=resultSet.getObject(dbCenter.getTable_GetName().get(table)[i]);
						}
						
						cacheDBC.addMapValue(value, new USER_PW(value, id, table, colums),table);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					CloseList();
					CloseStatement(statement);
				}
			}		
		}	
		return upsize;
	}
	public void reStartCache_C(DBCTools dbcTools) {
		cacheDBC.reStartCache(dbcTools);
	}	
	public DBCenter getDBCenter(){
		return this.dbCenter;
	}
	
	private boolean isCacheOpen() {
		if (cacheDBC==null) {
			System.err.println("\n*********缓存未打开，请CacheOpen之后操作！**********\n");
			return false;
		}else {
			return true;
		}
	}
		
	
	public boolean commit(List<String> sqlCommits,Integer...fieldSize) {
		if (isNotConnectNull()) {
			Statement statement=getStatement();
			try {				
				for (String string : sqlCommits) {
					statement.executeUpdate(parseSQL(string,fieldSize));
				}
				CONNECTION.commit();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				try {
					CONNECTION.rollback();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			} finally {
				CloseStatement(statement);
				sqlCacheClear();
			}
			return true;
		}else {
			return false;
		}
		
	}
	public void CloseList() {
		if(Mstatements.get(Thread.currentThread())==null)return;
		for (Statement statement : Mstatements.get(Thread.currentThread())) {
			if (statement!=null) {
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Mstatements.remove(Thread.currentThread());
		
	}
	public boolean close(boolean flag) {
		if (STATEMENT!=null) {
			try {
				STATEMENT.close();
				STATEMENT=null;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		if (CONNECTION!=null) {
			try {
				CONNECTION.close();
				CONNECTION=null;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}		
		return true;
	}
	
	public boolean close(Object entity) throws Exception{
		int isclear = reflexParse.setNullAll(DBCTools.class, entity.getClass(), entity, 0);
		if(isclear==0)return false;
		
		if(!close(true))return false;
		DBCInstance.DBCINSTANCE.geDbcTools().remove(getIDTools(getOnlyId()));
		DBCInstance.DBCINSTANCE.getDbcPw().remove(getOnlyId());	
		tDbcTools=null;
		
		return true;
	}
	
	
	
	protected String getOnlyId() {
		return onlyId;
	}

	public boolean createConnect(String host,String database,String port,String user,String password) {		
		if (CONNECTION==null) {			
			if (forClass()) {
				try {
					SRC="jdbc:mysql://"+host+":"+port+"/"+database+"?serverTimezone=UTC&connectTimeout=10000&socketTimeout=10000";
					CONNECTION=DriverManager.getConnection(SRC,user,password);
					this.host=host;this.database=database;this.port=port;this.user=user;this.password=password;
					STATEMENT=CONNECTION.createStatement();
					CONNECTION.setAutoCommit(false);
				} catch (Exception e) {
					return false;
				} 
				return true;
			}else {
				System.err.println("\n*********JDBC驱动没找到!!!  请保证有数据库依赖包!*********\n");
				return false;
			}
		}else {
			System.err.println("\n*********已存在Connection!!!  请先Close连接!*********\n");
			return false;
		}
	}
	public static synchronized DBCTools getDbcTools(String onlyID,String host,String port,String database,String username,String password) {
			for (int i=0;i<DBCInstance.DBCINSTANCE.getDbcPw().size();i++) {
				if (onlyID.equals(DBCInstance.DBCINSTANCE.getDbcPw().get(i))) {
					return DBCInstance.DBCINSTANCE.geDbcTools().get(i);
				}
			}
			if (DBCInstance.DBCINSTANCE.geDbcTools().size()>poolsize) {
				DBCInstance.DBCINSTANCE.geDbcTools().get(poolsize).close(false);				
				DBCInstance.DBCINSTANCE.getDbcPw().set(poolsize, onlyID);
				DBCInstance.DBCINSTANCE.geDbcTools().get(poolsize).createConnect(host, database,port, username, password);
				return DBCInstance.DBCINSTANCE.geDbcTools().get(poolsize);
			}else {
				tDbcTools=new DBCTools(onlyID);
				if (tDbcTools.createConnect(host, database,port,username,password)) {
					DBCInstance.DBCINSTANCE.geDbcTools().add(tDbcTools);
					DBCInstance.DBCINSTANCE.getDbcPw().add(onlyID);		
				}else {
					tDbcTools.close(false);
					tDbcTools=null;
				}					
				return tDbcTools;
			}
			
	}
	public static synchronized DBCTools getDbcTools(String onlyID,String fileName) {
		for (int i=0;i<DBCInstance.DBCINSTANCE.getDbcPw().size();i++) {
			if (onlyID.equals(DBCInstance.DBCINSTANCE.getDbcPw().get(i))) {
				return DBCInstance.DBCINSTANCE.geDbcTools().get(i);
			}
		}
		
		File file=new File(fileParent+File.separator+fileName);
		if (!file.exists()) {
			System.err.println("\n*********文件不存在！**********\n");
			return null;
		}
		String[] DBCF={"<DBC-host>","<DBC-port>","<DBC-database>","<DBC-username>","<DBC-password>"};
		String[] DBCL={"</DBC-host>","</DBC-port>","</DBC-database>","</DBC-username>","</DBC-password>"};
		
		String[] values=readDate(DBCF, DBCL, file);
		if(values==null){return null;}
		
		return getDbcTools(onlyID, values[0], values[1], values[2], values[3],values[4]);	
	}
	
	public static String[] readDate(String[] Frist,String[] Last,File file){
		String[] values=new String[Frist.length];
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String info=null;
			StringBuffer buffer=new StringBuffer();
			String value=null;boolean flag=false;
			int Findex,Lindex;
			
			while ((info=reader.readLine())!=null) {
				for (int i = 0; i < Frist.length; i++) {
					if ((Findex=info.indexOf(Frist[i]))!=-1) {	
						flag=true;
						if ((Lindex=info.indexOf(Last[i]))!=-1) {
							flag=false;
							value=info.substring(Findex, Lindex);
							values[i]=value.substring(value.indexOf(">")+1, value.length()).trim();
							info=info.substring(info.indexOf(">",info.indexOf(">")+1)+1);
						}else {
							value=info.substring(info.indexOf(">")+1).trim();
							buffer.append(value);
							break;
						}					
					}else if ((Lindex=info.indexOf(Last[i]))!=-1) {
						flag=false;
						value=info.substring(0,Lindex).trim();
						buffer.append(value);
						values[i]=buffer.toString();
						buffer.setLength(0);
						info=info.substring(info.indexOf(">")+1);					
					}else if (i==Frist.length-1){
						if (flag) {
							buffer.append(info.trim());
						}
						break;
					}			
				}
			}
			return values;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return null;	
	}
	
	private boolean isNotConnectNull() {
		if (CONNECTION!=null) {
			return true;
		}else {
			if(reconnection()){
				return true;
			}		
			System.err.println("\n**********Connection为空！  地址密码账号可能不对！***********\n");
			return false;
		}
	}
	public Connection getConnection(){
		return CONNECTION;
	}
	public static DBCTools getIDTools(String onlyID) {
		for (int i=0;i<DBCInstance.DBCINSTANCE.getDbcPw().size();i++) {
			if (onlyID.equals(DBCInstance.DBCINSTANCE.getDbcPw().get(i))) {
				return DBCInstance.DBCINSTANCE.geDbcTools().get(i);
			}
		}
		return null;
	}
	private boolean forClass() {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
}
