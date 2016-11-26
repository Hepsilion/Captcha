package org.captcha.image.svm;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.captcha.image.util.ImageUtil;

public class SVMFormater {
	/**
	 * ת��Ϊsvm ���Եĸ�ʽ
	 * @param imageList
	 */
	public static void transform2SVMTestFormat(ArrayList<BufferedImage> images){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File("svm/svm.test"));
			for (BufferedImage image : images) {
				int width = image.getWidth();
				int height = image.getHeight();
				int index = 1;
				String tmpLine = "-1 ";//Ĭ���ޱ�ţ���Ϊ-1
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int value = ImageUtil.isBlack(image.getRGB(x, y)) ? 1 : 0;//��ɫ����Ϊ1
						tmpLine += index + ":" + value + " ";
						index ++;
					}
				}
				writer.write(tmpLine + "\r\n");
				writer.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (writer != null) {
				writer.close();
			}
		}
	}
}
