package org.zframework.web.controller.admin.system;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.support.http.ContentType;
import org.zframework.web.controller.BaseController;
import org.zframework.web.service.admin.system.ExportExcelService;

@Controller
@RequestMapping("/admin/exportExcel")
public class ExportExcelController extends BaseController<Object>{
	@Autowired
	private ExportExcelService exportExcelService;
	/**
	 * 导出Excel首页
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		return "admin/system/exportExcel/index";
	}
	/**
	 * 导出全部
	 * @param 实体类
	 * @param 导出的字段集合
	 * @return
	 */
	@RequestMapping(value="/doExportExcelAll",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject doExportExcelAll(HttpServletRequest request,String entityClass,String[] columns,String[] titles,String fileName,boolean ifCompress){
		JSONObject jResult = new JSONObject();
		jResult = exportExcelService.executeExportExcelAll(request,entityClass, columns,titles,fileName,ifCompress);
		return jResult;
	}
	/**
	 * 导出指定页面
	 * @param 实体类名称
	 * @param 导出的字段集合
	 * @param 页码
	 * @param 分页大小
	 * @return
	 */
	@RequestMapping(value="/doExportExcelPage",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject doExportExcelPage(HttpServletRequest request,String entityClass,String[] columns,String[] titles,String fileName,boolean ifCompress,int pageNo,int pageSize){
		JSONObject jResult = new JSONObject();
		jResult = exportExcelService.executeExportExcelPage(request, entityClass, columns, titles,fileName, ifCompress,pageNo, pageSize);
		return jResult;
	}
	/**
	 * 导出选中数据
	 * @param 实体类
	 * @param 导出的列集合
	 * @param 选中行数据的ID值集合
	 * @return
	 */
	@RequestMapping(value="/doExportExcelSelected",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject doExportExcelSelected(HttpServletRequest request,String entityClass,String[] columns,String titles[],String fileName,boolean ifCompress,Integer[] ids){
		JSONObject jResult = new JSONObject();
		jResult = exportExcelService.executeExportExcelSelected(request, entityClass, columns, titles,fileName,ifCompress, ids);
		return jResult;
	}
	@RequestMapping("/download")
	public void download(HttpServletRequest request,HttpServletResponse response,String fileName,boolean ifCompress){
		String contentType = ContentType.EXCEL;
		//生成提示信息
		if(ifCompress){
			contentType = ContentType.ZIP;
		}
		response.setContentType(contentType);
		try {
			String savePath = request.getSession().getServletContext().getRealPath("/resources/excels");
			//判断存放excel的文件夹是否存在，不存在创建新的文件夹
			File dir = new File(savePath);
			if(!dir.exists())
				dir.mkdirs();
			File fExcel = new File(savePath+"/"+fileName);
			//文件下载
			fileDownLoad(request, response, contentType, fExcel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
