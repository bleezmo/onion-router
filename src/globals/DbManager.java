package globals;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

public class DbManager {
	public DbManager(){
		try {
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}
}
