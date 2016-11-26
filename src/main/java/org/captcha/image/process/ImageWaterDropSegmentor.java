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
	private int minD = 8;   // 最小字符宽度
	private int maxD = 16;  // 最大字符宽度
	private int meanD = 12; // 平均字符宽度
	private int b = 1;      // 大水滴的宽度 2*B+1,取0或者1效果最好

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
	 * 滴水法入口
	 * 
	 * @param sourceImage
	 * @return 切割完图片的数组
	 */
	public ArrayList<BufferedImage> drop_segment(BufferedImage srcImage){
		ArrayList<BufferedImage> images=new ArrayList<BufferedImage>();

		int width=srcImage.getWidth();
		int height=srcImage.getHeight();
		
		// if (width <= maxD) {
		// //如果是单个字符，则直接返回
		// this.imageList.add(sourceImage);
		// return this.imageList;
		// }

		// 在x轴的投影
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

		int num = (int) Math.round((double) width / meanD);// 字符的个数
		int lastP = 0; // 上一次分割的位置
		int curSplit = 1;// 分割点的个数，小于等于 num - 1;
		for (int i = 0; i < extrems.size(); i++) {
			if (curSplit > num - 1) {
				break;
			}

			// 判断两个分割点之间的距离是否合法
			int curP = extrems.get(i);
			int dBetween = curP - lastP + 1;
			if (dBetween < minD || dBetween > maxD) {
				continue;
			}

			// //判断当前分割点与末尾结束点的位置是否合法
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
	 * 获得滴水的路径
	 * 
	 * @param startP
	 * @param height
	 * @return
	 */
	private Point[] getEndRoute(BufferedImage srcImage, Point startP, int height, int curSplit){
		// 获得分割的路径
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

			if(sum==0)    // 如果全黑，需要看惯性
				maxW=4;
			if(sum==15)   // 如果周围全白，则默认垂直下落
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
				if(nextX>curP.x){   // 具有向右的惯性
					nextX=curP.x+1;
					nextY=curP.y+1;
				}

				if(nextX<curP.x){   // 向左的惯性或者sum = 0
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

			// 如果出现重复运动
			if(lastP.x==nextX && lastP.y==nextY){
				if(nextX<curP.x){// 向左重复
					maxW=5;
					nextX=curP.x+1;
					nextY=curP.y+1;
				}else{           // 向右重复
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
	 * 具体实行切割
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
	 * 获得大水滴中心点周围的像素值
	 * 
	 * @param cx
	 * @param cy
	 * @param j
	 *            中心点周围的编号
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

		// 如果 1<= j <= 3, 则判断下方的区域， 只要有一个黑点，则当做黑点，
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
