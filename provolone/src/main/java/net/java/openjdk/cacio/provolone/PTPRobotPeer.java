package net.java.openjdk.cacio.provolone;

import java.awt.Rectangle;

import net.java.openjdk.cacio.ctc.CTCRobotPeer;
import sun.awt.peer.cacio.managed.PlatformScreen;

public class PTPRobotPeer extends CTCRobotPeer {

	@Override
	protected PlatformScreen getPlatformScreen() {
		return PTPScreen.getInstance();
	}
	
	@Override
	public int getRGBPixel(int x, int y) {
		return PTPScreen.getInstance().getSurfaceData().imgBuffer.getRGB(x, y);
	}

	@Override
	public int[] getRGBPixels(Rectangle bounds) {
		return PTPScreen.getInstance().getSurfaceData().imgBuffer.getRGB(
				bounds.x, bounds.y, bounds.width, bounds.height, null, 0,
				bounds.width);
	}

}
