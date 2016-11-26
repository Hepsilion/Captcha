package org.captcha.image.process;

public class ImageWaterDropSegmentorTest {
	public static void main(String[] args) {
		String inDir="images/2_cfs";
		String outDir="images/3_drop";
		
		System.out.println("---begin---");
		long start = System.currentTimeMillis();
		ImageWaterDropSegmentor segmentor = new ImageWaterDropSegmentor();
		segmentor.segment(inDir, outDir);
		long end = System.currentTimeMillis();
		System.out.println("ºÄÊ±£º" + (end - start));
		System.out.println("---end----");
	}
}
