package org.zframework.core.util;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

/**
 * 压缩文件工具类
 * @author zengchao
 *
 */
public class ZipUtil {
	/**
	 * 压缩文件
	 * @param sourceFile 源文件路径
	 * @param targetFile 目标文件路径
	 * @return
	 */
	public static boolean CompressFile(String sourceFile,String targetFile){
		try {
			File sFile = new File(sourceFile);
			if(sFile.isDirectory()){
				Project prj = new Project();
				Zip zip = new Zip();
				zip.setProject(prj);
				zip.setDestFile(new File(targetFile));
				FileSet fileSet = new FileSet();
				fileSet.setProject(prj);
				fileSet.setDir(sFile);
				zip.addFileset(fileSet);
				zip.execute();
				return true;
			}else{
				Project prj = new Project();
				Zip zip = new Zip();
				zip.setProject(prj);
				zip.setDestFile(new File(targetFile));
				FileSet fileSet = new FileSet();
				fileSet.setProject(prj);
				fileSet.setFile(sFile);
				zip.addFileset(fileSet);
				
				zip.execute();
				return true;
			}
			
		} catch (Exception e) {
			return false;
		}
	}
}
