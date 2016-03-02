package com.example.scancodecollectmoney.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.scancodecollectmoney.Content;

/**
 * com.example.scancodecollectmoney.utils
 * @Email zhaoq_hero@163.com
 * @author zhaoQiang : 2016-3-1
 */
public class SocketToNet {

	/**
	 * 使用  socket访问服务器   
	 * @param serverIp  服务器ip
	 * @param requestPort 请求端口
	 * @param params 参数信息
	 * @return
	 */
	public static String socketTcpRequset(String serverIp, int requestPort, JSONObject params) {
		
		byte[] data = JSON.toJSONString(params).getBytes();
		
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		
		try {
			socket = new Socket(serverIp, requestPort);
			
			/**
			 * 发送数据
			 */
			in = socket.getInputStream();
			out = socket.getOutputStream();
			out.write(data);
			
			int totalByteRecvd = 0;
			int bytesRcvd;
			
			byte[] result = new byte[512];
			while(true){
				int read = in.read(result,totalByteRecvd,result.length-totalByteRecvd);
				if((bytesRcvd = read) ==-1){
					break;
				}
				totalByteRecvd += bytesRcvd;
			}
			
			String resultStr = new String(result,Content.CHAR_SET).trim();
			
			return resultStr;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(in!=null)
					in.close();
				if(out!=null)
					out.close();
				if(socket!=null)
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
