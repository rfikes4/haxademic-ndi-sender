
package com.ndisender;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.nio.ByteBuffer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.walker.devolay.Devolay;
import com.walker.devolay.DevolayFrameFourCCType;
import com.walker.devolay.DevolaySender;
import com.walker.devolay.DevolayVideoFrame;

import processing.core.PGraphics;
import processing.core.PImage;

public class NDISender
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
	protected DevolaySender sender;
	protected String senderName = "NDI Processing";
	protected DevolayVideoFrame videoFrame;
	
	protected PImage pimg;
	protected BufferedImage tempBuffered;
	protected ByteBuffer byteBuffer;
	protected DataBuffer dataBuffer;
	
	protected PGraphics pgVid;
	
	protected void overridePropsFile() {
	p.appConfig.setProperty( AppSettings.WIDTH, 600 );
	p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
	p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
	p.appConfig.setProperty( AppSettings.APP_NAME, "Basic" );
	p.appConfig.setProperty( AppSettings.APP_ICON, "images/app-icon.png" );
	p.appConfig.setProperty( AppSettings.SHOW_DEBUG, false);
	}
	
	public void setupFirstFrame() {
		p.background(0);
		
		pgVid = PG.newPG(p.width, p.height, false, false);
		
		// Setup NDI
		Devolay.loadLibraries();
		// Create the sender using the default settings, other than setting a name for the source.
		sender = new DevolaySender(senderName);
		// Create a video frame
		videoFrame = new DevolayVideoFrame();
		videoFrame.setResolution(p.width, p.height);
		videoFrame.setFourCCType(DevolayFrameFourCCType.NDIlib_FourCC_type_BGRX);
		videoFrame.setFrameRate((int)p.frameRate, 1);
	}
	
	public void drawApp() {
		p.background(0);
		
		// Draw into PGraphics
		pgVid.beginDraw();
		pgVid.background(P.sin(p.frameCount * 0.03f) * 127/2 + 127/2);
		pgVid.endDraw();
		
		// Convert PGraphics to PImage
		pimg = pgVid.get();
		
		// Convert PImage to ByteBuffer
		tempBuffered = ImageUtil.pImageToBuffered(pimg);
		byteBuffer = BufferedImageToByteBuffer(tempBuffered);
		
		// Send BytesBuffer to NDI
		videoFrame.setData(byteBuffer);
		sender.sendVideoFrame(videoFrame);
		
		p.image(pimg, 0, 0);
	}
	
	public ByteBuffer BufferedImageToByteBuffer(BufferedImage bi) {
		int[] pixels = new int[bi.getWidth() * bi.getHeight()];
		bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), pixels, 0, bi.getWidth());
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(bi.getWidth() * bi.getHeight() * 4);
		
		for (int y = 0; y < bi.getHeight(); y++) {
			for (int x = 0; x < bi.getWidth(); x++) {
				int pixel = pixels[y * bi.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); 	// Red
				buffer.put((byte) ((pixel >> 8) & 0xFF)); 	// Green
				buffer.put((byte) (pixel & 0xFF)); 			// Blue
				buffer.put((byte) 0); 						// Alpha or terminating byte?
			}
		}
		
		buffer.flip();
		return buffer;
	}
	
	// Terminate NDI sending, unnecessary but faster than Java's garbage collector
	public void closeNDI() {
		videoFrame.close();
		sender.close();
	}

}
