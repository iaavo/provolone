package net.java.openjdk.cacio.provolone;

import java.awt.GraphicsDevice;

import net.java.openjdk.awt.peer.web.WebGraphicsEnvironment;

public class PTPGraphicsEnvironment extends WebGraphicsEnvironment {
    @Override
    protected GraphicsDevice makeScreenDevice(int screennum) {
        return new PTPGraphicsDevice();
    }
}
