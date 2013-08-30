package net.java.openjdk.cacio.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import net.java.openjdk.cacio.provolone.PTPScreen;
import net.java.openjdk.cacio.servlet.transport.Transport;

public class CacioMonitorServerBurster {

	private final Thread thread;

	public static final int PORT = 3141;

	public static final double FPS = 2;

	public CacioMonitorServerBurster(int port, int j) {
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				acceptConnection();
			}
		}, "Server");
		this.thread.start();
	}

	protected void acceptConnection() {
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			while (true) {
				try {
					final Socket socket = serverSocket.accept();
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							sendImages(socket);
						}
					});
					thread.start();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	protected void sendImages(Socket socket) {
		try {
			while (socket != null && !socket.isClosed() && socket.isConnected()) {
				try {
					OutputStream os = socket.getOutputStream();
					InputStream is = socket.getInputStream();

					Transport encoder = PTPScreen.getInstance()
							.pollForScreenUpdates(15000, null);

					encoder.writeToStream(os);
					os.flush();

					// Wait for next image
					while (is.read() != 1) {
					}
				} catch (SocketException e) {
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			try {
				if (socket != null) {
					socket.close();
					socket = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
