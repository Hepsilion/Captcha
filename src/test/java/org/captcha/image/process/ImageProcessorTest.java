package org.captcha.image.process;


public class ImageProcessorTest {
	public static void main(String[] args){
		System.out.println("---begin---");
		long start = System.currentTimeMillis();
		ImageProcessor processor=new ImageProcessor();
		processor.process();
		long end = System.currentTimeMillis();
		System.out.println("ºÄÊ±£º" + (end - start));
		System.out.println("---end----");
	}
}
