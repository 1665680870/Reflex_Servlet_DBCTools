package DBConnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBCenter {
	private Map<String, String[]> table_GetName=null;
	private Map<String, String> table_GetIndexName=null;
	private List<String> tables=null;
	private Map<Thread, Integer> tableId=null;
	
	public Map<Thread, Integer> getTableId() {
		return tableId;
	}
	public void setTableId(Map<Thread, Integer>  tableId) {
		this.tableId = tableId;
	}
	public DBCenter() {
		table_GetName=new HashMap<>();
		table_GetIndexName=new HashMap<>();
		tables=new ArrayList<>();
		tableId=new HashMap<>();
	}
	public Map<String, String[]> getTable_GetName() {
		return table_GetName;
	}

	public Map<String, String> getTable_GetIndexName() {
		return table_GetIndexName;
	}

	public List<String> getTables() {
		return tables;
	}
	protected void close(){
		table_GetName.clear();
		table_GetIndexName.clear();
		tables.clear();
		table_GetIndexName=null;
		table_GetName=null;
		tables=null;
	}

}
