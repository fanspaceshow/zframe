package org.zframework.web.controller.admin.system;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/uploader")
public class UploaderController{
	/**
	 * 转向上传页面
	 * @param model
	 * @param uploadType
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		return "admin/system/uploader/file";
  }
	
	@RequestMapping(value="/upload/{uploadType}",method={RequestMethod.GET,RequestMethod.POST})
	public String uploadFile(Model model,@PathVariable String uploadType){
		return "admin/system/uploader/"+uploadType;		
	}
	/**
	 * 执行上传文件
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/doUploadFile",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject doUploadFile(HttpServletRequest request,@RequestParam("Filedata")MultipartFile uploadFiles,String uploadType){
		JSONObject jResult = new JSONObject();
		if(!uploadFiles.isEmpty()){
			String uploadPath = request.getSession().getServletContext().getRealPath("/resources/upload/"+uploadType);
			File dir = new File(uploadPath);
			if(!dir.exists())
				dir.mkdirs();
			String ext = uploadFiles.getOriginalFilename().substring(uploadFiles.getOriginalFilename().indexOf("."));
			String newFileName = new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
			newFileName += Math.round(10+(Math.random()*9990));
			newFileName += ext;
			File f = new File(uploadPath+"/"+newFileName);
			try {
				FileCopyUtils.copy(uploadFiles.getBytes(), f);
				jResult.element("result", "success");
				jResult.element("path", newFileName);
			} catch (IOException e) {
				e.printStackTrace();
				jResult.element("result", "error");
			}
		}else{
			jResult.element("result", "error");
		}
		return jResult;
	}
	@RequestMapping(value="/doUploadImage",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject doUploadImage(){
		JSONObject jResult = new JSONObject();
		return jResult;
	}
}
