package net.java.openjdk.cacio.provolone;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

import sun.awt.peer.cacio.managed.FullScreenWindowFactory;

public class PTPGraphicsConfiguration extends GraphicsConfiguration {

	private PTPGraphicsDevice device;

	public PTPGraphicsConfiguration(PTPGraphicsDevice device) {
		this.device = device;
	}

	@Override
	public GraphicsDevice getDevice() {
		return this.device;
	}

	@Override
	public ColorModel getColorModel() {
		return new DirectColorModel(24, 0x00FF0000, 0x0000FF00, 0x000000FF);
	}

	@Override
	public ColorModel getColorModel(int transparency) {
		if (transparency == Transparency.OPAQUE) {
			return getColorModel();
		} else {
			return ColorModel.getRGBdefault();
		}
	}

	@Override
	public AffineTransform getDefaultTransform() {
		return new AffineTransform();
	}

	@Override
	public AffineTransform getNormalizingTransform() {
		return new AffineTransform();
	}

	@Override
	public Rectangle getBounds() {
        Dimension d = FullScreenWindowFactory.getScreenDimension();
        return new Rectangle(0, 0, d.width, d.height);
	}
}
