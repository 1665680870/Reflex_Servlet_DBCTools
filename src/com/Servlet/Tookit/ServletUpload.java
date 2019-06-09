package com.Servlet.Tookit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.Servlet.Tookit.RQRPF.FileType;


public class ServletUpload{
	private List<FileItem> files=new ArrayList<>();
	private List<Object> values=new ArrayList<>();
	private List<Object> names=new ArrayList<>();
	private String oldName;private boolean flag=true;
	private long process=0;
	private long fileSize=0;
	private int curFile=0;
	private int countFile=0;
	private long[] fileSizes;
	private Map<Integer, FileType> mapFile;
	private HttpServletRequest request;
	private Map<Thread, RQRPF> map;
	private FileUpload upload=null;
	public ServletUpload() {}

	public boolean pdMultipart(Map<Thread, RQRPF> map,ServletTookit servletTookit) throws Exception {
		if (ServletFileUpload.isMultipartContent(map.get(Thread.currentThread()).getRequest())) {
			this.request=map.get(Thread.currentThread()).getRequest();this.map=map;
			
			run(map.get(Thread.currentThread()).getRequest(),map.get(Thread.currentThread()).getFileMaxSize());			
			return true;
		}else {
			servletTookit.setServletMethod(map);
			return false;
		}
	}
	@SuppressWarnings("deprecation")
	private void run(HttpServletRequest request,long fileMaxSize) {	
		DiskFileItemFactory factory=new DiskFileItemFactory();
		upload=new FileUpload(factory);
		if(fileMaxSize!=-1){
			upload.setFileSizeMax(fileMaxSize);
		}
		request.setAttribute("typeErro", false);
		request.setAttribute("sizeErro", false);
		mapFile=map.get(Thread.currentThread()).getFileMap();
		
		upload.setProgressListener(new ProgressListener() {
			
			@Override
			public void update(long arg0, long arg1, int arg2) {
				// TODO Auto-generated method stub
				process=arg0;fileSize=arg1;curFile=arg2;
			}
		});
		
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> items=upload.parseRequest(request);
			countFile=items.size();
			if (items!=null&&!(items.isEmpty())) {
				int i=1;
				for (FileItem fileItem : items) {
					if (fileItem.isFormField()) {						
						likeName(fileItem.getFieldName(),fileItem.getString("utf-8"));
					}else {
						if(mapFile.get(null)!=null||mapFile.get(0)!=null){
							if(pdFileType(fileItem,null)){
								files.add(fileItem);
							}else{
								request.setAttribute("typeErro", true);
								return;
							}
						}else{
							if(mapFile.get(i)!=null){
								if(pdFileType(fileItem,i)){
									files.add(fileItem);
								}else{
									request.setAttribute("typeErro", true);
									return;
								}
							}else{
								files.add(fileItem);
							}
							i++;
						}
					}
					
				}		
				names.add(oldName);
			}
		} catch (FileUploadException e) {
			request.setAttribute("sizeErro", true);
			e.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
	}
	private void likeName(String name,String value) {		
		String[] V={value};
		if (flag) {			
			flag=false;
		}else {			
			if (oldName.equals(name)) {
				String[] I=(String[])values.get(values.size()-1);
				String[] J=new String[I.length+1];
				for (int i = 0; i < I.length; i++) {
					J[i]=I[i];
				}
				J[J.length-1]=value;
				V=J;
			}else {
				names.add(oldName);
			}			
		}
		oldName=name;
		values.add(V);
	}
	private boolean pdFileType(FileItem item,Integer curFileIndex){
		String[] fileType= map.get(Thread.currentThread()).getFileMap().get(curFileIndex).fileType;
		if(fileType==null||fileType.length==0){
			return true;
		}
		if(map.get(Thread.currentThread()).getFileMap().get(curFileIndex).contain){
			for (String type : fileType) {
				if(type.equalsIgnoreCase(item.getName().substring(item.getName().lastIndexOf(".")+1, item.getName().length()))){
					return true;
				}
			}
			return false;
		}else{
			for (String type : fileType) {
				if(type.equalsIgnoreCase(item.getName().substring(item.getName().lastIndexOf(".")+1, item.getName().length()))){
					return false;
				}
			}
			return true;
		}
	}
	
	
	public List<Object> getValues() {
		return this.values;
	}
	public List<Object> getNames() {
		return this.names;
	}
	public String[] startServletWrite(String...filePaths) {
		String filePath=null;
		if (filePaths==null) {
			filePath="file";
		}
		int filesize=filePaths.length;
		String[] fileName=new String[files.size()];
		fileSizes=new long[files.size()];
		int i=0;
		for (FileItem fileItem : files) {
			if(filesize<=i){
				filePath="file";
			}else{
				filePath=filePaths[i];
				if("".equals(filePath)){
					filePath="file";
				}
			}
				File file=new File(request.getServletContext().getRealPath(filePath));
				if (!file.exists()) {
					file.mkdirs();
				}
				
				InputStream inputStream=null;
				FileOutputStream outputStream=null;
				try {
					fileSizes[i]=fileItem.getSize();
					fileName[i]=(new Date().getTime()+"_")+fileItem.getName();
					inputStream=fileItem.getInputStream();
					outputStream=new FileOutputStream(file+File.separator+fileName[i]);
					byte[] bs=new byte[1024*1024];
					int length;
					
					while ((length=inputStream.read(bs))!=-1) {
						outputStream.write(bs, 0, length);
					}				
				} catch (IOException e) {
					System.err.println("\n**********文件写入IO异常！***********\n");
					return null;
				} finally {
					try {
						fileName[i]=filePath+"/"+fileName[i];
						inputStream.close();
						outputStream.close();
						i++;
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			
		}	
		return fileName;
		
	}
	public long getServletCurProcess() {
		return this.process;
	}
	public long getServletCurFileSize() {
		return this.fileSize;
	}
	public int getServletCurFile() {
		return this.curFile;
	}
	public int getServletCurCountFile(){
		return this.countFile;
	}
	public long getServletFileSize(int index){
		try{
			return fileSizes[(index-1)];
		}catch(Exception e){
			return 0;
		}
	}
}
