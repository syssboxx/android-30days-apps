package com.bakhtiyor.android.batterylevel;

interface IBatteryLevelService {
	int getHealth();
	int getLevel();
	int getStatus();
}