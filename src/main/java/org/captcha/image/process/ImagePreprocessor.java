package org.captcha.image.process;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * �ҶȻ�����
 *
 */
public class ImagePreprocessor {
	/**
	 * �ļ��ҶȻ�����ֵ��
	 * 
	 * @param inDir ����ͼ���ļ�����Ŀ¼
	 * @param outDir ��ֵ���������ļ�Ŀ¼
	 */
	public void preprocess(String inDir, String outDir) {
		File dir=new File(inDir);
		File[] files=dir.listFiles(new FilenameFilter() {
			public boolean isJpg(String file) {
				if(file.toLowerCase().endsWith(".jpg"))
					return true;
				else
					return false;
			}

			@Override
			public boolean accept(File dir, String name) {
				return isJpg(name);
			}
		});

		for(File file:files) {
			try {
				BufferedImage img=ImageIO.read(file);
				BufferedImage binaryImg=getBinaryImage(img);
				ImageIO.write(binaryImg, "JPG", new File(outDir+"/"+file.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ֵ��
	 * 
	 * @param sourceImage
	 * @return ��ֵ��֮���ͼ��
	 */
	public BufferedImage getBinaryImage(BufferedImage srcImage) {
		double Wr=0.299, Wg=0.587, Wb=0.114;

		int width=srcImage.getWidth();
		int height=srcImage.getHeight();
		
		// �ҶȻ�
		int[][] gray=new int[width][height];
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				Color color=new Color(srcImage.getRGB(x, y));
				int rgb=(int)((color.getRed()*Wr+color.getGreen()*Wg+color.getBlue()*Wb)/3);
				gray[x][y]=rgb;
			}
		}

		// ��ֵ��
		BufferedImage binaryBufferedImage=new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		int threshold=getOstu(gray, width, height);
		for(int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				if (gray[x][y]>threshold) {
					int max=new Color(255, 255, 255).getRGB();
					gray[x][y]=max;
				} else {
					int min=new Color(0, 0, 0).getRGB();
					gray[x][y]=min;
				}
				binaryBufferedImage.setRGB(x, y, gray[x][y]);
			}
		}

		return binaryBufferedImage;
	}

	/**
	 * ��ö�ֵ��ͼ�� �����䷽�
	 * 
	 * @param gray
	 * @param width
	 * @param height
	 */
	private int getOstu(int[][] gray_array, int width, int height) {
		int grayLevel=256;
		int[] pixelNum=new int[grayLevel];
		// ��������ɫ�׵�ֱ��ͼ
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				int color=gray_array[x][y];
				pixelNum[color]++;
			}
		}

		double sum=0;
		int total=0;
		for(int i=0; i<grayLevel; i++) {
			sum+=i*pixelNum[i];   //x*f(x)�����أ�Ҳ����ÿ���Ҷȵ�ֵ�������������һ����Ϊ���ʣ���sumΪ���ܺ�
			total+=pixelNum[i];   //nΪͼ���ܵĵ�������һ��������ۻ�����
		}
		double sumB=0;       // ǰ��ɫ�������ܺ�
		int threshold=0;
		double wF=0;         // ǰ��ɫȨ��
		double wB=0;         // ����ɫȨ��
		double maxFreq=-1.0; // �����䷽��

		for(int i=0; i<grayLevel; i++) {
			wB+=pixelNum[i]; // wBΪ�ڵ�ǰ��ֵ����ͼ��ĵ���
			if(wB==0)        // û�зֳ�ǰ����
				continue;

			wF=total-wB;     // wBΪ�ڵ�ǰ��ֵǰ��ͼ��ĵ���
			if(wF==0)        // ȫ��ǰ��ͼ�������ֱ��break
				break;

			sumB+=(double)(i*pixelNum[i]);
			double meanB=sumB/wB;
			double meanF=(sum-sumB)/wF;
			// freqΪ��䷽��
			double freq=(double)(wF)*(double)(wB)*(meanB-meanF)*(meanB-meanF);
			if(freq>maxFreq){
				maxFreq=freq;
				threshold=i;
			}
		}

		return threshold;
	}
}
