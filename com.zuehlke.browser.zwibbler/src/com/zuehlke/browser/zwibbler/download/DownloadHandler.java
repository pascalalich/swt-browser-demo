package com.zuehlke.browser.zwibbler.download;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.zuehlke.browser.zwibbler.Activator;
import com.zuehlke.browser.zwibbler.editors.ZwibblerEditor;

public class DownloadHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ZwibblerEditor zwibblerEditor = (ZwibblerEditor) HandlerUtil
				.getActiveEditorChecked(event);

		DownloadJob downloadJob = new DownloadJob(zwibblerEditor);
		downloadJob.schedule();

		return null;
	}

	private class DownloadJob extends Job {

		private ZwibblerEditor zwibblerEditor;

		private BrowserFunction function;

		private String urlString = null;

		public DownloadJob(ZwibblerEditor zwibblerEditor) {
			super("Downloading Zwibbler file");
			this.zwibblerEditor = zwibblerEditor;
			setUser(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Display display = PlatformUI.getWorkbench().getDisplay();
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					function = new BrowserFunction(zwibblerEditor.getBrowser(),
							"readyForDownload") {
						@Override
						public Object function(Object[] arguments) {
							urlString = (String) arguments[0];
							synchronized (DownloadJob.this) {
								DownloadJob.this.notify();
							}
							function.dispose();
							return null;
						}
					};
				}
			});
			try {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						zwibblerEditor.executeJsFile("downloadZwibbler.js",
								"png");
					}
				});
				synchronized (this) {
					wait();
				}
				System.out.println("File ready for download: " + urlString);

				URL url = new URL(urlString);

				IFile file = zwibblerEditor.getFile();
				IFile targetFile = file.getParent().getFile(
						new Path(file.getName() + ".png"));
				if (!targetFile.exists()) {
					targetFile.create(new ByteArrayInputStream(new byte[] {}),
							true, new NullProgressMonitor());
				}
				InputStream in = url.openStream();
				try {
					targetFile.setContents(in, true, // keep saving,
														// even if IFile
														// is out of
														// sync with the
														// Workspace
							false, // dont keep history
							new NullProgressMonitor()); // progress monitor
				} finally {
					in.close();
				}

				return Status.OK_STATUS;
			} catch (Exception e) {
				e.printStackTrace();
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Zwibbler file could not be downloaded: "
								+ e.getMessage());
			}
		}
	}
}
