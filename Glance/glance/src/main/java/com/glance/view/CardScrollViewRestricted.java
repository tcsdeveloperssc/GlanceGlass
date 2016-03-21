package com.glance.view;

import android.content.Context;
import android.view.KeyEvent;

import com.google.android.glass.widget.CardScrollView;

public class CardScrollViewRestricted extends CardScrollView {
	private boolean restrict = false;
	

	public CardScrollViewRestricted(Context context) {
		super(context);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	public boolean isRestrict() {
		return restrict;
	}

	public void setRestrict(boolean restrict) {
		this.restrict = restrict;
	}

}