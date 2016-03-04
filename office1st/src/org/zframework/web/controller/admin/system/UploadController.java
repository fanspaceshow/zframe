package org.zframework.web.controller.admin.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;
import org.zframework.core.web.support.WebResult;

@Controller
@RequestMapping("/admin/upload")
public class UploadController {	
	
	Log log  = LogFactory.getLog("UploadController");
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		return "admin/system/updown/index";
  }
    @RequestMapping(value="/uploaddo",method={RequestMethod.GET,RequestMethod.POST})
    public void upload(HttpServletRequest request,HttpServletResponse response ){
    	log.error("into upload");
        MultipartHttpServletRequest multipartHttpservletRequest=(MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpservletRequest.getFile("uploadFile");
        String originalFileName=multipartFile.getOriginalFilename();//getOriginalFilename()   
        
        log.error("new File");
        
        int charAt= originalFileName.indexOf(".");        	
    	String fileExtName=originalFileName.substring(charAt,originalFileName.length());
    	String fileName = originalFileName.substring(0,charAt);
    	log.error(fileName);
    	log.error(fileExtName);
    	String saveFileName = makeFileName(fileName);
        String savePath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload");
        String realSavePath = makePath(saveFileName,savePath);
        //创建目录
        File file=new File(realSavePath);
        if(!file.exists()){
            file.mkdir();
        }
        //System.out.println(file.getAbsolutePath());
        try {         	
            FileOutputStream fileOutputStream=new FileOutputStream(file+"/"+saveFileName+fileExtName);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            
            log.error("success");
        } catch (FileNotFoundException e) {           
            e.printStackTrace();
           // return WebResult.error("未找到文件!");
        } catch (IOException e) {           
            e.printStackTrace();
          //  return WebResult.error("上传失败!");
        }
        log.error("return success");       
        //return WebResult.success();
        //return "redirect:admin/system/updown/index";
    }
 
    /**
     * 为防止一个目录下面出现太多文件，要使用hash算法打散存储
     * @Method: makePath
     * @Description: 
     * @Anthor:fantianming
     *
     * @param filename 文件名，要根据文件名生成存储目录
     * @param savePath 文件存储路径
     * @return 新的存储目录
     */
    private String makePath(String filename,String savePath){
        //得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
        int hashcode = filename.hashCode();
        int dir1 = hashcode&0xf; //0--15
        int dir2 = (hashcode&0xf0)>>4; //0-15
        //构造新的保存目录
        String dir = savePath + "\\" + dir1 + "\\" + dir2; //upload\2\3 upload\3\5
        //File既可以代表文件也可以代表目录     File file = new File(dir);
        File file = new File(dir);
        //如果目录不存在
        if(!file.exists()){
          //创建目录
          file.mkdirs();
       }
        return dir;
      }
    
    /**
     * @Method: makeFileName
     * @Description: 生成上传文件的文件名，文件名以：uuid+"_"+文件的原始名称
     * @Anthor:fantianming
     * @param filename 文件的原始名称
     * @return uuid+"_"+文件的原始名称
     */
     private String makeFileName(String filename){ //2.jpg
       //为防止文件覆盖的现象发生，要为上传文件产生一个唯一的文件名
       return UUID.randomUUID().toString() + "_" + filename;
     } 
     
     
     /**
      * 下载的文件list
      */
     @ResponseBody
     @RequestMapping(value="/downloadlist",method={RequestMethod.GET,RequestMethod.POST})
     public Map<String,String> downlistPage (HttpServletRequest request,HttpServletResponse response ){
    	//获取上传文件的目录
    	 String uploadFilePath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload");
    	 log.error("uploadFilePath"+uploadFilePath);
    	 //存储要下载的文件名
    	 Map<String,String> fileNameMap = new HashMap<String,String>();
    	 //递归遍历filepath目录下的所有文件和目录，将文件的文件名存储到map集合中
    	 listfile(new File(uploadFilePath),fileNameMap);//File既可以代表一个文件也可以代表一个目录
    	 //将Map集合发送到listfile.jsp页面进行显示
    	//request.setAttribute("fileNameMap", fileNameMap);
    	//request.getRequestDispatcher("/listfile.jsp").forward(request, response);
    	for (String key : fileNameMap.keySet()) {
    	String value = (String) fileNameMap.get(key);
    	System.out.println(value);
    	}
    	 return fileNameMap;
    	 }
    	  
    	 /**
    	 * @Method: listfile
    	 * @Description: 递归遍历指定目录下的所有文件
    	 * @Anthor:孤傲苍狼
    	 * @param file 即代表一个文件，也代表一个文件目录
    	 * @param map 存储文件名的Map集合
    	 */
    	 public void listfile(File file,Map<String,String> map){
    	 //如果file代表的不是一个文件，而是一个目录
    	 if(!file.isFile()){
    	 //列出该目录下的所有文件和目录
    	 File files[] = file.listFiles();
    	 //遍历files[]数组
    	 for(File f : files){
    	 //递归
    	 listfile(f,map);
    	 }
    	 }else{
    	 /**
    	 * 处理文件名，上传后的文件是以uuid_文件名的形式去重新命名的，去除文件名的uuid_部分
    	 file.getName().indexOf("_")检索字符串中第一次出现"_"字符的位置，如果文件名类似于：9349249849-88343-8344_阿_凡_达.avi
    	 那么file.getName().substring(file.getName().indexOf("_")+1)处理之后就可以得到阿_凡_达.avi部分
    	 */
    	 String realName = file.getName().substring(file.getName().indexOf("_")+1);
    	 //file.getName()得到的是文件的原始名称，这个名称是唯一的，因此可以作为key，realName是处理过后的名称，有可能会重复
    	 map.put(file.getName(), realName);
    	 }
     }
     
    	 
    	 
    	 @RequestMapping(value="/downloaddo",method={RequestMethod.GET,RequestMethod.POST})
    		public void download(HttpServletRequest request,HttpServletResponse response ){
    			try {
    				log.error("into download");
    				//得到要下载的文件名
    				String fileName = request.getParameter("filename"); //23239283-92489-阿凡达.avi
    				log.error("fileName : "+fileName);
    				fileName = new String(fileName.getBytes("iso8859-1"),"UTF-8");
    				log.error("fileName : "+fileName);
    				//上传的文件都是保存在/WEB-INF/upload目录下的子目录当中
    				String fileSaveRootPath=request.getSession().getServletContext().getRealPath("/WEB-INF/upload");
    				log.error("fileSaveRootPath : "+fileSaveRootPath);
    				//通过文件名找出文件的所在目录
    				String path = findFileSavePathByFileName(fileName,fileSaveRootPath);
    				log.error("path : "+path);
    				//得到要下载的文件
    				File file = new File(path);//+ "\\" + fileName
    				//如果文件不存在
    				if(!file.exists()){
    					log.error("into exists");
    				//request.setAttribute("message", "您要下载的资源已被删除！！");
    				//request.getRequestDispatcher("/message.jsp").forward(request, response);
    				return;
    				}
    				log.error("11111");
    				//处理文件名
    				String realname = fileName.substring(fileName.indexOf("_")+1);
    				//设置响应头，控制浏览器下载该文件
    				//response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(realname, "UTF-8"));
    				//解决下载时中文是URL编码的问题
    				response.setHeader("content-disposition", "attachment;filename=" + new String(realname.getBytes("gb2312"), "ISO8859-1"));
    				//读取要下载的文件，保存到文件输入流
    				FileInputStream in = new FileInputStream(path);// + "\\" + fileName
    				//创建输出流
    				OutputStream out = response.getOutputStream();
    				log.error("2222");
    				//创建缓冲区
    				byte buffer[] = new byte[1024];
    				int len = 0;
    				//循环将输入流中的内容读取到缓冲区当中
    				while((len=in.read(buffer))>0){
    				//输出缓冲区的内容到浏览器，实现文件下载
    				out.write(buffer, 0, len);
    				}
    				//关闭文件输入流
    				in.close();
    				//关闭输出流
    				out.close();
    				log.error("success download");
    			}catch (IOException o) {
    				o.printStackTrace();
    			}
    			catch (Exception e) {
    				e.printStackTrace();
    			}
    			}
    			 
    	 /** 
    	     * 递归查找文件 
    	     * @param baseDirName  查找的文件夹路径 
    	     * @param targetFileName  需要查找的文件名 
    	     * @param fileList  查找到的文件集合 
    	     */  
    	    public  String findFileSavePathByFileName(String fileName, String fileSaveRootPath) {  
    	    	boolean flag=true;
    	    	String path="";
    	    	//得到所有文件的文件名和路径
    	    	Map<String,String> fileNameMap = new HashMap<String,String>();
    	    	findfile(new File(fileSaveRootPath),fileNameMap);//File既可以代表一个文件也可以代表一个目录
    	    	//遍历所有map找到需要的文件和路径
    	    	for (String key : fileNameMap.keySet()) {
    	    		
    	    		 if (fileName.equals(key)) {
    	    			 flag=false;
    	    			 path = fileNameMap.get(key);
					}
				}
    	    	 if (flag) {
    	    		 path="error";
				}
				return path;
    	    }
    			
    	    
    	    /**
        	 * @Method: findfile
        	 * @Description: 递归遍历指定目录下的所有文件
        	 * @Anthor:fantianming
        	 * @param file 即代表一个文件，也代表一个文件目录
        	 * @param map 存储文件名的Map集合
        	 */
        	 public void findfile(File file,Map<String,String> map){
        		 System.out.println("into findfile");
        	 //如果file代表的不是一个文件，而是一个目录
        	 if(!file.isFile()){
        	 //列出该目录下的所有文件和目录
        	 File files[] = file.listFiles();
        	 //遍历files[]数组
        	 for(File f : files){
        	 //递归
        		 findfile(f,map);
        	 }
        	 }else{
        	 /**
        	 * 处理文件名，上传后的文件是以uuid_文件名的形式去重新命名的，去除文件名的uuid_部分
        	 file.getName().indexOf("_")检索字符串中第一次出现"_"字符的位置，如果文件名类似于：9349249849-88343-8344_阿_凡_达.avi
        	 那么file.getName().substring(file.getName().indexOf("_")+1)处理之后就可以得到阿_凡_达.avi部分
        	 */
        	 //String realName = file.getName().substring(file.getName().indexOf("_")+1);
        	 //file.getName()得到的是文件的原始名称，这个名称是唯一的，因此可以作为key，realName是处理过后的名称，有可能会重复
        	 map.put(file.getName(), file.getPath());
        	 System.out.println("filegetPath : "+file.getPath());
        	 }
         }
    	   
    	    
    	    
    	    /** 
    	     * 通配符匹配 
    	     * @param pattern    通配符模式 
    	     * @param str    待匹配的字符串 
    	     * @return    匹配成功则返回true，否则返回false 
    	     */  
    	    private  boolean wildcardMatch(String pattern, String str) {  
    	        int patternLength = pattern.length();  
    	        int strLength = str.length();  
    	        int strIndex = 0;  
    	        char ch;  
    	        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {  
    	            ch = pattern.charAt(patternIndex);  
    	            if (ch == '*') {  
    	                //通配符星号*表示可以匹配任意多个字符  
    	                while (strIndex < strLength) {  
    	                    if (wildcardMatch(pattern.substring(patternIndex + 1),  
    	                            str.substring(strIndex))) {  
    	                        return true;  
    	                    }  
    	                    strIndex++;  
    	                }  
    	            } else if (ch == '?') {  
    	                //通配符问号?表示匹配任意一个字符  
    	                strIndex++;  
    	                if (strIndex > strLength) {  
    	                    //表示str中已经没有字符匹配?了。  
    	                    return false;  
    	                }  
    	            } else {  
    	                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {  
    	                    return false;  
    	                }  
    	                strIndex++;  
    	            }  
    	        }  
    	        return (strIndex == strLength);  
    	    }    	        	    
}