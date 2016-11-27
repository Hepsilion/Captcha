package org.captcha.image.util;

public class Constants {
	public static final String SVM_LABEL_FILE= "svm/label.txt";
	public static final String SVM_TRAN_FILE = "svm/svm.train";
	public static final String SVM_MODEL_FILE ="svm/svm.model";
	public static final String SVM_TEST_FILE = "svm/svm.test";
	public static final String SVM_RESULT_FILE = "svm/result.txt";
	
	
	public static final String SVM_TRAIN_DIR="images/train";
	public static final String SVM_TEST_DIR="images/test";
	public static final String SVM_PREDICT_DIR="images/predict";
	
	public static final String DOWNLOAD_DIR="images/0_download";
	public static final String GRAY_FILE_DIR="images/1_gray";
	public static final String CFS_SEGMENT_DIR="images/2_cfs";
	public static final String DROP_SEGMENT_DIR="images/3_drop";
	public static final String SCALE_DIR="images/4_scale";
	public static final String TMP_DIR="images/tmp";
	
	
	public static final int MIN_RECTANGLE_HEIGHT=8;
	public static final int MIN_RECTANGLE_WIDTH=5;
	public static final int MIN_RECTANGLE_PIXELS=30;
	
	/**
	 * average character width
	 */
	public static final int AVERAGE_CHARACTER_WIDTH= 12;
	
	
	public static final int COLOR_WHITE = 0xffffffff;
	public static final int COLOR_BLACK = 0xff000000;
}