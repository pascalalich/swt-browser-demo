package com.zuehlke.browser.zwibbler.editors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.zuehlke.browser.zwibbler.Activator;

public class ZwibblerEditor extends EditorPart {

	/**
	 * The String representation of an empty Zwibbler drawing.
	 */
	private static final String EMPTY_ZWIBBLER = "zwibbler3.[]";

	private Browser browser;

	private boolean dirty;

	@Override
	public void doSave(IProgressMonitor monitor) {
		executeJsFile("saveZwibbler.js");
	}

	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

		if (!(input instanceof IFileEditorInput)) {
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		}
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.WEBKIT);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// react to resize actions
		browser.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				updateZwibblerLayout();
			}
		});

		// initialize as dirty as there is no way to get informed about changes
		reportDirtyState(true);

		updatePartName();

		initZwibbler();
	}

	private void reportDirtyState(boolean dirty) {
		if (dirty != this.dirty) {
			this.dirty = dirty;
			firePropertyChange(PROP_DIRTY);
		}
	}

	private void updatePartName() {
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		String partName = "New Zwibbler";
		if (input.getFile() != null) {
			partName = input.getName();
		}
		setPartName(partName);
		firePropertyChange(PROP_TITLE);
	}

	private void initZwibbler() {
		String url = Activator.getDefault().getHtmlFileURL("zwibbler.html")
				.toExternalForm();
		browser.addProgressListener(new ProgressAdapter() {
			@Override
			public void completed(ProgressEvent event) {
				initLoadFunction();
				initSaveFunction();

				updateZwibblerLayout();
				executeJsFile("loadZwibbler.js");
			}
		});

		// if we do not clear the sessions,
		// the canvas initializes with the latest drawing
		Browser.clearSessions();

		System.out.println("Setting URL to browser: " + url);
		browser.setUrl(url);
	}

	private void updateZwibblerLayout() {
		executeJsFile("initZwibbler.js", browser.getSize().x,
				browser.getSize().y);
	}

	private void initLoadFunction() {
		new BrowserFunction(browser, "loadZwibbler") {
			@Override
			public Object function(Object[] arguments) {
				return loadZwibbler();
			}
		};
		// TODO find a place to dispose the function
	}

	private String loadZwibbler() {
		String zwibblerString = EMPTY_ZWIBBLER;
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IFile file = input.getFile();
		if (file != null) {
			try {
				InputStream in = file.getContents(true);
				zwibblerString = readText(in);

			} catch (CoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return zwibblerString;
	}

	private String readText(InputStream in) throws IOException {
		StringBuilder b = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(in, "UTF-8");
		int c;
		while ((c = reader.read()) != -1) {
			b.append((char) c);
		}
		return b.toString();
	}

	private void initSaveFunction() {
		new BrowserFunction(browser, "saveZwibbler") {
			@Override
			public Object function(Object[] arguments) {
				String zwibblerString = (String) arguments[0];
				saveZwibbler(zwibblerString);
				return null;
			}
		};
		// TODO find a place to dispose the function
	}

	private void saveZwibbler(String zwibblerString) {
		System.out.println("Saving Zwibbler: " + zwibblerString);
		try {
			IFile file = getFile();
			file.setContents(
					new ByteArrayInputStream(zwibblerString.getBytes("UTF-8")),
					true, // save, even if file is out of sync
					false, // don't keep history
					new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void executeJsFile(String fileName, Object... args) {
		String jsCode = Activator.getDefault().getJavaScript(fileName);
		executeJsCode(jsCode, args);
	}

	private void executeJsCode(String jsCode, Object... args) {
		if (args != null && args.length > 0) {
			jsCode = String.format(jsCode, args);
		}
		System.out.println("Executing JS: " + jsCode);
		boolean executed = browser.execute(jsCode);
		System.out.println(executed);
	}

	public IFile getFile() {
		return ((IFileEditorInput) getEditorInput()).getFile();
	}

	public Browser getBrowser() {
		return browser;
	}
}
