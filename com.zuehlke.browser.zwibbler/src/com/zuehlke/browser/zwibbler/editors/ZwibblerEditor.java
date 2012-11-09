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

		browser.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				updateZwibblerLayout();
			}
		});

		setDirty(true);
		updatePartName();
		initZwibbler();
	}

	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
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
		System.out.println("Setting URL to browser: " + url);
		browser.addProgressListener(new ProgressAdapter() {
			@Override
			public void completed(ProgressEvent event) {
				new BrowserFunction(browser, "loadZwibbler") {
					@Override
					public Object function(Object[] arguments) {
						return loadZwibbler();
					}
				};

				new BrowserFunction(browser, "saveZwibbler") {
					@Override
					public Object function(Object[] arguments) {
						String zwibblerString = (String) arguments[0];
						saveZwibbler(zwibblerString);
						return null;
					}
				};

				updateZwibblerLayout();
				executeJsFile("loadZwibbler.js");
			}
		});

		Browser.clearSessions();
		browser.setUrl(url);
	}

	private void updateZwibblerLayout() {
		executeJsFile("initZwibbler.js", browser.getSize().x,
				browser.getSize().y);
	}

	private String loadZwibbler() {
		String zwibblerString = "zwibbler3.[]";
		IFileEditorInput input = (IFileEditorInput) getEditorInput();
		IFile file = input.getFile();
		if (file != null) {
			try {
				StringBuilder b = new StringBuilder();
				InputStream in = file.getContents(true);
				InputStreamReader reader = new InputStreamReader(in, "UTF-8");
				int c;
				while ((c = reader.read()) != -1) {
					b.append((char) c);
				}
				zwibblerString = b.toString();

			} catch (CoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return zwibblerString;
	}

	private void saveZwibbler(String zwibblerString) {
		System.out.println(zwibblerString);
		try {
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(
					new ByteArrayInputStream(zwibblerString.getBytes("UTF-8")),
					true, // keep saving, even if IFile is out of sync with the
							// Workspace
					false, // dont keep history
					new NullProgressMonitor()); // progress monitor
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void executeJsFile(String fileName, Object... args) {
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

}
