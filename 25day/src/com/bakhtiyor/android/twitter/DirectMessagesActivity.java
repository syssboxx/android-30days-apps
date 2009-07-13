package com.bakhtiyor.android.twitter;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class DirectMessagesActivity extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);
		final TabHost tabHost = getTabHost();

		Intent inboxMessages = new Intent(this, TimelineActivity.class);
		inboxMessages.setAction(TimelineActivity.INBOX_MESSAGES);
		tabHost.addTab(tabHost.newTabSpec("inbox").setIndicator("Inbox").setContent(inboxMessages));

		Intent sentMessages = new Intent(this, TimelineActivity.class);
		sentMessages.setAction(TimelineActivity.SENT_MESSAGES);
		tabHost.addTab(tabHost.newTabSpec("sent").setIndicator("Sent").setContent(sentMessages));
	}
}
