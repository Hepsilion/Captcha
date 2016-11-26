package org.captcha.image.svm;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.captcha.image.util.ImageUtil;

public class SVMDataPreparer {
	/**
	 * 加载类标签(训练)
	 */
	public static void loadImageLabel(HashMap<String, Integer> labelMap){
		produceImageLabel();
		BufferedReader reader=null;
		try {
			 reader=new BufferedReader(new FileReader(new File("svm/label.txt")));
			 String buff=null;
			 while((buff=reader.readLine())!=null){
				 String[] arr=buff.split(" ");
				 labelMap.put(arr[0], Integer.parseInt(arr[1]));
			 }
			 System.out.println("load image label finish!");
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			if(reader!=null) {
				try{
					reader.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void loadImageForTrain(HashMap<String, ArrayList<Integer[][]>> imageMap){
		File dir=new File("images/4_scale");
		File[] files=dir.listFiles(new FilenameFilter(){
			public boolean isJpg(String file){ 
			    if(file.toLowerCase().endsWith(".jpg"))  
			    	return true;   
			    else   
			    	return false;   
			}
			@Override
			public boolean accept(File dir, String name){
				return isJpg(name);
			}
		});
		
		for(File file:files){
			try{
				transformToMap(file, imageMap);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		System.out.println("load images end");
	}
	
	/**
	 * 将image 转换到 map中
	 * @param file
	 * @throws IOException
	 */
	private static void transformToMap(File file, HashMap<String, ArrayList<Integer[][]>> imageMap) throws IOException{
		BufferedImage image = ImageIO.read(file);
		int width = image.getWidth();
		int height = image.getHeight();
		Integer[][] imgArr = new Integer[height][width];
		
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int value=ImageUtil.isBlack(image.getRGB(x, y))?1:0;//黑色点标记为1
				imgArr[y][x]=value;
			}
		}
		
		ArrayList<Integer[][]> imgList=null;
		String className=ImageUtil.getImgClass(file.getName());
		if(imageMap.containsKey(className)){
			imgList=imageMap.get(className);
			imgList.add(imgArr);
		}else{
			imgList=new ArrayList<Integer[][]>();
			imgList.add(imgArr);
			imageMap.put(className, imgList);
		}
	}
	
	/**
	 * 转换成svm训练的格式
	 */
	public static void transformData2SvmFormatForTrain(HashMap<String, Integer> labelMap, HashMap<String, ArrayList<Integer[][]>> imageMap){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File("svm/svm.train"));
			Iterator<String> iterator=imageMap.keySet().iterator();
			while(iterator.hasNext()){
				String className=iterator.next();
				int classLabel=labelMap.get(className);
				ArrayList<Integer[][]> list=imageMap.get(className);
				System.out.println(className);
				for(Integer[][] img:list){
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
	 * 转成svm 测试集的格式
	 */
	private static void transformData2SvmFormatForTest(HashMap<String, Integer> labelMap, HashMap<String, Integer[][]> imageMap){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File("svm/svm.test"));
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
	 * 加载类标签(预测)
	 */
	public static void loadLabelMapForPredict(HashMap<Integer, String> labelMap){
		BufferedReader reader=null;
		try {
			 reader=new BufferedReader(new FileReader(new File("svm/label.txt")));
			 String buff=null;
			 while((buff=reader.readLine())!=null){
				 String[] arr=buff.split(" ");
				 labelMap.put(Integer.parseInt(arr[1]), arr[0]);
			 }
			 System.out.println("load image label finish!");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try{
					reader.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 产生类标签
	 */
	private static void produceImageLabel(){
		FileWriter writer=null;
		try {
			writer = new FileWriter(new File("svm/label.txt"));
			String charactor="1234567890abcdefghijklmnopqrstuvwxyz";
			for (int i=0; i<charactor.length(); i++){
				char c=charactor.charAt(i);
				String str=c+" "+(i+1)+"\r\n";
				writer.write(str);
			}
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try{
					writer.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}
