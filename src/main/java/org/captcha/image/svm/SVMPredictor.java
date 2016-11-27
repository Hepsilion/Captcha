package org.captcha.image.svm;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.captcha.image.util.Constants;

import libsvm.svm_predict;

public class SVMPredictor {
	HashMap<Integer, String> labelMap=null;
	
	public SVMPredictor(){
		labelMap=new HashMap<Integer, String>();
		SVMDataPreparer.produceImageLabel();
		SVMDataPreparer.loadLabelMapForPredict(labelMap);
	}
	
	public void run(){
		String[] args={Constants.SVM_TEST_FILE, Constants.SVM_MODEL_FILE, Constants.SVM_RESULT_FILE};
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
	 * @param 待预测图片
	 * @return
	 * @throws IOException
	 */
	public String predict(String filename) throws IOException{
		File file=new File(filename);
		ArrayList<BufferedImage> images=new ArrayList<BufferedImage>();
		SVMDataPreparer.loadImageForPredict(file, images);
		SVMFormater.transformData2SvmFormatForPredict(images);
		run();
		
		String result="";
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(new File(Constants.SVM_RESULT_FILE)));
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
		SVMPredictor predictor = new SVMPredictor();
		String result=predictor.predict(Constants.SVM_PREDICT_DIR+"/"+"4-299.jpg");
		System.out.println(result);
	}
}
