package com.zuehlke.browser.core;

import java.util.HashMap;
import java.util.Map;

class BrowserStore {

	private static final BrowserStore INSTANCE = new BrowserStore();

	private Map<String, BrowserReference> browsers = new HashMap<String, BrowserReference>();

	public static BrowserStore getInstance() {
		return INSTANCE;
	}

	public BrowserReference getBrowser(String browserId) {
		return browsers.get(browserId);
	}

}
