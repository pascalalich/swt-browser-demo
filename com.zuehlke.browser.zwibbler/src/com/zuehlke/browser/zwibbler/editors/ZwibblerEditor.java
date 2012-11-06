package com.zuehlke.browser.zwibbler.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.IBrowserViewerContainer;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.services.IServiceLocator;

@SuppressWarnings("restriction")
public class ZwibblerEditor extends EditorPart implements
		IBrowserViewerContainer {

	private BrowserViewer viewer;

	private Browser browser;

	private StatusLineManager statusLineManager = new StatusLineManager();

	private IActionBars actionBars = new StatusLineActionBars();

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginHeight = 0;
		compositeLayout.marginBottom = 2;
		compositeLayout.marginWidth = 0;
		compositeLayout.verticalSpacing = 0;
		compositeLayout.horizontalSpacing = 0;
		composite.setLayout(compositeLayout);
		// viewer = new BrowserViewer(composite, buildBrowserStyle());
		// viewer.setContainer(this);
		// viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser = new Browser(composite, buildBrowserStyle());
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label separator = new Label(composite, SWT.NONE);
		Display display = PlatformUI.getWorkbench().getDisplay();
		separator
				.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BORDER));
		GridData separatorLayoutData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		separatorLayoutData.heightHint = 1;
		separator.setLayoutData(separatorLayoutData);

		Composite statusLineBox = new Composite(composite, SWT.NONE);
		statusLineBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		GridLayout statusLineBoxLayout = new GridLayout();
		statusLineBoxLayout.marginHeight = 0;
		statusLineBoxLayout.marginTop = 2;
		statusLineBoxLayout.marginBottom = 0;
		statusLineBoxLayout.marginWidth = 3;
		statusLineBoxLayout.marginRight = 4;
		statusLineBoxLayout.verticalSpacing = 0;
		statusLineBoxLayout.horizontalSpacing = 0;
		statusLineBox.setLayout(statusLineBoxLayout);

		Control statusLine = statusLineManager.createControl(statusLineBox);
		GridData statusLayoutData = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		statusLayoutData.verticalIndent = 0;
		statusLayoutData.horizontalIndent = 0;
		statusLine.setLayoutData(statusLayoutData);

		initViewer();
	}

	private int buildBrowserStyle() {
		// TODO implement
		return SWT.WEBKIT;
	}

	private void initViewer() {
		Point size = getInitialSize(browser);
		int width = size.x;
		int height = size.y;
		// browser.setUrl("file:///C:/Users/ALP/Documents/GitHub/swt-browser-demo/html/zwibbler.html");
		// TODO setText in Webkit und Mozilla?
		browser.setText("<html><body><script src=\"http://zwibbler.com/component.js#width="
				+ width
				+ "&height="
				+ height
				+ "\" type=\"text/javascript\"></script><body></html>");
	}

	private Point getInitialSize(Composite c) {
		Point size = c.getSize();
		if (size.x == 0 && size.y == 0) {
			if (c.getParent() != null) {
				size = getInitialSize(c.getParent());
			} else {
				// TODO constant
				size = new Point(800, 600);
			}
		}
		return size;
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public boolean close() {
		// TODO implement correctly
		return false;
	}

	@Override
	public IActionBars getActionBars() {
		return actionBars;
	}

	@Override
	public void openInExternalBrowser(String url) {
		throw new UnsupportedOperationException();
	}

	// Unfortunately the BrowserViewer requires an IActionBars implementation,
	// but only uses IStatusLineManager
	private class StatusLineActionBars implements IActionBars {

		@Override
		public IStatusLineManager getStatusLineManager() {
			return statusLineManager;
		}

		@Override
		public void clearGlobalActionHandlers() {
		}

		@Override
		public IAction getGlobalActionHandler(String actionId) {
			return null;
		}

		@Override
		public IMenuManager getMenuManager() {
			return null;
		}

		@Override
		public IServiceLocator getServiceLocator() {
			return null;
		}

		@Override
		public IToolBarManager getToolBarManager() {
			return null;
		}

		@Override
		public void setGlobalActionHandler(String actionId, IAction handler) {
		}

		@Override
		public void updateActionBars() {
		}

	}

}
