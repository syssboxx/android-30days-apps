package com.bakhtiyor.android.tumblr;

interface ITumblrService {
	void uploadPhoto(String email, String password, String caption, boolean isPrivate, String file);
}