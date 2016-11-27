package org.captcha.image.svm;

import java.io.IOException;
import java.util.HashMap;

import org.captcha.image.util.Constants;

import libsvm.svm_predict;

public class SVMTester {
	/**
	 * 类标号map，如：a=>1 b=>2
	 */
	private HashMap<String, Integer> labelMap = null;
	
	private HashMap<String, Integer[][]> imageMap = null;
	
	public SVMTester(){
		labelMap=new HashMap<String, Integer>();
		imageMap=new HashMap<String, Integer[][]>();
		SVMDataPreparer.produceImageLabel();
		SVMDataPreparer.loadImageLabel(labelMap);
	}
	
	private void run(String[] args){
		try{
			System.out.println("测试开始");
			svm_predict.main(args);
			System.out.println("测试结束");
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void test(String[] args){
		SVMDataPreparer.loadImageForTest(imageMap);
		SVMFormater.transformData2SvmFormatForTest(labelMap, imageMap);
		run(args);
	}

	public static void main(String[] args){
		//test参数
		String[] arg={Constants.SVM_TEST_FILE, Constants.SVM_MODEL_FILE, Constants.SVM_RESULT_FILE};
		SVMTester tester=new SVMTester();
		tester.test(arg);
	}
}