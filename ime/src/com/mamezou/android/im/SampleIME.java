package com.mamezou.android.im;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class SampleIME extends InputMethodService {
	private KeyboardView keyboardView;
	private MyKeyboard abKeyboard;
	private MyKeyboard numKeyboard;
	private MyKeyboard qwertyKeyboard;
	private StringBuilder composing = new StringBuilder();
	private MyCandidateView candidatesView;
	private int imeOptions;

	private MyKeyboard currentKeyboard;

	@Override
	public boolean onEvaluateInputViewShown() {
		return true;
	}

	@Override
	public boolean onEvaluateFullscreenMode() {
		return false;
	}

	@Override
	public boolean isExtractViewShown() {
		return true;
	}

	@Override
	public void onInitializeInterface() {
		abKeyboard = new MyKeyboard(this, R.xml.abkeyboard);
		numKeyboard = new MyKeyboard(this, R.xml.numkeyboard);
		qwertyKeyboard = new MyKeyboard(this, R.xml.qwertykeyboard);
	}

	private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

		public void onKey(int primaryCode, int[] keyCodes) {
			if (primaryCode == -100) {
				getCurrentInputConnection().commitText(composing,
						composing.length());
				composing.delete(0, composing.length());
				// 選択肢を非表示
				setCandidatesViewShown(false);
			} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
				// キーボードを変更
				if (currentKeyboard == numKeyboard) {
					currentKeyboard = abKeyboard;
				} else if (currentKeyboard == abKeyboard) {
					currentKeyboard = qwertyKeyboard;
				} else {
					currentKeyboard = numKeyboard;
				}
				
				updateCurrentKeyboardView();
				keyboardView.setKeyboard(currentKeyboard);

			} else if (primaryCode == MyKeyboard.KEYCODE_OPTION) {
				// オプション画面の代わりにIME選択画面を表示
				InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				manager.showInputMethodPicker();
			} else if (primaryCode == '\n') {
				getCurrentInputConnection().commitText(composing,
						composing.length());
				composing.delete(0, composing.length());
				sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
			} else {
				appendToComposing(primaryCode);
			}
		}

		public void onPress(int primaryCode) {
			Log.d("SampleIME", "onPress(" + primaryCode + ") called");
		}

		public void onRelease(int primaryCode) {
			Log.d("SampleIME", "onRelease(" + primaryCode + ") called");

		}

		public void onText(CharSequence text) {
			InputConnection connection = getCurrentInputConnection();
			connection.commitText(text, 1);
		}

		public void swipeDown() {
			Log.d("SampleIME", "swipeDown()");

		}

		public void swipeLeft() {
			Log.d("SampleIME", "swipeLeft()");

		}

		public void swipeRight() {
			Log.d("SampleIME", "swipeRight()");

		}

		public void swipeUp() {
			Log.d("SampleIME", "swipeUp()");

		}

	};

	@Override
	public View onCreateInputView() {
		keyboardView = (KeyboardView) getLayoutInflater().inflate(
				R.layout.keyboard, null);

		keyboardView.setOnKeyboardActionListener(listener);
		return keyboardView;
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
        // 入力タイプによってキーボードのタイプを変更
        switch (info.inputType&EditorInfo.TYPE_MASK_CLASS) {
        case EditorInfo.TYPE_CLASS_NUMBER:
        	// 数値入力は数字だけのIM
        	currentKeyboard = numKeyboard;
        	break;
        default:
        	// その他はAとBだけのIM
        	currentKeyboard = abKeyboard;
        	break;
        
        }

        // 開始時に得た情報からキーボードの見た目を変更
		imeOptions = info.imeOptions;
		updateCurrentKeyboardView();

		keyboardView.setKeyboard(currentKeyboard);
	}

	private void updateCurrentKeyboardView() {
		Drawable icon = null;
		String label = null;
		if ((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEARCH) {
			// 検索の場合は検索アイコン
			icon = getResources().getDrawable(R.drawable.sym_keyboard_search);
		} else {
			// その他は文字列「ent」
			label = getResources().getString(R.string.label_enter_key);
		}

		currentKeyboard.setEnterKeyLooks(icon, label);
		if (keyboardView != null) {
			keyboardView.invalidate();
		}
	}

	private static final List<String> CANDIDATES_LIST;

	static {
		List<String> l = new ArrayList<String>();
		l.add("aaa");
		l.add("aab");
		l.add("abb");
		l.add("bbb");
		l.add("bba");
		l.add("baa");
		
		CANDIDATES_LIST = Collections.unmodifiableList(l);
	}
	private List<String> selection = new ArrayList<String>();

	private void updateCandidate() {
		String start = composing.toString();
		selection.clear();
		for (String candidate : CANDIDATES_LIST) {
			if (candidate.startsWith(start)) {
				selection.add(candidate);
			}
		}
		// CandidatesViewの選択肢を更新
		candidatesView.setCandidates(selection);
	}

	@Override
	public View onCreateCandidatesView() {
		candidatesView = (MyCandidateView) getLayoutInflater().inflate(
				R.layout.candidates, null);
		candidatesView.setSampleIME(this);
		return candidatesView;
	}

	public void onCandidateSelect(String str) {
		getCurrentInputConnection().commitText(str, str.length());
		composing.delete(0, composing.length());
		setCandidatesViewShown(false);
	}

	private void appendToComposing(int primaryCode) {
		composing.append((char) primaryCode);
		InputConnection ic = getCurrentInputConnection();
		ic.setComposingText(composing, composing.length());

		// 設定から候補表示を行うかどうかを取得
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showSugestion = sp.getBoolean("show_suggestions", true);

		if (showSugestion) {
			// CandidatesViewを表示
			setCandidatesViewShown(true);
			// 選択肢の更新
			updateCandidate();
		}
	}

	@Override
	public void onFinishInput() {
		super.onFinishInput();
		composing.delete(0, composing.length());
		setCandidatesViewShown(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ハードウェアキーボードの処理
		int unicode = event.getUnicodeChar();
		if (('a' <= unicode && unicode <= 'z' ) 
				|| ('A' <= unicode && unicode <= 'Z' )
				|| ('0' <= unicode && unicode <= '9')) {
			// 文字入力はこっちで処理
			appendToComposing(unicode);
			// 確定まではEditTextにはハンドルさせない
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
