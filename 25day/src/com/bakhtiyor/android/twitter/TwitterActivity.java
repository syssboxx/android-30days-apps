package com.bakhtiyor.android.twitter;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TwitterActivity extends TabActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		final TabHost tabHost = getTabHost();

		Intent personalTimeline = new Intent(this, TimelineActivity.class);
		personalTimeline.setAction(TimelineActivity.PERSONAL_TIMELINE);
		tabHost.addTab(tabHost.newTabSpec("personal").setIndicator("Personal").setContent(
				personalTimeline));

		Intent friendsTimeline = new Intent(this, TimelineActivity.class);
		friendsTimeline.setAction(TimelineActivity.FRIENDS_TIMELINE);
		tabHost.addTab(tabHost.newTabSpec("friends").setIndicator("Friends").setContent(
				friendsTimeline));

		Intent directTimeline = new Intent(this, DirectMessagesActivity.class);
		directTimeline.setAction(Intent.ACTION_MAIN);
		tabHost.addTab(tabHost.newTabSpec("direct").setIndicator("Direct").setContent(
				directTimeline));
    }

}