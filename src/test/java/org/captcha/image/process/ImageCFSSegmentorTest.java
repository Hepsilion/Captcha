package org.captcha.image.process;

public class ImageCFSSegmentorTest {
	public static void main(String[] args) {
		String inDir="images/1_gray";
		String outDir="images/2_cfs";
		
		System.out.println("---begin---");
		long start = System.currentTimeMillis();
		ImageCFSSegmentor segmentor = new ImageCFSSegmentor();
		segmentor.segment(inDir, outDir);
		long end = System.currentTimeMillis();
		System.out.println("ºÄÊ±£º" + (end - start));
		System.out.println("---end----");
	}
}
