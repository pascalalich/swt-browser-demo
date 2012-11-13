package com.zuehlke.browser.zwibbler.download;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.zuehlke.browser.zwibbler.editors.ZwibblerEditor;

public class DownloadHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ZwibblerEditor zwibblerEditor = (ZwibblerEditor) HandlerUtil
				.getActiveEditorChecked(event);

		ZwibblerDownloadJob downloadJob = new ZwibblerDownloadJob(
				zwibblerEditor);
		downloadJob.schedule();

		return null;
	}
}
