package cnyl.catlover.common.util;

import cn.hutool.core.io.FileUtil;

public class FileUtils extends FileUtil{
	public static final String DOT = ".";
	public static final String PNG = "png";
	public static final String JPG = "jpg";
	public static final String GIF = "gif";
	public static final String JPEG = "jpeg";
	public static final String AVI = "avi";
	public static final String MP4 = "mp4";
	public static final String MP3 = "mp3";
	public static final String FLV = "flv";
	
	public static boolean isImage(String suffix){
		
		if(suffix == null){
			return false;
		}
		
		suffix = suffix.replace(DOT, "");
		
		if(suffix.equalsIgnoreCase(PNG) || suffix.equalsIgnoreCase(JPG) || suffix.equalsIgnoreCase(GIF) || suffix.equalsIgnoreCase(JPEG)){
			return true;
		}
		
		return false;
	}
	
	public static boolean isVideo(String suffix){
		
		if(suffix == null){
			return false;
		}
		
		suffix = suffix.replace(DOT, "");
		
		if(suffix.equalsIgnoreCase(AVI) || suffix.equalsIgnoreCase(MP4) || suffix.equalsIgnoreCase(MP3) || suffix.equalsIgnoreCase(FLV)){
			return true;
		}
		
		return false;
	}

}
