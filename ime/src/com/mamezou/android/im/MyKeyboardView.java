package com.mamezou.android.im;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;

public class MyKeyboardView extends KeyboardView {

	public MyKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
        	// モードチェンジボタン長押しで設定画面表示
            getOnKeyboardActionListener().onKey(MyKeyboard.KEYCODE_OPTION, null);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }
}
