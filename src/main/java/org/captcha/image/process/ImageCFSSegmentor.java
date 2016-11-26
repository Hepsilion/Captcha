package org.captcha.image.process;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.captcha.image.model.Point;
import org.captcha.image.model.SubImage;
import org.captcha.image.util.ImageUtil;

/**
 * ʹ��CFS��ͨ���и�
 *
 */
public class ImageCFSSegmentor {
	/**
	 * ��ȡ�Ҷ�ͼƬ�������и�
	 * @param inDir
	 * @param outDir
	 */
	public void segment(String inDir, String outDir){
		File dir=new File(inDir);
		File[] files=dir.listFiles(new FilenameFilter(){
			public boolean isJpg(String file) {
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

		for(int i=0; i<files.length; i++) {
			try{
				File file=files[i];
				BufferedImage image=ImageIO.read(file);
				ArrayList<BufferedImage> images=cfs_segment(image);
				for (int j=0; j<images.size(); j++){
					BufferedImage subImg=images.get(j);
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
	 * cfs���зָ�,���طָ�������
	 * 
	 * @param sourceImage
	 * @return
	 */
	public ArrayList<BufferedImage> cfs_segment(BufferedImage srcImage) {
		int width=srcImage.getWidth();
		int height=srcImage.getHeight();

		ArrayList<SubImage> subImgList=new ArrayList<SubImage>(); // ������ͼ��
		HashMap<String, Boolean> visited=new HashMap<String, Boolean>(); // �Ѿ����ʹ��ĵ�
		for(int x=0; x<width; x++){
			for(int y=0; y<height; y++){
				int rgb=srcImage.getRGB(x, y);
				String key=x+"-"+y;
				// ������Ǻ�ɫ�������Ѿ������ʹ���������cfg
				if(!ImageUtil.isBlack(rgb) || visited.containsKey(key)) {
					continue;
				}

				// �����ɫ����û�з��ʣ����Դ˵㿪ʼ������ͨ��̽��
				SubImage subImage=new SubImage();                 // ���浱ǰ�ַ���������
				LinkedList<Point> queue=new LinkedList<Point>();  // ���浱ǰ�ַ���ķ��ʶ���
				
				queue.offer(new Point(x, y, true));
				visited.put(key, true);
				subImage.pixelList.add(new Point(x, y, true));
				subImage.left = x;
				subImage.top = y;
				subImage.right = x;
				subImage.bottom = y;

				while(queue.size()!=0){
					Point tmp=queue.poll();

					// ��ѰĿ��İ˸�����
					int startX=(tmp.x-1<0) ? 0:tmp.x-1;
					int startY=(tmp.y-1<0) ? 0:tmp.y-1;
					int endX=(tmp.x+1>width-1) ? width-1:tmp.x+1;
					int endY=(tmp.y+1>height-1) ? height-1:tmp.y+1;

					for(int tx=startX; tx<=endX; tx++){
						for(int ty=startY; ty<=endY; ty++){
							if(tx==tmp.x && ty==tmp.y)
								continue;

							key = tx + "-" + ty;
							System.out.println(key);
							if (ImageUtil.isBlack(srcImage.getRGB(tx, ty)) && !visited.containsKey(key)) {
								queue.offer(new Point(tx, ty, true));
								visited.put(key, true);
								subImage.pixelList.add(new Point(tx, ty, true)); // ���뵽·����

								// ���±߽�����
								subImage.left = Math.min(subImage.left, tx);
								subImage.top = Math.min(subImage.top, ty);
								subImage.right = Math.max(subImage.right, tx);
								subImage.bottom = Math.max(subImage.bottom, ty);
							}
						}
					}
				}// end of while

				subImage.width=subImage.right-subImage.left+1;
				subImage.height=subImage.bottom-subImage.top+1;
				subImgList.add(subImage);
			}
		}
		System.out.println();
		
		return cfsToImage(subImgList);
	}

	private ArrayList<BufferedImage> cfsToImage(ArrayList<SubImage> subImgList){
		ArrayList<BufferedImage> images=new ArrayList<BufferedImage>();
		
		for(int i=0; i<subImgList.size(); i++){
			SubImage subImage=subImgList.get(i);
			BufferedImage image=new BufferedImage(subImage.width, subImage.height, BufferedImage.TYPE_BYTE_BINARY);
			for(int x=0; x<subImage.width; x++){
				for(int y=0; y<subImage.height; y++)
					image.setRGB(x, y, new Color(255, 255, 255).getRGB());
			}
			ArrayList<Point> pixeList=subImage.pixelList;
			for(Point point:pixeList){
				// System.out.println("("+(point.x-subImage.left)+","+(point.y-subImage.top)+")");
				image.setRGB(point.x-subImage.left, point.y-subImage.top, new Color(0, 0, 0).getRGB());
			}

			// ���и���м�ͼƬ���뵽images��
			images.add(image);
		}
		return images;
	}
}
