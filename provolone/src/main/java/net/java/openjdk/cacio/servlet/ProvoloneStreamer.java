/*
 * Copyright (c) 2011, Clemens Eisserer, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package net.java.openjdk.cacio.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.java.openjdk.cacio.provolone.PTPScreen;
import net.java.openjdk.cacio.servlet.transport.Transport;

/**
 * Servlet for fetching ScreenUpdates if available.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class ProvoloneStreamer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// FIXME: subSessionID parameter is intepreted as the available format 
		// on the clients browser. See also the according note in StreamBase.html
		String format = request.getParameter("subSessionID");
		format = (format != null && format.trim().length() > 0) ? format
				.toLowerCase() : "png";

		disableCaching(response);

		Transport transport = PTPScreen.getInstance().pollForScreenUpdates(
				15000, format);
		response.setContentType(transport.getContentType());
		transport.writeToStream(response.getOutputStream());
	}

	/**
	 * Sets various headers to avoid browsers caching data.
	 * 
	 * @param response
	 */
	protected void disableCaching(HttpServletResponse response) {
		response.setHeader("Expires", "Sat, 1 May 2000 12:00:00 GMT");
		response.setHeader("Cache-Control",
				"no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
	}
}
