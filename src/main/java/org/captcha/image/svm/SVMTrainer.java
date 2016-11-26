package org.captcha.image.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import libsvm.svm_train;

public class SVMTrainer{
	/**
	 * ����map���磺a=>1 b=>2
	 */
	private HashMap<String, Integer> labelMap=null;
	
	/**
	 * ����ͼ������map��keyΪ��ǰ���ţ� valueΪ��Ӧ��ͼƬ��ͼƬ�Զ�ά�������ʽ����
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
			System.out.println("ѵ����ʼ");
			svm_train.main(args);
			System.out.println("ѵ������");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void train(String[] args){
		run(args);
	}
	
	public static void main(String[] args){
		//train����
		String[] arg = {"-t","0","svm/svm.train","svm/svm.model"};
		SVMTrainer trainer=new SVMTrainer();
		trainer.train(arg);
	}
}