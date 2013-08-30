package net.java.openjdk.cacio.monitor;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.java.openjdk.awt.peer.web.BlitScreenUpdate;
import net.java.openjdk.awt.peer.web.WebRect;

public class CacioMonitorClient {

	public static void main(String[] args) {
		new CacioMonitorClient();
	}

	private JLabel panel;

	private Thread thread;

	private JFrame frame;

	private String status;

	private BufferedImage completeImage = new BufferedImage(1024, 768,
			BufferedImage.TYPE_INT_RGB);

	private OutputStream os;

	public CacioMonitorClient() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CacioMonitorClient.this.frame = new JFrame();
				CacioMonitorClient.this.frame
						.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				setupGui(CacioMonitorClient.this.frame);
				CacioMonitorClient.this.frame.pack();
				CacioMonitorClient.this.frame.setSize(1024, 768);
				CacioMonitorClient.this.frame.setLocationRelativeTo(null);
				CacioMonitorClient.this.frame.setVisible(true);
				CacioMonitorClient.this.thread = new Thread(new Runnable() {
					@Override
					public void run() {
						receiveImages();
					}
				});
				CacioMonitorClient.this.thread.start();
			}
		});
	}

	private void setStatus(String status) {
		this.status = status;
		this.frame.setTitle("Provolone-Test-Monitor - " + this.status);
	}

	private String appendStatus(String status) {
		this.status += status;
		this.frame.setTitle("Provolone-Test-Monitor - " + this.status);
		return this.status;
	}

	private void setupGui(JFrame frame) {
		frame.setLayout(new BorderLayout());
		this.panel = new JLabel();
		this.panel.setHorizontalAlignment(SwingConstants.CENTER);
		this.panel.setVerticalAlignment(SwingConstants.CENTER);
		panel.setIcon(new ImageIcon(completeImage));
		frame.add(this.panel, BorderLayout.CENTER);
	}

	private void receiveImages() {
		while (true) {
			Socket socket = null;
			try {
				// Connect
				while (socket == null) {
					try {
						// this.panel.setIcon(null);
						setStatus("Trying to connect...");
						socket = new Socket("127.0.0.1",
								CacioMonitorServerBurster.PORT);
						appendStatus("Success");
					} catch (Exception e1) {
						appendStatus("Fail");
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// Receive Images
				while (socket != null && !socket.isClosed()
						&& socket.isConnected()) {
					try {
						setStatus("Receive Image...");
						InputStream is = socket.getInputStream();
						os = socket.getOutputStream();
						while (true) {
							// Receive the type of update
							int type = readInt(is);
//							System.out.println("Type: " + type);
							if (type == 0) {
								List<BlitScreenUpdate> commands = new ArrayList<BlitScreenUpdate>();
								int x1 = readInt(is);
								int y1 = readInt(is);
								int x2 = readInt(is);
								int y2 = readInt(is);
								int packedX = readInt(is);
								int packedY = readInt(is);
								int size;
								commands.add(new BlitScreenUpdate(x1, y1,
										packedX, packedY, Math.abs(x2 - x1),
										Math.abs(y2 - y1), null));
								while ((size = readInt(is)) == 0) {
									x1 = readInt(is);
									y1 = readInt(is);
									x2 = readInt(is);
									y2 = readInt(is);
									packedX = readInt(is);
									packedY = readInt(is);
									commands.add(new BlitScreenUpdate(x1, y1,
											packedX, packedY,
											Math.abs(x2 - x1), Math
													.abs(y2 - y1), null));
								}
								if (size < 0) {
									throw new IOException(
											"Insufficient data in stream");
								}
								// Receive Image data
								byte[] data = new byte[size];
								int index = 0;
								while (index < size) {
									int bytesRead = is.read(data, index, size
											- index);
									if (bytesRead < 0) {
										throw new IOException(
												"Insufficient data in stream");
									}
									index += bytesRead;
								}

								appendStatus(" received");
								// Set the image
								BufferedImage image = ImageIO
										.read(new ByteArrayInputStream(data));

								for (BlitScreenUpdate u : commands) {
									WebRect r = u.getUpdateArea();
									completeImage.getGraphics().drawImage(
											image, r.getX1(), r.getY1(),
											r.getX2(), r.getY2(), u.getSrcX(),
											u.getSrcY(),
											u.getSrcX() + r.getWidth(),
											u.getSrcY() + r.getHeight(), null);

								}
							}

							// send OK
							os.write(1);
							os.flush();

							this.panel.repaint();
							appendStatus(" and painted.");
						}
					} catch (SocketException e) {
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} catch (IOException e) {
						appendStatus(" failed. (" + e.getMessage() + ")");
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					// Wait for next image
					try {
						Thread.sleep((long) (1000 / CacioMonitorServerBurster.FPS));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					setStatus("Disconnected");
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

	private int readInt(InputStream in) throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}
}