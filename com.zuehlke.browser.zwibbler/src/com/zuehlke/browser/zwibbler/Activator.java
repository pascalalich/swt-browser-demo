package com.zuehlke.browser.zwibbler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.zuehlke.browser.zwibbler"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		extractHtmlFiles("/html");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Copies all files from plugin's folder "/html" to "<state-location>/html".
	 * 
	 * @throws IOException
	 * @see {@link #getHtmlFileURL(String)}
	 */
	private void extractHtmlFiles(String dir) throws IOException {
		Bundle bundle = getBundle();
		File stateLocation = getStateLocation().toFile();

		Enumeration<?> paths = bundle.getEntryPaths(dir);
		while (paths.hasMoreElements()) {
			String path = (String) paths.nextElement();
			File toFile = new File(stateLocation, path);
			if (path.endsWith("/")) {
				System.out.println("Creating dir: " + path);
				toFile.mkdirs();
				extractHtmlFiles(dir + "/" + toFile.getName());
			} else {
				System.out.println("Copying file: " + path);
				toFile.getParentFile().mkdirs();
				if (!toFile.exists()) {
					copy(bundle.getEntry(path), toFile);
				}
			}
		}
	}

	private void copy(URL fromURL, File toFile) throws IOException {
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = fromURL.openStream();
			out = new BufferedOutputStream(new FileOutputStream(toFile));
			byte[] buffer = new byte[2048];
			for (int read = 0; (read = in.read(buffer)) > 0;) {
				out.write(buffer, 0, read);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} finally {
					if (out != null) {
						out.close();
					}
				}
			}
		}
	}

	/**
	 * Returns a "file:" URL to files within the "/html" folder, even if this
	 * plugin is deployed as a JAR.
	 * 
	 * @param path
	 * @return the URL or <tt>null</tt> if the path does not exist
	 */
	public URL getHtmlFileURL(String path) {
		File stateLocation = getStateLocation().toFile();
		try {
			return new File(stateLocation, "html/" + path).toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param fileName
	 * @return
	 */
	public String getJavaScript(String fileName) {
		URL url = getHtmlFileURL("js/" + fileName);
		if (url == null) {
			throw new IllegalArgumentException("File not found: " + fileName);
		}
		try {
			StringBuilder b = new StringBuilder();
			InputStream in = url.openStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				b.append(line).append("\n");
			}
			return b.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
