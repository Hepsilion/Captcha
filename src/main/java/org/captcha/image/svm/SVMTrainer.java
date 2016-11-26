package org.captcha.image.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import libsvm.svm_train;

public class SVMTrainer{
	/**
	 * 类标号map，如：a=>1 b=>2
	 */
	private HashMap<String, Integer> labelMap=null;
	
	/**
	 * 所有图像分类的map，key为当前类标号， value为对应的图片，图片以二维数组的形式保存
	 */
	private HashMap<String, ArrayList<Integer[][]>> imageMap=null;
	
	public SVMTrainer(){
		labelMap=new HashMap<String, Integer>();
		imageMap=new HashMap<String, ArrayList<Integer[][]>>();
		SVMDataPreparer.loadImageLabel(labelMap);
		SVMDataPreparer.loadImageForTrain(imageMap);
		SVMDataPreparer.transformData2SvmFormatForTrain(labelMap, imageMap);
	}
	
	public static void run(String[] args){
		try {
			System.out.println("训练开始");
			svm_train.main(args);
			System.out.println("训练结束");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void train(String[] args){
		run(args);
	}
	
	public static void main(String[] args){
		//train参数
		String[] arg = {"-t","0","svm/svm.train","svm/svm.model"};
		SVMTrainer trainer=new SVMTrainer();
		trainer.train(arg);
	}
}