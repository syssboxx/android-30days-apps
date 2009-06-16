package com.bakhtiyor.android.countmein;

public interface ICounter {
	int get();
	void set(int value);
	int incrementAndGet();
	int decrementAndGet();
}
