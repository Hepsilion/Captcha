package org.captcha.image.process;

public class ImageScalerTest {
	public static void main(String[] args) {
		String inDir="images/3_drop";
		String outDir="images/4_scale";
		
		System.out.println("---begin---");
		long start = System.currentTimeMillis();
		ImageScaler scaler = new ImageScaler();
		scaler.scale(inDir, outDir);
		long end = System.currentTimeMillis();
		System.out.println("ºÄÊ±£º" + (end - start));
		System.out.println("---end----");
	}
}