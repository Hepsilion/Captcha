package org.captcha.image.svm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.captcha.image.util.Constants;
import org.captcha.image.util.ImageUtil;

public class SVMFormater {
	/**
	 * 转换成svm训练的格式
	 */
	public static void transformData2SvmFormatForTrain(HashMap<String, Integer> labelMap, HashMap<String, ArrayList<Integer[][]>> imageMap){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(Constants.SVM_TRAN_FILE));
			Iterator<String> iterator=imageMap.keySet().iterator();
			while(iterator.hasNext()){
				String className=iterator.next();
				int classLabel=labelMap.get(className);
				ArrayList<Integer[][]> imgList=imageMap.get(className);
				System.out.println(className);
				for(Integer[][] img:imgList){
					String tmpLine=classLabel+" ";
					int index=1;
					for(int i=0; i<img.length; i++){
						for(int j=0; j<img[i].length; j++){
							tmpLine+=index+":"+img[i][j]+" ";
							index++;
						}
					}
					writer.write(tmpLine+"\r\n");
					writer.flush();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(writer!=null)
				writer.close();
		}
	}
	
	/**
	 * 转成svm测试的格式
	 */
	public static void transformData2SvmFormatForTest(HashMap<String, Integer> labelMap, HashMap<String, Integer[][]> imageMap){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(Constants.SVM_TEST_FILE));
			Iterator<String> iterator=imageMap.keySet().iterator();
			
			while(iterator.hasNext()) {
				String fileName=(String) iterator.next();
				String className=ImageUtil.getImgClass(fileName);
				int classLabel=getClassLabel(labelMap, className);
				
				String tmpLine=classLabel + " ";
				Integer[][] imageArr=imageMap.get(fileName);
				
				int index=1;
				for(int i=0; i<imageArr.length; i++){
					for(int j=0; j<imageArr[i].length; j++){
						tmpLine+=index+":"+imageArr[i][j]+" ";
						index++;
					}
				}
				writer.write(tmpLine+"\r\n");
				writer.flush();
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}
	}
	
	/**
	 * 获得类标号
	 * @param className
	 * @return
	 */
	private static int getClassLabel(HashMap<String, Integer> labelMap, String className){
		if(labelMap.containsKey(className)){
			return labelMap.get(className);
		}else{
			return -1;
		}
	}
	
	/**
	 * 转换为svm预测的格式
	 * @param imageList
	 */
	public static void transformData2SvmFormatForPredict(ArrayList<BufferedImage> images){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File("svm/svm.test"));
			for (BufferedImage image : images) {
				int width = image.getWidth();
				int height = image.getHeight();
				int index = 1;
				String tmpLine = "-1 ";//默认无标号，则为-1
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int value = ImageUtil.isBlack(image.getRGB(x, y)) ? 1 : 0;//黑色点标记为1
						tmpLine += index + ":" + value + " ";
						index ++;
					}
				}
				writer.write(tmpLine + "\r\n");
				writer.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (writer != null) {
				writer.close();
			}
		}
	}
}
