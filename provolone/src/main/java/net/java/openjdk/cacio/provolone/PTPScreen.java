package net.java.openjdk.cacio.provolone;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import net.java.openjdk.awt.peer.web.WebGraphicsConfiguration;
import net.java.openjdk.awt.peer.web.WebScreen;
import net.java.openjdk.cacio.servlet.transport.BinaryPngTransport;
import net.java.openjdk.cacio.servlet.transport.PNGTransport;
import net.java.openjdk.cacio.servlet.transport.Transport;
import sun.awt.peer.cacio.managed.FullScreenWindowFactory;

public class PTPScreen extends WebScreen {

    private static PTPScreen instance;

    public static PTPScreen getInstance() {
        if (instance == null) {
            instance = new PTPScreen();
        }
        return instance;
    }

    private PTPScreen() {
    	super();
    }
    
    public PTPScreen(WebGraphicsConfiguration webGraphicsConfiguration) {
		super(webGraphicsConfiguration);
	}

	@Override
    public WebGraphicsConfiguration getConfig() {
    	return (WebGraphicsConfiguration) getGraphicsConfiguration();
    }

	public GraphicsConfiguration getGraphicsConfiguration() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	public Rectangle getBounds() {
		Dimension d = FullScreenWindowFactory.getScreenDimension();
		return new Rectangle(0, 0, d.width, d.height);
	}
	
	@Override
	public void resizeScreen(int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Transport getEncoder() {
		return Transport.getTransportForName(Transport.FORMAT_PNG_IMG, 3);
	}
}
