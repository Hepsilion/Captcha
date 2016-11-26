package org.captcha.image.svm;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import libsvm.svm_predict;

import org.captcha.image.process.ImageCFSSegmentor;
import org.captcha.image.process.ImagePreprocessor;
import org.captcha.image.process.ImageWaterDropSegmentor;
import org.captcha.image.util.ImageUtil;

public class SVMPredictor {
	/**
	 * 类标号map，如：a=>1 b=>2
	 */
	private HashMap<String, Integer> labelMap = null;
	
	private HashMap<String, Integer[][]> imageMap = null;
	
	public SVMPredictor(){
		labelMap=new HashMap<String, Integer>();
		imageMap=new HashMap<String, Integer[][]>();
		SVMDataPreparer.loadImageLabel(labelMap);
	}
	
	public static void predict(ArrayList<BufferedImage> images){
		SVMFormater.transform2SVMTestFormat(images);
		
		String[] args={"svm/svm.test", "svm/svm.model", "svm/result.txt"};
		try{
			System.out.println("预测开始");
			svm_predict.main(args);
			System.out.println("预测结束");
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 具体的预测，返回识别的文字
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String predict(File file) throws IOException{
		HashMap<Integer, String> labelMap=new HashMap<Integer, String>();
		SVMDataPreparer.loadLabelMapForPredict(labelMap);
		
		BufferedImage sourceImage=ImageIO.read(file);
		ImagePreprocessor preprocessor=new ImagePreprocessor();
		BufferedImage binaryImage=preprocessor.getBinaryImage(sourceImage);
		
		ImageCFSSegmentor cfs_segmentor=new ImageCFSSegmentor();
		ArrayList<BufferedImage> interList=cfs_segmentor.cfs_segment(binaryImage);
		
		ArrayList<BufferedImage> images=new ArrayList<BufferedImage>();
		ImageWaterDropSegmentor drop_segmentor=new ImageWaterDropSegmentor();
		for(BufferedImage img:interList){
			ArrayList<BufferedImage> tmpList=drop_segmentor.drop_segment(img);
			for(BufferedImage sumImg:tmpList)
				images.add(ImageUtil.scaleImage(sumImg));
		}
		for(int i=0; i<images.size(); i++)
			ImageIO.write(images.get(i), "JPG", new File("tmp/" + i + ".jpg"));
		
		predict(images);
		String result="";
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(new File("svm/result.txt")));
			String buff="";
			while((buff=reader.readLine())!=null){
				int label=(int)Double.parseDouble(buff);
				String className=labelMap.get(label);
				result+=className+" ";
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(reader!=null)
				reader.close();
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException{
		SVMPredictor p = new SVMPredictor();
		String result=p.predict(new File("images/0_download/4.jpg"));
		System.out.println(result);
		
//		try {
//			BufferedImage image=ImageIO.read(new File("images/4_scale/1-0-0.jpg"));
//			ArrayList<BufferedImage> list=new ArrayList<BufferedImage>();
//			list.add(image);
//			predict(list);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}