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
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet downloading the html/javascript code and pre-initializing the
 * session.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class ProvoloneInitializeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String startHtml = null;

    public ProvoloneInitializeServlet() throws Exception {
	startHtml = loadStartHTML();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// TODO be able to set in some sort of property
	String format = "png_img";
	format = (format != null && format.trim().length() > 0) ? format.toLowerCase() : "rle";

	response.setContentType("text/html");
	String ssidStartHtml = startHtml.replaceAll("IMGFORMAT", "\"" + format + "\"");
	response.getWriter().write(ssidStartHtml);
    }

    /**
     * Loads the html code from the classpath.
     * 
     * @return
     * @throws Exception
     */
    protected String loadStartHTML() throws Exception {
	StringBuilder htmlBuilder = new StringBuilder(8192);
	Reader r = new InputStreamReader(getClass().getResourceAsStream("/StreamBase.html"), "UTF-8");
	int read;
	while ((read = r.read()) != -1) {
	    htmlBuilder.append((char) read);
	}
	return htmlBuilder.toString();
    }
}
