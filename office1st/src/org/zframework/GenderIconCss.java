package org.zframework;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class GenderIconCss {
	public static void main1(String[] args) {
		//String path = "E:/MyEclipse/Workspaces/MyEclipse 8.5/zFrame/webapps/resources/framework/images/icons";
		String path = "C:/Users/vaio/Desktop/1";
		final String iconName = "application";
		final String targetName = "application";
		File file = new File(path);
		File[] files = file.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				if(name.startsWith(iconName))
					return true;
				return false;
			}
		});
		for(File f : files){
			String name = f.getName().replace(".png", "");
			name = name.replace(".gif", "");
			name = name.replace(iconName, targetName);
			String icon = ".icon-" + name.replace("_", "-")+"{\n";
			StringBuffer sb = new StringBuffer(icon);
			sb.append("\tbackground:url('../../images/icons/"+name+".png') no-repeat;\n");
			sb.append("}");
			if(targetName.length()>0 && !iconName.equals(targetName))
				f.renameTo(new File(path+"/"+name+".png"));
			System.out.println(sb.toString());
		}
	}
	public static void main2(String[] args) {
		String path = "C:/Users/vaio/Desktop/1";
		File file = new File(path);
		File[] files = file.listFiles();
		for(File f : files){
			String name = f.getName();
			String icon = ".icon-" + name.substring(0,name.lastIndexOf(".")).replace("_", "-")+"{\n";
			StringBuffer sb = new StringBuffer(icon);
			sb.append("\tbackground:url('../../images/icons/"+name+"') no-repeat;\n");
			sb.append("}");
			System.out.println(sb.toString());
		}
	}
	
	public static void main(String[] args) {
		StringBuilder sbd = new StringBuilder("12345");
		System.out.println(sbd.reverse().toString());
	}
	/*public static void main(String[] args) {
		File file = new File("E:/MyEclipse/Workspaces/MyEclipse 8.5/zFrame/webapps/resources/framework/css/easyui/bootstrap/easyui.css");
		try {
			List<String> urls = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(file));
			while(br.ready()){
				String line = br.readLine();
				if(line.indexOf("images/")>0){
					String url = line.substring(line.indexOf("images/"),line.indexOf(".")+4);
					urls.add(url);
				}
			}
			br.close();
			int bytesum = 0;
			int byteread = 0;
			for(String url : urls){
				URL netUrl = new URL("http://jeasyui.com/easyui/themes/bootstrap/"+url);
				URLConnection urlCon = netUrl.openConnection();
				InputStream is = urlCon.getInputStream();
				
				FileOutputStream fos = new FileOutputStream("E:/MyEclipse/Workspaces/MyEclipse 8.5/zFrame/webapps/resources/framework/css/easyui/bootstrap/"+url);
				byte[] buffer = new byte[1204];
				while((byteread = is.read(buffer)) != -1) {
					 bytesum += byteread;
					 fos.write(buffer, 0, byteread);
				}
				System.out.println("文件"+url+"下载成功!");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
enum Scope{
	PRIVATE,PUBLIC,PROTECTED
}
class Clazz{
	private Scope scope = Scope.PUBLIC;
	private String name;
	private Class<?> superClass = null;
	private Class<?> entityClass = null;
	private List<Method> methods = new ArrayList<Method>();
	private List<Var> vars = new ArrayList<Var>();
	private List<String> annotations = new ArrayList<String>();
	
	public Clazz(String name){
		this.setName(name);
	}
	public String getScope() {
		return scope.toString().toLowerCase();
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class<?> getSuperClass() {
		return superClass;
	}
	public void setSuperClass(Class<?> superClass) {
		this.superClass = superClass;
	}
	public Class<?> getEntityClass() {
		return entityClass;
	}
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
	public void AddMethod(Method method){
		this.methods.add(method);
	}
	public void addVar(Var var){
		this.vars.add(var);
	}
	public void addAnnotation(String annotation){
		this.annotations.add(annotation);
	}
	
	public String toString() {
		StringBuffer sbVar = new StringBuffer();
		for(String annotation : this.annotations){
			sbVar.append(annotation+"\n");
		}
		sbVar.append(this.getScope()+" class "+this.name);
		if(this.getSuperClass()!=null){
			if(getEntityClass() != null)
				sbVar.append(" extends "+this.getSuperClass().getSimpleName()+"<"+this.getEntityClass().getSimpleName()+">");
			else
				sbVar.append(" extends "+this.getSuperClass().getSimpleName());
		}
		sbVar.append("{\n");
		for(Var var : vars){
			sbVar.append(var.toString(1)+"\n\n");
		}
		for(Method method : methods){
			sbVar.append(method.toString(1)+"\n\n");
		}
		sbVar.append("}");
		return sbVar.toString();
	}
}
class Method{
	private Scope scope = Scope.PUBLIC;
	private String returnType = "void";
	private String name;
	private List<MethodParam> params = new ArrayList<MethodParam>();
	private List<String> annotations = new ArrayList<String>();
	private List<String> lines = new ArrayList<String>();
	
	public Method(Scope socpe,String returnType,String name){
		this.setScope(socpe);
		this.setReturnType(returnType);
		this.setName(name);
	}
	
	public String getScope() {
		return scope.toString().toLowerCase();
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void addAnnotation(String annotation){
		this.annotations.add(annotation);
	}
	public void addParam(Class<?> type,String name){
		this.params.add(new MethodParam(type,name));
	}
	public void addLine(String line){
		if(!line.endsWith(";"))
			line = line + ";";
		this.lines.add(line);
	}
	public String toString(int indent){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<indent;i++){
			sb.append("\t");
		}
		String strIndent = sb.toString();
		StringBuffer sbVar = new StringBuffer();
		for(String annotation : this.annotations){
			sbVar.append(strIndent+annotation+"\n");
		}
		sbVar.append(strIndent+this.getScope() + " "+this.getReturnType()+" "+this.getName()+"(");
		for(MethodParam param : this.params){
			sbVar.append(param+",");
		}
		if(this.params.size()>=1){
			sbVar = new StringBuffer(sbVar.substring(0,sbVar.length()-1));
		}
		sbVar.append("){\n");
		for(String line : this.lines){
			sbVar.append(strIndent+strIndent+line+"\n");
		}
		sbVar.append(strIndent+"}");
		return sbVar.toString();
	}
	
}
class MethodParam{
	private Class<?> type = null;
	private String name;
	
	public MethodParam(Class<?> type,String name){
		this.setType(type);
		this.setName(name);
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.getType().getSimpleName() + " "+this.getName();
	}
	
}
class Var{
	private Scope scope = Scope.PRIVATE;
	private Class<?> type = null;
	private String name = "";
	private List<String> annotations = new ArrayList<String>();
	
	public Var(Scope scope,Class<?> type,String name){
		this.setScope(scope);
		this.setType(type);
		this.setName(name);
	}
	public String getScope() {
		return scope.toString().toLowerCase();
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addAnnotation(String annotation){
		this.annotations.add(annotation);
	}
	
	public String toString(int indent) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<indent;i++){
			sb.append("\t");
		}
		String strIndent = sb.toString();
		StringBuffer sbVar = new StringBuffer();
		for(String annotation : this.annotations){
			sbVar.append(strIndent+annotation);
		}
		sbVar.append(strIndent+this.getScope() + " "+this.getType().getSimpleName()+" "+this.getName()+";");
		return sbVar.toString();
	}
}
