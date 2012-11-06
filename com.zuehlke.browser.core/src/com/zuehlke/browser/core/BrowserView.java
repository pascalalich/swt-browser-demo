package com.zuehlke.browser.core;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserView;

@SuppressWarnings("restriction")
public class BrowserView extends WebBrowserView {

	public static final String VIEW_ID = "com.zuehlke.browser.core.view";
	
	@Override
	public void createPartControl(Composite parent) {
		String browserId = getViewSite().getSecondaryId();
		BrowserStore browserStore = BrowserStore.getInstance();
		ViewBrowser browser = (ViewBrowser) browserStore.getBrowser(browserId);
		int style = browser.getBrowserStyle();
		viewer = new BrowserViewer(parent, style);
		viewer.setContainer(this);
		initDragAndDrop();
		initAppearance(browser);
	}

	private void initAppearance(ViewBrowser browser) {
		// TODO
	}

}
