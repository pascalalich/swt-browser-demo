package com.zuehlke.browser.demo.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.zuehlke.browser.demo.views.BrowserDemoView;
import com.zuehlke.browser.demo.views.TwitterWidgetView;

public class PerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);

		layout.addView(BrowserDemoView.VIEW_ID, IPageLayout.LEFT, 0.95f,
				IPageLayout.ID_EDITOR_AREA);

		layout.addPlaceholder(TwitterWidgetView.VIEW_ID, IPageLayout.RIGHT,
				0.7f, BrowserDemoView.VIEW_ID);
	}

}
