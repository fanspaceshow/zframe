package org.zframework.web.service.admin.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.util.ClassUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.ReflectUtil;
import org.zframework.core.util.ZipUtil;
import org.zframework.orm.dao.BaseHibernateDao;
import org.zframework.orm.query.PageBean;

/**
 * 导出Excel 
 * 读取数据库数据
 * @author ZENGCHAO
 *
 */
@Service
public class ExportExcelService {
	@Autowired
	private BaseHibernateDao baseDao;
	/**
	 * 导出Excel
	 * 返回生成的excel名称
	 * @param request
	 * @param list
	 * @param columns
	 * @param titles
	 * @return
	 * @throws IOException
	 * 
	 */
	private String exportExcel(HttpServletRequest request,List<?> list,String[] columns,String[] titles,String fileName,boolean ifCompress) throws IOException{
		FileOutputStream fos = null;
		try{
			//产生工作簿对象
			HSSFWorkbook workbook = new HSSFWorkbook();
			//产生工作表对象
			HSSFSheet sheet = workbook.createSheet();
			
			//创建表头
			HSSFRow headRow = sheet.createRow(0);
			//设置表格样式
			CellStyle cellStyle = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			cellStyle.setFont(font);
			for(int i=0;i<titles.length;i++){
				HSSFCell cell = headRow.createCell(i);
				cell.setCellStyle(cellStyle);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(titles[i]);
				sheet.setColumnWidth(i, 20*256);
			}
			for(int i=1;i<list.size()+1;i++){
				Object obj = list.get(i-1);
				HSSFRow row = sheet.createRow(i);
				for(int j=0;j<columns.length;j++){
					Object value = ReflectUtil.getFieldValueNoCaseSensitive(obj, columns[j]);
					HSSFCell cell = row.createCell(j);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					if(ObjectUtil.isNotNull(value)){
						//状态字段特殊处理
						if(columns[j].toLowerCase().equals("enabled")){
							if(Integer.parseInt(value.toString()) == 0)
								cell.setCellValue("启用");
							else
								cell.setCellValue("禁用");
						}else{
							cell.setCellValue(value.toString());
						}
					}
					else
						cell.setCellValue("");
				}
			}
			String savePath = request.getSession().getServletContext().getRealPath("/resources/excels");
			//判断存放excel的文件夹是否存在，不存在创建新的文件夹
			File dir = new File(savePath);
			if(!dir.exists())
				dir.mkdirs();
			if(ObjectUtil.isNotNull(fileName) && ObjectUtil.isNotEmpty(fileName))
				fileName = fileName + ".xls";
			else
				fileName = new SimpleDateFormat("yyyyMMddHHmmsssss").format(new Date())+".xls";
			File fExcel = new File(savePath+"/"+fileName);
			fos = new FileOutputStream(fExcel);
			workbook.write(fos);
			fos.flush();
			fos.close();
			if(ifCompress){//判断是否压缩
				String sourceFile = savePath+"/"+fileName;
				fileName = fileName.replace(".xls", ".zip");
				ZipUtil.CompressFile(sourceFile, savePath+"/"+fileName);
				fExcel.delete();//删除原excel文件
			}
			return fileName;
		}finally{
			if(ObjectUtil.isNotNull(fos)){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public JSONObject executeExportExcelAll(HttpServletRequest request,String entityClass,String[] columns,String[] titles,String fileName,boolean ifCompress){
		JSONObject jResult = new JSONObject();
		try {
			Set<Class<?>> classes = ClassUtil.getClassesByAnnotation("org.zframework.web.entity", Entity.class);
			Class<?> clazz = null;
			Iterator<Class<?>> iter = classes.iterator();
			while(iter.hasNext()){
				Class<?> cls = iter.next();
				if(cls.getName().endsWith("."+entityClass)){
					clazz = cls;
					break;
				}
			}
			if(ObjectUtil.isNull(clazz)){
				throw new ClassNotFoundException();
			}
			List<?> list = new ArrayList<Object>();
			//屏蔽关键性数据
			if(entityClass.toLowerCase().equals("user"))
				list = baseDao.list(clazz,Restrictions.not(Restrictions.eq("loginName", "superadmin")));
			else
				list = baseDao.list(clazz);
			fileName = exportExcel(request, list, columns, titles,fileName,ifCompress);
			jResult.element("isExported", true);
			jResult.element("fileName", fileName);
			jResult.element("ifCompress", ifCompress);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "实体"+entityClass+"不存在!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "IO错误!");
		} catch (IOException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "IO错误!");
		}
		return jResult;
	}
	public JSONObject executeExportExcelPage(HttpServletRequest request,String entityClass,String[] columns,String[] titles,String fileName,boolean ifCompress,int pageNo,int pageSize){
		JSONObject jResult = new JSONObject();
		try {
			Set<Class<?>> classes = ClassUtil.getClassesByAnnotation("org.zframework.web.entity", Entity.class);
			Class<?> clazz = null;
			Iterator<Class<?>> iter = classes.iterator();
			while(iter.hasNext()){
				Class<?> cls = iter.next();
				if(cls.getName().endsWith("."+entityClass)){
					clazz = cls;
					break;
				}
			}
			if(ObjectUtil.isNull(clazz)){
				throw new ClassNotFoundException();
			}
			List<?> list = new ArrayList<Object>();
			PageBean pageBean = new PageBean();
			pageBean.setPage(pageNo);
			pageBean.setRows(pageSize);
			//屏蔽关键性数据
			if(entityClass.toLowerCase().equals("user")){
				pageBean.addCriterion(Restrictions.not(Restrictions.eq("loginName", "superadmin")));
				list = baseDao.list(clazz,pageBean);
			}else{
				list = baseDao.list(clazz, pageBean);
			}
			fileName = exportExcel(request, list, columns, titles,fileName, ifCompress);
			jResult.element("isExported", true);
			jResult.element("fileName", fileName);
			jResult.element("ifCompress", ifCompress);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "实体"+entityClass+"不存在!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "IO错误!");
		} catch (IOException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "IO错误!");
		}
		return jResult;
	}
	public JSONObject executeExportExcelSelected(HttpServletRequest request,String entityClass,String[] columns,String titles[],String fileName,boolean ifCompress,Integer[] ids){
		JSONObject jResult = new JSONObject();
		try {
			Set<Class<?>> classes = ClassUtil.getClassesByAnnotation("org.zframework.web.entity", Entity.class);
			Class<?> clazz = null;
			Iterator<Class<?>> iter = classes.iterator();
			while(iter.hasNext()){
				Class<?> cls = iter.next();
				if(cls.getName().endsWith("."+entityClass)){
					clazz = cls;
					break;
				}
			}
			if(ObjectUtil.isNull(clazz)){
				throw new ClassNotFoundException();
			}
			List<?> list = new ArrayList<Object>();
			//屏蔽关键性数据
			if(entityClass.toLowerCase().equals("user"))
				list = baseDao.list(clazz, Restrictions.not(Restrictions.eq("loginName", "superadmin")) , Restrictions.in("id", ids));
			else
				list = baseDao.list(clazz,Restrictions.in("id", ids));
			fileName = exportExcel(request, list, columns, titles,fileName ,ifCompress);
			jResult.element("isExported", true);
			jResult.element("fileName", fileName);
			jResult.element("ifCompress", ifCompress);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "实体"+entityClass+"不存在!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "IO错误!");
		} catch (IOException e) {
			e.printStackTrace();
			jResult.element("isExported", false);
			jResult.element("error", "IO错误!");
		}
		return jResult;
	}
}
