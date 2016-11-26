package org.captcha.image.process;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.captcha.image.model.Point;
import org.captcha.image.util.Extremum;
import org.captcha.image.util.ImageUtil;

public class ImageWaterDropSegmentor {
	private int minD = 8;   // ��С�ַ����
	private int maxD = 16;  // ����ַ����
	private int meanD = 12; // ƽ���ַ����
	private int b = 1;      // ��ˮ�εĿ�� 2*B+1,ȡ0����1Ч�����

	public void segment(String inDir, String outDir){
		File dir=new File(inDir);
		File[] files=dir.listFiles(new FilenameFilter(){
			public boolean isJpg(String file){
				if(file.toLowerCase().endsWith(".jpg"))
					return true;
				else
					return false;
			}

			@Override
			public boolean accept(File dir, String name){
				return isJpg(name);
			}
		});

		for(File file:files){
			try{
				BufferedImage image=ImageIO.read(file);
				ArrayList<BufferedImage> list=drop_segment(image);
				for(int j=0; j<list.size(); j++){
					BufferedImage subImg=list.get(j);
					String prex=file.getName().split("\\.")[0];
					String filename=outDir+"/"+prex+"-"+j+".jpg";
					ImageIO.write(subImg, "JPG", new File(filename));
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ˮ�����
	 * 
	 * @param sourceImage
	 * @return �и���ͼƬ������
	 */
	public ArrayList<BufferedImage> drop_segment(BufferedImage srcImage){
		ArrayList<BufferedImage> images=new ArrayList<BufferedImage>();

		int width=srcImage.getWidth();
		int height=srcImage.getHeight();
		
		// if (width <= maxD) {
		// //����ǵ����ַ�����ֱ�ӷ���
		// this.imageList.add(sourceImage);
		// return this.imageList;
		// }

		// ��x���ͶӰ
		int[] histData=new int[width];
		for(int x=0; x<width; x++){
			for(int y=0; y<height; y++){
				if(ImageUtil.isBlack(srcImage.getRGB(x, y)))
					histData[x]++;
			}
		}

		ArrayList<Integer> extrems=Extremum.getMinExtrem(histData);

		Point[] startRoute=new Point[height];
		Point[] endRoute=null;

		for(int y=0; y<height; y++)
			startRoute[y] = new Point(0, y);

		int num = (int) Math.round((double) width / meanD);// �ַ��ĸ���
		int lastP = 0; // ��һ�ηָ��λ��
		int curSplit = 1;// �ָ��ĸ�����С�ڵ��� num - 1;
		for (int i = 0; i < extrems.size(); i++) {
			if (curSplit > num - 1) {
				break;
			}

			// �ж������ָ��֮��ľ����Ƿ�Ϸ�
			int curP = extrems.get(i);
			int dBetween = curP - lastP + 1;
			if (dBetween < minD || dBetween > maxD) {
				continue;
			}

			// //�жϵ�ǰ�ָ����ĩβ�������λ���Ƿ�Ϸ�
			// int dAll = width - curP + 1;
			// if (dAll < minD*(num - curSplit) || dAll > maxD*(num - curSplit))
			// {
			// continue;
			// }
			endRoute = getEndRoute(srcImage, new Point(curP, 0), height,
					curSplit);
			doSplit(images, srcImage, startRoute, endRoute);
			startRoute = endRoute;
			lastP = curP;
			curSplit++;
			System.out.println(curP);
		}

		endRoute = new Point[height];
		for (int y = 0; y < height; y++) {
			endRoute[y] = new Point(width - 1, y);
		}
		doSplit(images, srcImage, startRoute, endRoute);

		System.out.println("=================");
		System.out.println(width + "," + height);

		return images;
	}

	/**
	 * ��õ�ˮ��·��
	 * 
	 * @param startP
	 * @param height
	 * @return
	 */
	private Point[] getEndRoute(BufferedImage srcImage, Point startP, int height, int curSplit){
		// ��÷ָ��·��
		Point[] endRoute=new Point[height];
		Point curP=new Point(startP.x, startP.y);
		Point lastP=curP;
		endRoute[0]=curP;
		while(curP.y<height-1){
			int maxW=0;
			int sum=0;
			int nextX=curP.x;
			int nextY=curP.y;

			for(int j=1; j<=5; j++){
				try{
					int curW=getPixelValue(srcImage, curP.x, curP.y, j)*(6-j);
					sum+=curW;
					if(curW>maxW)
						maxW=curW;
				}catch(Exception e){
					e.printStackTrace();
					System.exit(0);
				}
			}

			if(sum==0)    // ���ȫ�ڣ���Ҫ������
				maxW=4;
			if(sum==15)   // �����Χȫ�ף���Ĭ�ϴ�ֱ����
				maxW=6;

			switch(maxW){
			case 1:
				nextX=curP.x-1;
				nextY=curP.y;
				break;
			case 2:
				nextX=curP.x+1;
				nextY=curP.y;
				break;
			case 3:
				nextX=curP.x+1;
				nextY=curP.y+1;
				break;
			case 5:
				nextX=curP.x-1;
				nextY=curP.y+1;
				break;
			case 6:
				nextX=curP.x;
				nextY=curP.y+1;
				break;
			case 4:
				if(nextX>curP.x){   // �������ҵĹ���
					nextX=curP.x+1;
					nextY=curP.y+1;
				}

				if(nextX<curP.x){   // ����Ĺ��Ի���sum = 0
					nextX=curP.x;
					nextY=curP.y+1;
				}

				if(sum==0){
					nextX=curP.x;
					nextY=curP.y+1;
				}
				break;
			default:
				break;
			}

			// ��������ظ��˶�
			if(lastP.x==nextX && lastP.y==nextY){
				if(nextX<curP.x){// �����ظ�
					maxW=5;
					nextX=curP.x+1;
					nextY=curP.y+1;
				}else{           // �����ظ�
					maxW=3;
					nextX=curP.x-1;
					nextY=curP.y+1;
				}
			}

			lastP=curP;
			int rightLimit=meanD*curSplit+1;
			if(nextX>rightLimit){
				nextX=rightLimit;
				nextY=curP.y+1;
			}

			int leftLimit=meanD*(curSplit-1)+meanD/2;
			if(nextX<leftLimit){
				nextX=leftLimit;
				nextY=curP.y+1;
			}
			curP=new Point(nextX, nextY);
			endRoute[curP.y]=curP;
		}
		return endRoute;
	}

	/**
	 * ����ʵ���и�
	 * 
	 * @param starts
	 * @param ends
	 */
	private void doSplit(ArrayList<BufferedImage> images, BufferedImage srcImage, Point[] starts, Point[] ends){
		int left=starts[0].x;
		int top=starts[0].y;
		int right=ends[0].x;
		int bottom=ends[0].y;

		for(int i=0; i<starts.length; i++) {
			left=Math.min(starts[i].x, left);
			top=Math.min(starts[i].y, top);
			right=Math.max(ends[i].x, right);
			bottom=Math.max(ends[i].y, bottom);
		}

		int width=right-left+1;
		int height=bottom-top+1;
		BufferedImage image=new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

		for(int x=0; x<width; x++){
			for(int y= 0; y<height; y++)
				image.setRGB(x, y, new Color(255, 255, 255).getRGB());
		}
		for(int i=0; i<ends.length; i++){
			Point start=starts[i];
			Point end=ends[i];
			for(int x=start.x; x<end.x; x++){
				if(ImageUtil.isBlack(srcImage.getRGB(x, i))){
					// System.out.println((x - left) + ", " + (start.y - top));
					image.setRGB(x-left, start.y-top, new Color(0, 0, 0).getRGB());
				}
			}
		}
		images.add(image);
		System.out.println("-----------------------");
	}

	/**
	 * ��ô�ˮ�����ĵ���Χ������ֵ
	 * 
	 * @param cx
	 * @param cy
	 * @param j
	 *            ���ĵ���Χ�ı��
	 * @return
	 */
	private int getPixelValue(BufferedImage srcImage, int cx, int cy, int j) {
		int rgb=0;

		if(j==4){
			int right=cx+b+1;
			right=right>=srcImage.getWidth()-1 ? srcImage.getWidth()-1:right;
			rgb=srcImage.getRGB(right, cy);
			return ImageUtil.isBlack(rgb)?0:1;
		}

		if(j==5){
			int left=cx-b-1;
			left=left<=0?0:left;
			rgb=srcImage.getRGB(left, cy);
			return ImageUtil.isBlack(rgb)?0:1;
		}

		// ��� 1<= j <= 3, ���ж��·������� ֻҪ��һ���ڵ㣬�����ڵ㣬
		int start=cx-b+j-2;
		int end=cx+b+j-2;

		start=start<=0?0:start;
		end=end>=srcImage.getWidth()-1?srcImage.getWidth()-1:end;
		int blackNum=0;
		int whiteNum=0;
		for(int i=start; i<=end; i++){
			rgb=srcImage.getRGB(i, cy+1);
			if(ImageUtil.isBlack(rgb))
				blackNum++;
			else
				whiteNum++;
		}
		return (blackNum>=whiteNum)?0:1;
	}
}
