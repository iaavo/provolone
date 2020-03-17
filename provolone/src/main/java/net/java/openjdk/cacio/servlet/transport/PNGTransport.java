package net.java.openjdk.cacio.servlet.transport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import net.java.openjdk.awt.peer.web.ScreenUpdate;
import net.java.openjdk.awt.peer.web.TreeImagePacker;
import net.java.openjdk.awt.peer.web.WebRect;
import net.java.openjdk.cacio.servlet.transport.Transport;

/**
 * Uses <code>ImageIO</code> to write png images to an array of bytes and stores
 * each data entry within a byte array using up the least possible amount of
 * space.
 */
public class PNGTransport extends Transport {
	private final byte[] emptyResponseData = intToByteArray(-1);

	private List<Integer> cmdList;
	private BufferedImage packedImage;

	public PNGTransport() {
		super("image/png");
	}

	@Override
	public void prepareUpdate(List<ScreenUpdate> pendingUpdateList,
			TreeImagePacker packer, List<Integer> cmdList) {
		dataAvailable = true;
		this.cmdList = cmdList;

		WebRect packedRegionBox = packer.getBoundingBox();
		if (packedRegionBox.getWidth() > 0 && packedRegionBox.getHeight() > 0) {
			packedImage = new BufferedImage(packedRegionBox.getWidth(),
					packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
			copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
		}
	}

	@Override
	public void writeToStream(OutputStream os) throws IOException {
		if (dataAvailable) {
			// send coordinates
			for (int i = 0; i < cmdList.size(); i++) {
				os.write(intToByteArray(cmdList.get(i)));
			}
			// get image binary data
			if (packedImage != null) {
				ByteArrayOutputStream bScrn = new ByteArrayOutputStream();
				ImageIO.write(this.packedImage, "PNG", bScrn);
				byte data[] = bScrn.toByteArray();
				bScrn.close();
				// send image size
				int size = data.length;
				os.write(intToByteArray(size));
				// Send Image data
				os.write(data);
			}
		} else {
			os.write(emptyResponseData);
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

	@Override
	public String asString() {
		String string = "";
		for (int cmdInt : cmdList) {
			string += cmdInt + " ";
		}
		return string;
	}
}
