package main.java.globals;


public class GlobalVars {
	public static final VarTemplate templateDirectory = new VarTemplate(8081,8082,"directory");
	public static final VarTemplate template1(){
		return new VarTemplate(8081,8082,"kroja");
	}
	public static final VarTemplate template1(String nodeName){
		return new VarTemplate(8081,8082,nodeName);
	}
	public static final VarTemplate template2(){
		return new VarTemplate(8083,8084,"bleezmo");
	}
	public static final VarTemplate template2(String nodeName){
		return new VarTemplate(8083,8084,nodeName);
	}
	public static final VarTemplate template3(){
		return new VarTemplate(8085,8086,"alexas");
	}
	public static final VarTemplate template3(String nodeName){
		return new VarTemplate(8085,8086,nodeName);
	}
	public static final VarTemplate template4(){
		return new VarTemplate(8087,8088,"grendor");
	}
	public static final VarTemplate template4(String nodeName){
		return new VarTemplate(8087,8088,nodeName);
	}
	
	public static final String directoryHost = "localhost";
	public static final int directoryPort = 8080;
	
	private static VarTemplate varTemplate;
	public static final int serverPort(){
		return getVarTemplate().serverPort;
	}
	public static final int terminalPort(){
		return getVarTemplate().terminalPort;
	}
	public static final String nodeName(){
		return getVarTemplate().nodeName;
	}
	
	public static void setVarTemplate(VarTemplate varTemplate){
		if(GlobalVars.varTemplate == null) GlobalVars.varTemplate = varTemplate;
	}
	private static final VarTemplate getVarTemplate(){
		if(varTemplate != null) return varTemplate;
		else {
			varTemplate = template1();
			return varTemplate;
		}
	}
	public static class VarTemplate{
		private final int serverPort;
		private final int terminalPort;
		private final String nodeName;
		private VarTemplate(int serverPort, int terminalPort, String nodeName){
			this.serverPort = serverPort;
			this.terminalPort = terminalPort;
			if(nodeName.getBytes().length < 16){
				this.nodeName = nodeName;
			}else throw new RuntimeException("cannot have a node name greater then 16 bytes");
		}
	}
}
