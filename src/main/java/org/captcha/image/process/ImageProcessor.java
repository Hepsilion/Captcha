package org.captcha.image.process;

import org.captcha.image.util.Constants;

public class ImageProcessor {
	private ImagePreprocessor preprocessor;
	private ImageCFSSegmentor cfs_segmentor;
	private ImageWaterDropSegmentor drop_segmentor;
	private ImageScaler scaler;
	
	public ImageProcessor(){
		this.preprocessor=new ImagePreprocessor();
		this.cfs_segmentor=new ImageCFSSegmentor();
		this.drop_segmentor=new ImageWaterDropSegmentor();
		this.scaler=new ImageScaler();
	}
	
	public void process(){
		this.preprocessor.preprocess(Constants.DOWNLOAD_DIR, Constants.GRAY_FILE_DIR);
		this.cfs_segmentor.segment(Constants.GRAY_FILE_DIR, Constants.CFS_SEGMENT_DIR);
		this.drop_segmentor.segment(Constants.CFS_SEGMENT_DIR, Constants.DROP_SEGMENT_DIR);
		this.scaler.scale(Constants.DROP_SEGMENT_DIR, Constants.SCALE_DIR);
	}
}
