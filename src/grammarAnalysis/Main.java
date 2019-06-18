package grammarAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.omg.PortableServer.ServantActivator;

import com.sun.corba.se.spi.ior.IORTypeCheckRegistry;
import com.sun.nio.sctp.Association;
import com.sun.org.apache.xml.internal.security.encryption.Reference;
import com.sun.xml.internal.ws.wsdl.writer.document.PortType;

import sun.reflect.generics.tree.Tree;

public class Main {
	
	private static BufferedReader reader = null;
	private static BufferedWriter writer = null;
	private static BufferedWriter errorWriter = null;
	private static List<String> tokens;
	private static String currentToken;
	private static int lineNumber = 0;
	private static int level = 0;
	private static TreeNode root;
	
	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		reader = new BufferedReader(new FileReader(new File("tokenOut1.txt")));
		writer = new BufferedWriter(new FileWriter(new File("SyntaxOut1.txt")));
		errorWriter = new BufferedWriter(new FileWriter(new File("ErrorInfo1.txt")));
		tokens = new ArrayList<String>();
		currentToken = getToken();
		currentToken = getToken();
		while(!currentToken.equals("EOF")){
			root = ThreadSpec();
			recur(root);
			writer.newLine();
		}
		writer.close();
		errorWriter.close();
	}

	public static void recur(TreeNode treeNode) throws IOException{
		if (treeNode != null){
			
			for(int i = 0; i < level; i++){
				if(i < level-1)
					writer.write("	");
				else 
					writer.write("|---");
			}
			writer.write(treeNode.getNodeKind());
			writer.newLine();
			for(int i = 0; i < treeNode.getChild().size(); i++){
				level++;
				recur(treeNode.getChild().get(i));
				level--;
			}
		}
	}
	
	public static String getToken() throws IOException{
		if(!tokens.isEmpty()){
			String str = tokens.get(0);
			tokens.remove(0);
			return str;
		}else{
			String line = reader.readLine();
			lineNumber ++;
			String [] arr = line.split("\\s+");
			tokens = new ArrayList<String>();
			for(int i = 0; i < arr.length; i++){
				tokens.add(arr[i]);
			}
			String str = tokens.get(0);
			tokens.remove(0);
			return str;
		}
	}
	
	public static void match(String token) throws IOException{
		if(currentToken.equals(token))
			currentToken = getToken();
		else{
			errorWriter.write("error in line " + lineNumber + ", error token is \"" + currentToken + "\"");
			errorWriter.newLine();
			recur(root);
			writer.close();
			errorWriter.close();
			System.exit(0);
			
		}
	}
	
	public static TreeNode ThreadSpec() throws IOException{
		TreeNode threadSpec = new TreeNode("threadspec");
		match("THREAD");
		match("IDENTIFIER");
		if(currentToken.equals("FEATURES")){
			match("FEATURES");
			threadSpec.setChild(featureSpec());
		}
		if(currentToken.equals("FLOWS")){
			match("FLOWS");
			threadSpec.setChild(flowSpec());
		}
		if(currentToken.equals("PROPERTIES")){
			match("PROPERTIES");
			threadSpec.setChild(association());
			match(";");
		}
		match("END");
		match("IDENTIFIER");
		match(";");
		return threadSpec;
	}
	
	public static TreeNode featureSpec() throws IOException{
		TreeNode featureSpec = new TreeNode("featurespec");
		if(currentToken.equals("IDENTIFIER")){
			match("IDENTIFIER");
			match(":");
			if(currentToken.equals("IN")){
				match("IN");
				if(currentToken.equals("OUT"))
					match("OUT");
			}else if(currentToken.equals("OUT")){
				match("OUT");
			}else {
				System.out.println("error");
			}
			if(currentToken.equals("DATA") || currentToken.equals("EVENT")){
				TreeNode portSpec = new TreeNode("portSpec"); 
				TreeNode IOtype = new TreeNode("IOtype");
				featureSpec.setChild(portSpec);
				portSpec.setChild(IOtype);
				portSpec.setChild(portType());
				if(currentToken.equals("{")){
					match("{");
					TreeNode association = association();
					portSpec.setChild(association);
					while(currentToken.equals("{")){
						association.setSibling(association());
					}
					match("}");
				}
				match(";");
			}else if(currentToken.equals("PARAMETER")){
				TreeNode parameterSpec = new TreeNode("parameterSpec");
				TreeNode IOtype = new TreeNode("IOtype");
				featureSpec.setChild(parameterSpec);
				parameterSpec.setChild(IOtype);
				match("PARAMETER");
				if(currentToken.equals("IDENTIFIER"))
					parameterSpec.setChild(reference());
				if(currentToken.equals("{")){
					match("{");
					TreeNode association = association();
					parameterSpec.setChild(association);
					while(currentToken.equals("IDENTIFIER")){
						association.setSibling(association());
					}
					match("}");
				}
				match(";");
			}	
		}else if(currentToken.equals("NONE")){
			match("NONE");
			match(";");
		}
		return featureSpec;
	}
	
	public static TreeNode portSpec() throws IOException{
		TreeNode portSpec = new TreeNode("portSpec");
		match("IDENTIFIER");
		match(":");
		portSpec.setChild(IOtype());
		portSpec.setChild(portType());
		if(currentToken.equals("{")){
			match("{");
			TreeNode association = association();
			portSpec.setChild(association);
			while(currentToken.equals("IDENTIFIER")){
				association.setSibling(association());
			}
			match("}");
		}
		match(";");
		return portSpec;
	}
	
	public static TreeNode portType() throws IOException{
		TreeNode portType = new TreeNode("portType");
		if(currentToken.equals("DATA")){
			match("DATA");
			match("PORT");
			if(currentToken.equals("IDENTIFIER"))
				portType.setChild(reference());
		}else if (currentToken.equals("EVENT")) {
			match("EVENT");
			if (currentToken.equals("DATA")) {
				match("DATA");
				match("PORT");
				if(currentToken.equals("IDENTIFIER"))
					portType.setChild(reference());
			}else{
				match("PORT");
			}
		}
		return portType;
	}
	
	
	public static TreeNode parameterSpec() throws IOException{
		TreeNode parameterSpec = new TreeNode("parameterSpec");
		match("IDENTIFIER");
		match(":");
		parameterSpec.setChild(IOtype());
		match("PARAMETER");
		if(currentToken.equals("IDENTIFIER"))
			parameterSpec.setChild(reference());
		if(currentToken.equals("{")){
			TreeNode association = new TreeNode("association");
			match("{");
			parameterSpec.setChild(association);
			while(currentToken.equals("IDENTIFIER"))
				association.setSibling(association());
			match("}");
		}
		match(";");
		return parameterSpec;
	}
	
	
	public static TreeNode IOtype() throws IOException{
		TreeNode IOtype = new TreeNode("IOtype");
		if(currentToken.equals("IN")){
			match("IN");
			if(currentToken.equals("OUT"))
				match("OUT");
		}else if(currentToken.equals("OUT"))
			match("OUT");
		return IOtype;	
	}
	
	public static TreeNode flowSpec() throws IOException {
		TreeNode flowSpec = new TreeNode("flowSpec");
		TreeNode flowSourceSpec = new TreeNode("flowSourceSpec");
		TreeNode flowSinkSpec = new TreeNode("flowSinkSpec");
		TreeNode flowPathSpec = new TreeNode("flowPathSpec");
		if(currentToken.equals("IDENTIFIER")){
			match("IDENTIFIER");
			match(":");
			match("FLOW");
			if(currentToken.equals("SOURCE")){
				flowSpec.setChild(flowSourceSpec);
				match("SOURCE");
				match("IDENTIFIER");
				if(currentToken.equals("{")){
					match("{");
					TreeNode association = new TreeNode("association");
					flowSourceSpec.setChild(association);
					while(currentToken.equals("IDENTIFIER")){
						association.setSibling(association());
					}
					match("}");
				}
			}else if(currentToken.equals("SINK")){
				flowSpec.setChild(flowSinkSpec);
				match("SINK");
				match("IDENTIFIER");
				if(currentToken.equals("{")){
					match("{");
					TreeNode association = new TreeNode("association");
					flowSinkSpec.setChild(association);
					while(currentToken.equals("IDENTIFIER")){
						association.setSibling(association());
					}
					match("}");
				}
			}else if(currentToken.equals("PATH")){
				flowSpec.setChild(flowPathSpec);
				match("PATH");
				match("IDENTIFIER");
				match("->");
				match("IDENTIFIER");
			}
			match(";");
		}else if(currentToken.equals("NONE")){
			match("NONE");
			match(";");
		}
		return flowSpec;
	}
	
	public static TreeNode association() throws IOException {
		TreeNode association = new TreeNode("association");
		if(currentToken.equals("IDENTIFIER")){
			match("IDENTIFIER");
			if(currentToken.equals("::")){
				match("::");
				match("IDENTIFIER");
			}
			association.setChild(splitter());
			if(currentToken.equals("CONSTANT"))
				match("CONSTANT");
			match("ACCESS");
			match("DECIMAL");
		}else if(currentToken.equals("NONE")){
			match("NONE");
		}
		return association;
	}
	
	public static TreeNode splitter() throws IOException {
		TreeNode splitter = new TreeNode("splitter");
		if(currentToken.equals("=>"))
			match("=>");
		if(currentToken.equals("+=>"))
			match("+=>");
		return splitter;
	}
	
	public static TreeNode reference() throws IOException{
		TreeNode reference = new TreeNode("reference");
		boolean flag = true;
		while(flag){
			match("IDENTIFIER");
			if(currentToken.equals("::"))
				match("::");
			else
				flag = false;
		}
		return reference;
		
	}
	
}
