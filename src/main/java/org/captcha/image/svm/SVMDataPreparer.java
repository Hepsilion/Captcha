package org.captcha.image.svm;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.captcha.image.process.ImageCFSSegmentor;
import org.captcha.image.process.ImagePreprocessor;
import org.captcha.image.process.ImageWaterDropSegmentor;
import org.captcha.image.util.Constants;
import org.captcha.image.util.ImageUtil;

public class SVMDataPreparer {
	/**
	 * 产生类标签
	 */
	public static void produceImageLabel(){
		FileWriter writer=null;
		try {
			writer = new FileWriter(new File(Constants.SVM_LABEL_FILE));
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
	
	/**
	 * 加载类标签(训练)
	 */
	public static void loadImageLabel(HashMap<String, Integer> labelMap){
		BufferedReader reader=null;
		try {
			 reader=new BufferedReader(new FileReader(new File(Constants.SVM_LABEL_FILE)));
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
		File dir=new File(Constants.SVM_TRAIN_DIR);
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
				transformImage2MapForTrain(file, imageMap);
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
	private static void transformImage2MapForTrain(File file, HashMap<String, ArrayList<Integer[][]>> imageMap) throws IOException{
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
	 * 加载图片，供测试用
	 */
	public static void loadImageForTest(HashMap<String, Integer[][]> imageMap){
		File dir = new File(Constants.SVM_TEST_DIR);
		File[] files=dir.listFiles(new FilenameFilter(){
			public boolean isJpg(String file) {
				if (file.toLowerCase().endsWith(".jpg"))
					return true;
				else
					return false;
			}
			@Override
			public boolean accept(File dir, String name) {
				return isJpg(name);
			}
		});

		for(File file : files){
			try{
				transformImage2MapForTest(file, imageMap);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("load mage end");
	}
	
	/**
	 * 将image 转换到 map中
	 * @param file
	 * @throws IOException
	 */
	private static void transformImage2MapForTest(File file, HashMap<String, Integer[][]> imageMap) throws IOException{
		BufferedImage image=ImageIO.read(file);
		int width=image.getWidth();
		int height=image.getHeight();
		Integer[][] imgArr=new Integer[height][width];
		
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				int value=ImageUtil.isBlack(image.getRGB(x, y)) ? 1 : 0;//黑色点标记为1
				imgArr[y][x]=value;
			}
		}
		imageMap.put(file.getName(), imgArr);
	}
	
	/**
	 * 加载类标签(预测)
	 */
	public static void loadLabelMapForPredict(HashMap<Integer, String> labelMap){
		BufferedReader reader=null;
		try {
			 reader=new BufferedReader(new FileReader(new File(Constants.SVM_LABEL_FILE)));
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
	
	public static void loadImageForPredict(File file, ArrayList<BufferedImage> images) throws IOException{
		BufferedImage sourceImage=ImageIO.read(file);
		ImagePreprocessor preprocessor=new ImagePreprocessor();
		BufferedImage binaryImage=preprocessor.getBinaryImage(sourceImage);
		
		ImageCFSSegmentor cfs_segmentor=new ImageCFSSegmentor();
		ArrayList<BufferedImage> interList=cfs_segmentor.cfs_segment(binaryImage);
		
		ImageWaterDropSegmentor drop_segmentor=new ImageWaterDropSegmentor();
		for(BufferedImage img:interList){
			ArrayList<BufferedImage> tmpList=drop_segmentor.drop_segment(img);
			for(BufferedImage sumImg:tmpList)
				images.add(ImageUtil.scaleImage(sumImg));
		}
		for(int i=0; i<images.size(); i++)
			ImageIO.write(images.get(i), "JPG", new File(Constants.TMP_DIR+"/" + i + ".jpg"));
	}
}
