package com.bakhtiyor.android.twitter;

import java.util.Collections;
import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Message;
import winterwell.jtwitter.Twitter.Status;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ocpsoft.pretty.time.PrettyTime;

public class TimelineActivity extends ListActivity {
	public static final String PERSONAL_TIMELINE = TimelineActivity.class.getCanonicalName()
			+ ".personal";
	public static final String FRIENDS_TIMELINE = TimelineActivity.class.getCanonicalName()
			+ ".friends";
	public static final String INBOX_MESSAGES = TimelineActivity.class.getCanonicalName()
			+ ".inbox";
	public static final String SENT_MESSAGES = TimelineActivity.class.getCanonicalName() + ".sent";

	private Twitter twitter;
	private ImageLoader imageLoader;
	private View listView;
	private View progressView;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		Drawable drawable = getResources().getDrawable(R.drawable.frame);
		progressView = findViewById(R.id.progress);
		listView = findViewById(android.R.id.list);
		imageLoader = new ImageLoader(drawable);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (getIntent() != null) {
			if (getIntent().getAction().equals(PERSONAL_TIMELINE)
					|| getIntent().getAction().equals(FRIENDS_TIMELINE)
					|| getIntent().getAction().equals(INBOX_MESSAGES)
					|| getIntent().getAction().equals(SENT_MESSAGES)) {
				if (sharedPreferences.getString(Settings.USERNAME, "").equals("")
						|| sharedPreferences.getString(Settings.PASSWORD, "").equals("")) {
					startSettingsActivity();
					Toast.makeText(this, R.string.message_setup_settings, Toast.LENGTH_LONG).show();
				} else {
					loadTweets();
				}
			}
		}
	}

	private void loadTweets() {
		String username = sharedPreferences.getString(Settings.USERNAME, "");
		String password = sharedPreferences.getString(Settings.PASSWORD, "");
		twitter = new Twitter(username, password);
		new TweetsLoad().execute(getIntent().getAction());
	}

	private void setTimelineAdapter(final List<? extends Twitter.ITweet> tweets) {
		ListAdapter adapter = new BaseAdapter() {
			PrettyTime prettyTime = new PrettyTime();

			@Override
			public int getCount() {
				return tweets.size();
			}

			@Override
			public Object getItem(int position) {
				return tweets.get(position);
			}

			@Override
			public long getItemId(int position) {
				return tweets.get(position).getId();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = null;
				if (convertView != null) {
					view = convertView;
				} else {
					view = getLayoutInflater().inflate(R.layout.row, null);
				}

				TextView textView1 = (TextView) view.findViewById(R.id.text1);
				TextView textView2 = (TextView) view.findViewById(R.id.text2);
				TextView textView3 = (TextView) view.findViewById(R.id.text3);
				ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
				ImageLoader.Callback callback = new ImageLoader.Callback() {
					public void done(String url, Bitmap bitmap) {
						runOnUiThread(new Runnable() {
							public void run() {
								notifyDataSetChanged();
							}
						});
					}
				};

				Twitter.ITweet tweet = tweets.get(position);

				String url = tweet.getUser().profileImageUrl.toString();
				if (url != null) {
					Drawable drawable = imageLoader.load(url, callback);
					if (drawable != null) {
						avatar.setImageDrawable(drawable);
					}
				}
				textView1.setText(String.format("%s", tweet.getUser().name));
				textView2.setText(tweet.getText());

				if (getIntent().getAction().equals(PERSONAL_TIMELINE)
						|| getIntent().getAction().equals(FRIENDS_TIMELINE)) {
					Status status = (Status) tweet;
					textView3.setText(String.format("%s from %s", prettyTime
							.format(status.createdAt), status.source.replaceAll(
							"<[a-zA-Z\\/][^>]*>", "")));
				} else if (getIntent().getAction().equals(INBOX_MESSAGES)
						|| getIntent().getAction().equals(SENT_MESSAGES)) {
					Twitter.Message message = (Message) tweet;
					textView3.setText(String
							.format("%s", prettyTime.format(message.getCreatedAt())));
				}
				return view;
			}
		};
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.optionsmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_reload:
			listView.setVisibility(View.GONE);
			progressView.setVisibility(View.VISIBLE);
			loadTweets();
			return true;
		case R.id.menu_update_status:
			final EditText input = new EditText(this);
			input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(140) });
			new AlertDialog.Builder(this).setView(input).setTitle(R.string.title_update_status)
					.setPositiveButton(R.string.btn_update, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface d, final int which) {
							d.dismiss();
							if (input.getText().length() > 0) {
								twitter.updateStatus(input.getText().toString());
							}
						}
					}).setNeutralButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface d, final int which) {
							d.dismiss();
						}
					}).create().show();
			;
			return true;
		case R.id.menu_settings:
			startSettingsActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startSettingsActivity() {
		Intent intent = new Intent(this, Settings.class);
		startActivityForResult(intent, 1024);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (getIntent().getAction().equals(INBOX_MESSAGES)
				|| getIntent().getAction().equals(SENT_MESSAGES)) {
			menu.findItem(R.id.menu_update_status).setVisible(false);
			menu.findItem(R.id.menu_settings).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1024) {
			loadTweets();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class TweetsLoad extends AsyncTask<String, Void, List<? extends Twitter.ITweet>> {
		@Override
		public List<? extends Twitter.ITweet> doInBackground(String... params) {
			String timeline = params[0];

			if (timeline.equals(PERSONAL_TIMELINE))
				return twitter.getUserTimeline();
			else if (timeline.equals(FRIENDS_TIMELINE))
				return twitter.getFriendsTimeline();
			else if (timeline.equals(INBOX_MESSAGES))
				return twitter.getDirectMessages();
			else if (timeline.equals(SENT_MESSAGES))
				return twitter.getDirectMessagesSent();
			return Collections.<Twitter.ITweet> emptyList();
		}

		@Override
		public void onPostExecute(List<? extends Twitter.ITweet> result) {
			progressView.setVisibility(View.GONE);
			setTimelineAdapter(result);
			listView.setVisibility(View.VISIBLE);
		}
	}
}
