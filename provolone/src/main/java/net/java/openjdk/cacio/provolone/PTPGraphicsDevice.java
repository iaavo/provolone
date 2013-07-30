
package net.java.openjdk.cacio.provolone;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

class PTPGraphicsDevice extends GraphicsDevice {

    private PTPGraphicsConfiguration defaultConfig;
	
	@Override
	public int getType() {
		return GraphicsDevice.TYPE_RASTER_SCREEN;
	}

	@Override
	public String getIDstring() {
		return "Provolone Device";
	}

	@Override
	public GraphicsConfiguration[] getConfigurations() {
		return new GraphicsConfiguration[] { getDefaultConfiguration() };
	}

	@Override
	public synchronized GraphicsConfiguration getDefaultConfiguration() {
        if (defaultConfig == null) {
            defaultConfig = new PTPGraphicsConfiguration(this);
        }
        return defaultConfig;
	}

}
