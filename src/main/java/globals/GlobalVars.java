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
	public static final VarTemplate template5(){
		return new VarTemplate(8089,8089,"user");
	}
	public static final VarTemplate template5(String nodeName){
		return new VarTemplate(8089,8089,nodeName);
	}
	
	public static final String directoryHost = "ec2-107-22-59-195.compute-1.amazonaws.com";
	public static final int directoryPort = 8080;
	
	public static final String returnIpHost = "localhost";
	public static final int returnIpPort = 9000;
	
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
		public VarTemplate(int serverPort, int terminalPort, String nodeName){
			this.serverPort = serverPort;
			this.terminalPort = terminalPort;
			if(nodeName.getBytes().length < 16){
				this.nodeName = nodeName;
			}else throw new RuntimeException("cannot have a node name greater then 16 bytes");
		}
	}
}
