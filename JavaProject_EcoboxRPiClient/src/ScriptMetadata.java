/***
 * 
 * 
 * 
 * 
 * 
 *******************************************************************************************************************************************
 *                                                                                                                                         *
 *     /\    DISCLAIMER     UGLY, UN-OPTIMIZED, "ALPHA-PROTOTYPING" CODE                                                                   *
 *    /  \   DISCLAIMER     DO NOT READ FURTHER UNTIL YOU HAVE FOUND A CURE FOR EYE CANCER                                                 *
 *   / !! \  DISCLAIMER     #KAPPA                                                                                                         *
 *  /______\ DISCLAIMER     Seriously though. Don't judge, this was written in a rush and will be improved, revised, and refactored soon.  *
 *                                                                                                                                         *
 *******************************************************************************************************************************************
 *
 *
 *
 *
 * (I'll only warn you once)
 ***/


import java.awt.image.BufferedImage;
import java.util.Random;

import com.github.sarxos.webcam.Webcam;


public abstract class ScriptMetadata {

	public final String NAME;	
	public boolean currentlyStreaming = false;
	public final long TCP_INTERVAL;
	
	long time = 0;
	long timesSent = 0;
	
	public boolean update(long runningSince){
		//System.out.println(l + " " + time);
		//time += l;
		if(runningSince/TCP_INTERVAL == timesSent){
		//if(time >= TCP_INTERVAL){
			System.out.println("Sending.");
			timesSent++;
			//time = 0;
			return true;
		}
		return false;
	}
	
	//public abstract void registerNeededPackets() aka setup()
	//public abstract void nextData
	//-> no need for openstream/closestream method. Same data is used for UDP and TCP, just the rate at which it is sent changes.
	//-> edit: have one retrieveData method which takes delta as arg, and one simple retrieveData method for streaming. the first one just is a timeblock.
	//-> edit: update(delta) will give true if tcp packet should be sent. retrieveData is then called (Or when streaming)
	
	
	
	/**
	 * THIS IS AN EXAMPLE DONT USE THIS IN FINAL VERSION
	 */
	public abstract Object retrieveData();
	
	public ScriptMetadata(String name, long interval){
		this.NAME = name;
		this.TCP_INTERVAL = interval;
	}
	
	public static class ScriptMetadataTest1 extends ScriptMetadata {
		private int i = 0;
		private String[] text = {"This", "Is", "Some", "Message", "Delivered", "Over", "Time"};
		
		public ScriptMetadataTest1(){
			super("TimeMsg", 3500);
		}
		
		@Override
		public Object retrieveData(){
			String s = text[i++];
			if(i >= text.length) i = 0;
			return s;
		}
	}
	
	public static class ScriptMetadataTest2 extends ScriptMetadata {
		public ScriptMetadataTest2(){
			super("Random", 10000);
		}
		public Object retrieveData(){
			return new Random().nextInt(123);
		}
	}
	//TODO: Add priorities for each task. important tasks should get executed first.
	public static class SMDT3 extends ScriptMetadata{
		public SMDT3(){
			super("Webcam", 1000);
			webcam = Webcam.getDefault();
			webcam.open();
		}
		Webcam webcam;
		public Object retrieveData(){
			BufferedImage img = webcam.getImage();

			
			//int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
			System.out.println(img.getWidth() + ", " + img.getHeight());
			System.out.println(img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()).length);
			return new int[][]{new int[]{img.getWidth(), img.getHeight()}, img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth())};
		}
		
	}
	
}
