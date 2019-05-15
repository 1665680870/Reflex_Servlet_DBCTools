package DBConnect;

import java.util.ArrayList;
import java.util.List;

public class DBCInstance {
	private List<DBCTools> dbcTools;
	private List<String> dbcPw;
	protected static DBCInstance DBCINSTANCE=new DBCInstance();
	private DBCInstance() {
		dbcTools=new ArrayList<>();
		dbcPw=new ArrayList<>();
	}
	protected List<DBCTools> geDbcTools(){return dbcTools;}
	protected List<String> getDbcPw(){return dbcPw;}
}
