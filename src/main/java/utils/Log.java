package main.java.utils;

import main.java.globals.DbManager;
import main.java.globals.GlobalVars;

import java.util.Date;

import com.mongodb.BasicDBObject;

public class Log {
	public static final void i(String out){
		System.out.println("INFO: "+out);
	}
	public static final void w(String out){
		System.out.println("WARN: "+out);
	}
	public static final void e(String out){
		System.out.println("ERROR: "+out);
	}
	public static final void e(StackTraceElement[] elements){
		for(int i = 0; i < elements.length; i++){
			Log.e(elements[i].toString());
		}
	}
	public static final void f(String out){
		System.out.println("FATAL: "+out);
	}
	//db logs
	public static final void db(String message){
		db(new ORLog(GlobalVars.getNodeName(),message));
	}
	private static final void db(ORLog orlog){
		BasicDBObject doc = new BasicDBObject();
		doc.append("timestamp", orlog.getTimestamp());
		doc.append("ornode", orlog.getOrnode());
		doc.append("message", orlog.getMessage());
		DbManager.get().insert(doc);
	}
	public static class ORLog{
		private final long timestamp = new Date().getTime();
		private final String ornode;
		private final String message;
		public long getTimestamp() {
			return timestamp;
		}
		public String getOrnode() {
			return ornode;
		}
		public String getMessage() {
			return message;
		}
		public ORLog(String ornode, String message){
			this.ornode = ornode;
			this.message = message;
		}
	}
}
