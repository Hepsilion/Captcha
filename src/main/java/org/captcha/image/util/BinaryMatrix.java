package org.captcha.image.util;

import java.awt.image.BufferedImage;

public class BinaryMatrix {
	private final boolean[][] array;
	private final int height;
	private final int width;
	
	public BinaryMatrix(boolean[][] array) {
		this.array = array;
		this.height = array.length;
		this.width = array[0].length;
	}
	
	public static BinaryMatrix fromArray(boolean[][] array){
		return new BinaryMatrix(array);
	}
	
	/**
	 * generate a blank matrix
	 * @param height
	 * @param width
	 * @return
	 */
	public static BinaryMatrix fromBlank(int height, int width){
		boolean[][] array=new boolean[height][width];
		return new BinaryMatrix(array);
	}
	
	/**
	 * Load binary matrix from image, only colors whose RGB equals to 0xff000000 are set to black
	 * @param image
	 * @return
	 */
	public static BinaryMatrix fromImage(BufferedImage image){
		int height=image.getHeight();
		int width=image.getWidth();
		
		boolean[][] array=new boolean[height][width];
		for(int h=0; h<height; h++){
			for(int w=0; w<width; w++){
				if(image.getRGB(w, h)==Constants.COLOR_BLACK)
					array[h][w]=true;
				else
					array[h][w]=false;
			}
		}
		return new BinaryMatrix(array);
	}

	/**
	 * remove blank paddings
	 * @return
	 */
//	public BinaryMatrix trim(){
//		
//	}
	
	/**
	 * check height, width and pixels of image larger than minimum values
	 * @return
	 */
	public boolean isValid(){
		if(this.height<Constants.MIN_RECTANGLE_HEIGHT || this.width<Constants.MIN_RECTANGLE_WIDTH)
			return false;
		int pixels=0;
		for(int h=0; h<this.height; h++){
			for(int w=0; w<this.width; w++){
				if(this.array[h][w])
					pixels++;
			}
		}
		if(pixels<Constants.MIN_RECTANGLE_PIXELS)
			return false;
		return true;
	}
	
	public boolean getValue(int x, int y){
		return this.array[y][x];
	}
	
	public void setValue(int x, int y, boolean value){
		this.array[y][x]=value;
	}
	
	public void setTrue(int x, int y){
		this.array[y][x]=true;
	}
	
	public void setFalse(int x, int y){
		this.array[y][x]=false;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append(this.height+"X"+this.width+"\n");
		for(int h=0; h<this.height; h++){
			for(int w=0; w<this.width; w++){
				if(this.array[h][w])
					sb.append("1");
				else
					sb.append("0");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
