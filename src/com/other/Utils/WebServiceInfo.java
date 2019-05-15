package com.other.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class WebServiceInfo {
	private static WebServiceInfo serviceInfo;
	public synchronized static WebServiceInfo getServiceInfo(){
		if(serviceInfo==null)
			serviceInfo=new WebServiceInfo();
		return serviceInfo;
	}
	
	
	private InputStream getInputStream(String url,LinkedHashMap<String, String> map,StringBuffer buffer) throws IOException{
		URL urlnet=new URL(url); 
		HttpURLConnection connection=(HttpURLConnection) urlnet.openConnection();	
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		
		if(map!=null){
			Iterator<String> iterator2 = map.values().iterator();
			Iterator<String> iterator = map.keySet().iterator();
			while(iterator.hasNext()){
				buffer.append(iterator.next()+"="+iterator2.next()+"&");
			}
			buffer.delete(buffer.length()-1, buffer.length());
		}
		
		
		DataOutputStream outputStream=new DataOutputStream(connection.getOutputStream());
		outputStream.writeUTF(buffer.toString());
		
		
		
		return connection.getInputStream();
		
	}
	
	public String getInfo(String url,LinkedHashMap<String, String> map) throws IOException{
		StringBuffer buffer=new StringBuffer();
		Scanner scanner=new Scanner(getInputStream(url, map,buffer));
		buffer.setLength(0);
		
		while(scanner.hasNext()){
			buffer.append(scanner.nextLine()+"\n");
		}
		scanner.close();
		
		String info=buffer.toString();
		buffer.setLength(0);
		buffer=null;
		
		return info;
	}
	
	public InputStream getByte(String url,LinkedHashMap<String, String> map) throws IOException{
		StringBuffer buffer=new StringBuffer();
		
		InputStream inputStream=getInputStream(url, map, buffer);
		buffer.setLength(0);
		buffer=null;
		
		return inputStream;
	}
}
