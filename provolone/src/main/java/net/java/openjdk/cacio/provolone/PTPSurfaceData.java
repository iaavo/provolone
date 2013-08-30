package net.java.openjdk.cacio.provolone;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.List;

import net.java.openjdk.awt.peer.web.BlitScreenUpdate;
import net.java.openjdk.awt.peer.web.ScreenUpdate;
import net.java.openjdk.awt.peer.web.WebRect;
import net.java.openjdk.awt.peer.web.WebScreen;
import net.java.openjdk.awt.peer.web.WebSurfaceData;
import sun.java2d.loops.SurfaceType;

public class PTPSurfaceData extends WebSurfaceData {

	static SurfaceType typeDefault = SurfaceType.IntRgb
			.deriveSubType("Provolone default");

	protected PTPSurfaceData(WebScreen screen, SurfaceType surfaceType,
			ColorModel cm, Rectangle b, GraphicsConfiguration gc, Object dest) {
		super(screen, surfaceType, cm, b, gc, dest);
	}

	/**
	 * "Evacuate" all pending BlitScreenUpdates.
	 * 
	 * @see BlitScreenUpdate for more information.
	 */
	protected void evacuateBlitScreenUpdates() {
		for (ScreenUpdate update : surfaceUpdateList) {
			if (update instanceof BlitScreenUpdate) {
				((BlitScreenUpdate) update).evacuate();
			}
		}
	}

	/**
	 * Add a list of updates, to the list of pending updates.
	 * 
	 * @param updates
	 */
	protected void addPendingUpdates(List<ScreenUpdate> updates) {
		if (updates != null) {
			surfaceUpdateList.addAll(updates);
		}
	}

	/**
	 * If there are multiple BlitScreenUpdates caused by ScreenUpdates which
	 * require ordering (e.g. CopyAreaScreenUpdate), check if the total
	 * Image-Size of those BlitScreenUpdates is really less than a single one
	 * spanning the whole changed area.
	 * 
	 * Because multiple BlitScreenUpdates have packing overhead, the threshold
	 * is for choosing a single one is at 66% of the size a single
	 * BlitScreenUpdate would take.
	 */
	protected void mergeMultipleScreenUpdates() {
		if (surfaceUpdateList.size() >= 2) {
			WebRect singleUpdateBoundingBox = ScreenUpdate
					.getScreenUpdateBoundingBox(surfaceUpdateList);
			int multiUpdateSize = BlitScreenUpdate
					.getPendingBlitScreenUpdateSize(surfaceUpdateList);
			int singleUpdateSize = singleUpdateBoundingBox.getWidth()
					* singleUpdateBoundingBox.getHeight();

			if (multiUpdateSize * 1.5 >= singleUpdateSize) {
				surfaceUpdateList.clear();
				surfaceUpdateList.add(new BlitScreenUpdate(
						singleUpdateBoundingBox.getX1(),
						singleUpdateBoundingBox.getY1(),
						singleUpdateBoundingBox.getX1(),
						singleUpdateBoundingBox.getY1(),
						singleUpdateBoundingBox.getWidth(),
						singleUpdateBoundingBox.getHeight(), imgBuffer));
			}
		}
	}

	/**
	 * @return A list with all ScreenUpdates, or null iff there are none.
	 */
	public List<ScreenUpdate> fetchPendingSurfaceUpdates() {
		addPendingUpdates(damageTracker.groupDamagedAreas(imgBuffer));

		if (surfaceUpdateList.size() > 0) {
			mergeMultipleScreenUpdates();

			List<ScreenUpdate> pendingUpdateList = surfaceUpdateList;
			// FIXME Right now it stores all updates from the very beginning of
			// the app.
			// it should empty the surfaceUpdateList and store keyframes! And
			// the update list
			// should be generated seperately for each sub session!
			/** surfaceUpdateList = new ArrayList<ScreenUpdate>(); */
			return pendingUpdateList;
		}

		return null;
	}

}
