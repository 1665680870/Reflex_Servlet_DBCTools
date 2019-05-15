package DBConnect;

public class USER_PW {
	private String username;
	private String tableName;
	private int id;
	private Object[] colums;
	public USER_PW(String username,int id,String tableName,Object... objects) {
		this.username=username;this.id=id;this.colums=objects;this.tableName=tableName;
	}
	public void setUsername(String username) {
		this.username=username;
	}
	public void setTable(String table) {
		this.tableName=table;
	}
	public void setId(int id) {
		this.id=id;
	}
	public void setColums(Object[] colums) {
		this.colums=colums;
	}
	public String getUsername() {
		return username;
	}
	public int getId() {
		return id;
	}
	public String getTable(){
		return tableName;
	}
	public Object[] getColums() {
		return colums;
	}
	public Object getColum(int index) {
		try {
			return colums[index];
		} catch (Exception e) {
			System.err.println("\n*******数组越界！请输入存在的列，下标以1开始！********\n");
			return null;
		}
		
	}

}
