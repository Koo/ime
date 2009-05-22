package com.mamezou.android.im;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;

public class MyKeyboard extends Keyboard {
	/**
	 * IME変更のキーコード
	 */
	public static final int KEYCODE_OPTION = -200;

	private Key enterKey;

	public MyKeyboard(Context context, int xmlLayoutResId) {
		super(context, xmlLayoutResId);
	}

	@Override
	protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
			XmlResourceParser parser) {
		Key key = super.createKeyFromXml(res, parent, x, y, parser);
		if (key.codes[0] == 10) {
			enterKey = key;
		}
		return key;
	}
    /**
     * カスタムキーのアイコンとラベルを変更する。
     * @param icon アイコン
     * @param label ラベル
     */
    public void setEnterKeyLooks(Drawable icon, String label) {
        if (enterKey == null) {
            return;
        }

        enterKey.icon = icon;
        enterKey.label = label;
    }
}
