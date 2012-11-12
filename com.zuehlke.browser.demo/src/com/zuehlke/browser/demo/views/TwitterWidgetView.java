package com.zuehlke.browser.demo.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserView;

@SuppressWarnings("restriction")
public class TwitterWidgetView extends WebBrowserView {

	private static final String START_URL = "http://www.alichs.de/twitter.html";

	@Override
	public void createPartControl(Composite parent) {
		viewer = new BrowserViewer(parent, SWT.NONE);
		viewer.setContainer(this);
		initDragAndDrop();

		setURL(START_URL);
	}
}
