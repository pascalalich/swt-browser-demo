package com.zuehlke.browser.zwibbler.download;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.zuehlke.browser.zwibbler.Activator;
import com.zuehlke.browser.zwibbler.editors.ZwibblerEditor;

class ZwibblerDownloadJob extends Job {

	private ZwibblerEditor zwibblerEditor;

	private String urlString = null;

	public ZwibblerDownloadJob(ZwibblerEditor zwibblerEditor) {
		super("Downloading Zwibbler file");
		this.zwibblerEditor = zwibblerEditor;
		setUser(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			initDownloadFunction();
			startCreationAndWait();
			downloadAndSave();

			return Status.OK_STATUS;
		} catch (Exception e) {
			e.printStackTrace();
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Zwibbler file could not be downloaded: " + e.getMessage());
		}
	}

	private void initDownloadFunction() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {
			private BrowserFunction function;

			@Override
			public void run() {
				function = new BrowserFunction(zwibblerEditor.getBrowser(),
						"readyForDownload") {
					@Override
					public Object function(Object[] arguments) {
						urlString = (String) arguments[0];
						synchronized (ZwibblerDownloadJob.this) {
							ZwibblerDownloadJob.this.notify();
						}
						function.dispose();
						return null;
					}
				};
			}
		});
	}

	private void startCreationAndWait() throws InterruptedException {
		Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				zwibblerEditor.executeJsFile("downloadZwibbler.js", "png");
			}
		});
		synchronized (this) {
			wait();
		}
	}

	private void downloadAndSave() throws CoreException, IOException {
		System.out.println("About to download file: " + urlString);

		URL url = new URL(urlString);

		IFile targetFile = determineTargetFile();
		touchTargetFile(targetFile);

		InputStream in = url.openStream();
		save(in, targetFile);
	}

	/**
	 * @return the target file handle for the download
	 */
	private IFile determineTargetFile() {
		IFile zwibblerFile = zwibblerEditor.getFile();
		IFile targetFile = zwibblerFile.getParent().getFile(
				new Path(zwibblerFile.getName() + ".png"));
		return targetFile;
	}

	/**
	 * Creates the target file if not yet existing
	 * 
	 * @param targetFile
	 * @throws CoreException
	 */
	private void touchTargetFile(IFile targetFile) throws CoreException {
		if (!targetFile.exists()) {
			targetFile.create(new ByteArrayInputStream(new byte[] {}), true,
					new NullProgressMonitor());
		}
	}

	/**
	 * Saves the content of the {@link InputStream} to the target file,
	 * overwriting existing content if existent.
	 * 
	 * @param in
	 * @param targetFile
	 * @throws CoreException
	 * @throws IOException
	 */
	private void save(InputStream in, IFile targetFile) throws IOException,
			CoreException {
		try {
			// save, even if file is out of sync
			boolean force = true;
			boolean keepHistory = false;
			targetFile.setContents(in, force, keepHistory,
					new NullProgressMonitor()); // progress monitor
		} finally {
			in.close();
		}
	}
}
