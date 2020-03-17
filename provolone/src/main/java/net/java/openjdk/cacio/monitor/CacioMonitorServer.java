package net.java.openjdk.cacio.monitor;

import net.java.openjdk.cacio.provolone.PTPGraphicsEnvironment;
import net.java.openjdk.cacio.provolone.PTPToolkit;
import net.java.openjdk.cacio.servlet.ProvoloneInitializeServlet;
import net.java.openjdk.cacio.servlet.ProvoloneStreamer;
import net.java.openjdk.cacio.servlet.ResourceLoaderServlet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class CacioMonitorServer {

	public CacioMonitorServer() throws Exception {
		this(8080, 125);
	}

	/**
	 * Initializes buitin http-server on the specified port, and sets system
	 * properties.
	 * 
	 * @param port
	 * @throws Exception
	 */
	public CacioMonitorServer(int port, int maxThreads) throws Exception {
//		applySystemProperties();

		Server server = new Server(port);
		server.setThreadPool(new QueuedThreadPool(maxThreads));

		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setResourceBase("bin/");
		context.getSessionHandler().getSessionManager()
				.setMaxInactiveInterval(90);
		// TODO NEEDED?
//		context.getSessionHandler()
//				.addEventListener(new CacioSessionListener());

		ResourceHandler handler = new ResourceHandler();
		handler.setResourceBase("bin");
		handler.setServer(server);

		// context.addServlet(new ServletHolder(new ImgBenchServlet()),
		// "/ImageStreamer");

		context.addServlet(new ServletHolder(new ProvoloneInitializeServlet()),
				"/SessionInitializer");
		context.addServlet(new ServletHolder(new ProvoloneStreamer()),
				"/ImageStreamer");
		context.addServlet(new ServletHolder(new ResourceLoaderServlet()),
				"/ResourceLoader");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { handler, context });
		server.setHandler(handlers);

		server.start();
		server.join();
	}

	/**
	 * Set the appropriate system properties, so the user doesn't have to
	 * specify all the caciocavallo-web classnames by hand.
	 */
	protected void applySystemProperties() {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("awt.toolkit", PTPToolkit.class.getName());
		System.setProperty("java.awt.graphicsenv", PTPGraphicsEnvironment.class.getName());
	}

}
