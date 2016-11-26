package org.captcha.image.process;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.captcha.image.util.ImageUtil;

/**
 * 将原图片缩放成统一大小的图片
 *
 */
public class ImageScaler {
	public void scale(String inDir, String outDir) {
		File dir=new File(inDir);
		File[] files=dir.listFiles(new FilenameFilter(){
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

		File targetDir=new File(outDir);
		if(!targetDir.exists())
			targetDir.mkdir();

		for(File file:files){
			BufferedImage sourceImage;
			try{
				sourceImage=ImageIO.read(file);
				BufferedImage targetImage=ImageUtil.scaleImage(sourceImage);
				ImageIO.write(targetImage, "JPG", new File(outDir+"/"+file.getName()));
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
