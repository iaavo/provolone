package net.java.openjdk.cacio.monitor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.imageio.ImageIO;

public class CacioMonitorServer {

	private final BufferedImage image;

	private final Thread thread;

	public static final int PORT = 3141;

	public static final int FPS = 30;

	public CacioMonitorServer(BufferedImage image) {
		this.image = image;
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				sendImages();
			}
		});
		this.thread.start();
	}

	protected void sendImages() {
		while (true) {
			Socket socket = null;
			try {
				// Connect
				while (socket == null) {
					ServerSocket serverSocket = null;
					try {
						serverSocket = new ServerSocket(PORT);
						socket = serverSocket.accept();
					} catch (Exception e1) {
					} finally {
						if (serverSocket != null) {
							try {
								serverSocket.close();
							} catch (IOException e) {
							}
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				// Send Images
				while (socket != null && !socket.isClosed()
						&& socket.isConnected()) {
					try {
						OutputStream os = socket.getOutputStream();
						// Send number of images
						os.write(intToByteArray(1));
						{
							// Send x position of image
							os.write(intToByteArray(0));
							// Send y position of image
							os.write(intToByteArray(0));
							// Send size of the image
							ByteArrayOutputStream bScrn = new ByteArrayOutputStream();
							ImageIO.write(this.image, "PNG", bScrn);
							byte data[] = bScrn.toByteArray();
							bScrn.close();
							int size = data.length;
							os.write(intToByteArray(size));
							// Send Image data
							os.write(data);
						}
						os.flush();
						// Wait for next image
						Thread.sleep(1000 / FPS);
					} catch (SocketException e) {
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} catch (Exception e) {
					}
				}
			} finally {
				try {
					if (socket != null) {
						socket.close();
						socket = null;
					}
				} catch (IOException e) {
				}
			}
		}
	}

	private byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}
}
