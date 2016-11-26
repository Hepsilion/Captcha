package org.captcha.image.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.jhlabs.image.ScaleFilter;

public class ImageUtil {
	/**
	 * �ж��Ƿ�λ��ɫ����
	 * @param rgb
	 * @return
	 */
	public static boolean isBlack(int rgb){
		if(getColorBright(rgb)<=300){
			return true;
		}
		return false;
	}
	
	public static boolean isWhite(int rgb) {
		if (getColorBright(rgb)>300) {
			return true;
		}
		return false;
	}
	
	public static int isBlackOrWhite(int rgb) {
		if(getColorBright(rgb)<30 || getColorBright(rgb)>730)
			return 1;
		return 0;
	}
	
	public static int getColorBright(int rgb) {
		Color color=new Color(rgb);
		return color.getRed()+color.getGreen()+color.getBlue();
	}
	
	/**
	 * ����ͼƬ,Ĭ��16x16
	 * @param img
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage img) {
		return scaleImage(img, 16, 16);
	}
	
	/**
	 * ����ͼƬ
	 * @param img
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage img, int width, int height){
		ScaleFilter sf=new ScaleFilter(width,height);
		BufferedImage imgdest=new BufferedImage(width, height, img.getType());
		return sf.filter(img, imgdest);
	}
	
	/**
	 * ���ѵ����ͼƬ�ķ��࣬��a-12.jpg������a
	 * @param filename
	 * @return
	 */
	public static String getImgClass(String filename){
		String[] arr=filename.split("-");
		if(arr!=null)
			return arr[0];
		else
			return "";
	}
}
