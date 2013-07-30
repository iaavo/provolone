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

//	private FileWriter fw;

	public static final int PORT = 3141;

	public static final double FPS = 2;

	public CacioMonitorServerBurster(int port, int j) {
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				sendImages();
			}
		}, "Server");
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
//				fw = new FileWriter(new File("test.log"));
//				OutputStream os2 = new OutputStream() {
//					int i = 0;
//					int ch[] = new int[4];
//					
//					@Override
//					public void write(int b) throws IOException {
//						ch[i] = b;
//						i++;
//						if(i==4) {
//							i = 0;
//							fw.write(((ch[0] << 24) + (ch[1] << 16) + (ch[2] << 8) + (ch[3] << 0)) + " ");
//						}
//					}
//					@Override
//					public void flush() throws IOException {
//						super.flush();
//						fw.write('\n');
//					}
//				};
				
				
				while (socket != null && !socket.isClosed()
						&& socket.isConnected()) {
					try {
						OutputStream os = socket.getOutputStream();
						InputStream is = socket.getInputStream();
						
						Transport encoder = PTPScreen.getInstance().pollForScreenUpdates(15000);
						
						encoder.writeToStream(os);
//						encoder.writeToStream(os2);
						os.flush();
//						os2.flush();
						
						// Wait for next image
						while(is.read() != 1) {
							
						}
					} catch (SocketException e) {
//						e.printStackTrace();
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
//			} catch (IOException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
			} finally {
//				if(fw!=null)
//					try {
//						fw.close();
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
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
}
