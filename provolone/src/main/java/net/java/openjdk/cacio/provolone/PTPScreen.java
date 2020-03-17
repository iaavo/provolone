package net.java.openjdk.cacio.provolone;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.java.openjdk.awt.peer.web.ScreenUpdate;
import net.java.openjdk.awt.peer.web.TreeImagePacker;
import net.java.openjdk.awt.peer.web.WebGraphicsConfiguration;
import net.java.openjdk.awt.peer.web.WebScreen;
import net.java.openjdk.awt.peer.web.WebSurfaceData;
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
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration();
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
		return new PNGTransport();
	}

	public WebSurfaceData getSurfaceData() {
		if (surfaceData == null) {
			surfaceData = new PTPSurfaceData(this, PTPSurfaceData.typeDefault,
					getColorModel(), getBounds(), getGraphicsConfiguration(),
					this);
		}
		return surfaceData;
	}

	/**
	 * Default no operation implementation. Might be implemented in the future.
	 * 
	 * @see net.java.openjdk.awt.peer.web.WebScreen#pollForScreenUpdates(int)
	 */
	public Transport pollForScreenUpdates(int timeout) throws IOException {
		throw new UnsupportedOperationException(
				"Call pollForScreenUpdates(int, String) instead.");
	}

	/**
	 * Polls the WebScreen for pending updates. - Returns immediatly if pending
	 * updates are available - Waits up to timeout seconds, of no updates are
	 * available.
	 * 
	 * @param response
	 *            - the HttpServletResponse the updates will be written to
	 * @param timeout
	 * @throws IOException
	 */
	public Transport pollForScreenUpdates(int timeout, String format)
			throws IOException {

		Transport updatesWritten = null;
		try {
			lockScreen();
			updatesWritten = prepareScreenUpdates(format);

			if (updatesWritten == null) {
				try {
					boolean signalled = screenCondition.await(timeout,
							TimeUnit.MILLISECONDS);

					/*
					 * If we had to wait, we quite likely have been waked by the
					 * first operation. Usually (e.g. Swing) many draw-commands
					 * are executed closely together, so we wait just a little
					 * longer, so we can send a larger batch down. In order to
					 * allow the rendering thread to do its job, we have to
					 * unlock the screen however.
					 */
					if (signalled) {
						unlockScreen();
						Thread.sleep(10);
						lockScreen();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				updatesWritten = prepareScreenUpdates(format);
			}
		} finally {
			unlockScreen();
		}

		return updatesWritten;
	}

	@Override
	protected boolean prepareScreenUpdates() {
		throw new UnsupportedOperationException(
				"Call prepareScreenUpdates(String) instead.");
	}

	/**
	 * If ScreenUpdates are pending, preserve/encode pending them, so the
	 * ScreenLock can be released and rendering can continue.
	 * 
	 * @param os
	 *            the OutputStream the pending updates are written to
	 * @return true if updates have been written, false if no updates were
	 *         pending.
	 * @throws IOException
	 */
	protected Transport prepareScreenUpdates(String format) {
		if (surfaceData == null) {
			return null;
		}

		try {
			lockScreen();

			Transport encoder = null;
			if (format == null) {
				encoder = new PNGTransport();
			} else {
				encoder = Transport.getTransportForName(format, 3);
			}

			List<ScreenUpdate> screenUpdates = surfaceData
					.fetchPendingSurfaceUpdates();
			if (screenUpdates != null) {
				pendingUpdateList.addAll(screenUpdates);
			}

			if (pendingUpdateList.size() > 0) {
				ArrayList<Integer> cmdList = new ArrayList<Integer>(
						pendingUpdateList.size() * 7);

				// Refactor
				TreeImagePacker packer = new TreeImagePacker();
				packer.insertScreenUpdateList(pendingUpdateList);
				for (ScreenUpdate update : pendingUpdateList) {
					// System.out.println(update);
					update.writeToCmdStream(cmdList);
				}

				// Write updates to us
				encoder.prepareUpdate(pendingUpdateList, packer, cmdList);
				pendingUpdateList.clear();

				return encoder;
			}
		} finally {
			unlockScreen();
		}

		return null;
	}

}
