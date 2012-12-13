package main.java.globals;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DbManager{
	private static ORColl coll = null;
	public static class ORColl {
		private final DBCollection orlog;
		private  ORColl(DBCollection orlog){
			this.orlog = orlog;
		}
	}
	public static final DBCollection get(){
		if(coll == null){
			try {
				//mongodb://<dbuser>:<dbpassword>@ds045557.mongolab.com:45557/orlog
				MongoClient mongoClient = new MongoClient( "ds045557.mongolab.com" , 45557);
				DB db = mongoClient.getDB("orlog");
				boolean auth = db.authenticate("bleezmo", "abc123".toCharArray());
				if(!auth) throw new RuntimeException("unauthorized for db");
				coll = new ORColl(db.getCollection("orlog"));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return coll.orlog;
		}else{
			return coll.orlog;
		}
	}
}

