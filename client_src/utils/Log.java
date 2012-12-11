package utils;

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
	public static final void f(String out){
		System.out.println("FATAL: "+out);
	}
}
