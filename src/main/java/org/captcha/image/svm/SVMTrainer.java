package org.captcha.image.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.captcha.image.util.Constants;

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
		SVMDataPreparer.produceImageLabel();
		SVMDataPreparer.loadImageLabel(labelMap);
	}
	
	private void run(String[] args){
		try {
			System.out.println("ѵ����ʼ");
			svm_train.main(args);
			System.out.println("ѵ������");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void train(String[] args){
		SVMDataPreparer.loadImageForTrain(imageMap);
		SVMFormater.transformData2SvmFormatForTrain(labelMap, imageMap);
		run(args);
	}
	
	public static void main(String[] args){
		//train����
		String[] arg = {"-t", "0", Constants.SVM_TRAN_FILE, Constants.SVM_MODEL_FILE};
		SVMTrainer trainer=new SVMTrainer();
		trainer.train(arg);
	}
}