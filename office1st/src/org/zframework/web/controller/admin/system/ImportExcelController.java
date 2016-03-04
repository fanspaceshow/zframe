package org.zframework.web.controller.admin.system;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.controller.BaseController;
import org.zframework.web.service.admin.system.ImportExcelService;

@Controller
@RequestMapping("/admin/importExcel")
public class ImportExcelController extends BaseController<Object>{	
	Log log  = LogFactory.getLog("ImportExcelController");
	@Autowired
	private ImportExcelService importExcelService;
	
	public String index(){
		return "admin/system/updown/importExcel";
  }
	@RequestMapping(value="/import",method={RequestMethod.GET,RequestMethod.POST})
	public String importExcel(){
		return "admin/system/updown/importExcel";
  }
	 /**
     * Excel导入
     * --------------------------------------------------------
     */    
    @RequestMapping(value="/importdo",method={RequestMethod.GET,RequestMethod.POST})
    public String excelImport(HttpServletRequest request,HttpServletResponse response,@RequestParam("excelFile") MultipartFile excelFile ){
    	log.error("into excelImport");
/**        MultipartHttpServletRequest multipartHttpservletRequest=(MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpservletRequest.getFile("excelFile");*/
        String originalFileName=excelFile.getOriginalFilename();    	
        String savePath = request.getSession().getServletContext().getRealPath("/WEB-INF/temp");
        String filePath = savePath+"/"+originalFileName;
        String flag = "1";
        try {
        	//创建目录
            File file=new File(savePath);
            if(!file.exists()){
                file.mkdir();
            }
    /**        FileOutputStream fileOutputStream=new FileOutputStream(savePath+"/"+originalFileName);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();*/
            
            //*************************
            File file2 = new File(filePath);
            BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(file2));
            FileCopyUtils.copy(excelFile.getInputStream(), stream);
			stream.close();
            
			flag =  importExcelService.importExcel(filePath);
        	
        	file2.delete();
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
        return flag;
    } 
}
