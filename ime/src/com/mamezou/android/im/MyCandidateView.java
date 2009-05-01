package com.mamezou.android.im;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyCandidateView extends ListView {

	
	public MyCandidateView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.d("SampleIME", "onTouch(" + ev + ")");
		return super.onTouchEvent(ev);
	}
	
}
