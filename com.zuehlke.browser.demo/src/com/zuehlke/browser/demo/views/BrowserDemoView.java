package com.zuehlke.browser.demo.views;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserView;

@SuppressWarnings("restriction")
public class BrowserDemoView extends WebBrowserView {

	private static final String START_URL = "http://wiki.eclipse.org/Eclipse_DemoCamps_November_2012/Hamburg";
	private static final String RESTRICT_URL = "http://wiki.eclipse.org";
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		init();
		setURL(START_URL);
	}

	private void init() {
		final Browser browser = viewer.getBrowser();

		browser.addProgressListener(new ProgressAdapter() {
			@Override
			public void completed(ProgressEvent event) {
				if (browser.getUrl().equals(START_URL)) {
					String js = 
						"var links = document.getElementsByTagName('a');"
								+ "for (var i = 0; i < links.length; i++) {"
								+ "  var link = links[i];" 
								+ "  if (link.innerText == 'Pascal Alich') {" 
								+ "    var html = ' &nbsp;&nbsp;- <a href=\"#startDemo\" class=\"external\">Start Demo</a>';"
								+ "    link.insertAdjacentHTML('AfterEnd', html);"
								+ "    break;"
								+ "  }"
								+ "}";
					boolean executed = browser.execute(js);
					System.out.println(js + "-->" + executed);
				}
			}
		});
		
		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changing(LocationEvent event) {
				// Restrict browser to Eclipse Wiki
				if (!event.location.startsWith(RESTRICT_URL)) {
					event.doit = false;
					Shell modalShell = PlatformUI.getWorkbench()
							.getModalDialogShellProvider().getShell();
					MessageDialog.openInformation(modalShell,
							"Sandbox restriction",
							"Navigation is restricted to: " + RESTRICT_URL);
				}
				if (event.location.endsWith("#startDemo")) {
					System.out.println("Starting demo...");
					IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
					try {
						IWebBrowser externalBrowser = browserSupport.getExternalBrowser();
						externalBrowser.openURL(new URL("http://www.zwibbler.com"));
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					event.doit = false;
				}
			}
		});
	}

}
