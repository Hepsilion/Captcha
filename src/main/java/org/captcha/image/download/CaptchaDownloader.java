package org.captcha.image.download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 从网上下载验证码图片
 * @author Hepsilion
 *
 */
public class CaptchaDownloader implements Runnable{
	private String url;
	private String savePath;
	
	public CaptchaDownloader(String url, String savePath){
		this.url=url;
		this.savePath=savePath;
	}
	
	public void run() {
		CloseableHttpClient client=HttpClients.createDefault();
		HttpGet get=new HttpGet(this.url);
		CloseableHttpResponse response=null;
		OutputStream os=null;
		try {
			response=client.execute(get);
			HttpEntity entity=response.getEntity();
			if(entity!=null){
				File file=new File(this.savePath+"/"+System.currentTimeMillis()+".jpg");
				os=new BufferedOutputStream(new FileOutputStream(file));
				entity.writeTo(os);
				os.flush();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(response!=null){
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args){
		String url="https://passport.bilibili.com/captcha";
		String savePath="images/0_download";
		Thread downloader=null;
		for (int i = 0; i < 100; i++) {
			downloader=new Thread(new CaptchaDownloader(url, savePath));
			downloader.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
