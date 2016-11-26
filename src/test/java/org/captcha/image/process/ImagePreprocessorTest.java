package org.captcha.image.process;

public class ImagePreprocessorTest {
	public static void main(String[] args) {
		String inDir="images/0_download";
		String outDir="images/1_gray";
		
		System.out.println("---begin---");
		long start = System.currentTimeMillis();
		ImagePreprocessor preprocessor = new ImagePreprocessor();
		preprocessor.preprocess(inDir, outDir);
		long end = System.currentTimeMillis();
		System.out.println("ºÄÊ±£º" + (end - start));
		System.out.println("---end----");
	}
}
