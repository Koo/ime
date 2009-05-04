package com.mamezou.android.im;

import java.util.ArrayList;
import java.util.List;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

public class SampleIME extends InputMethodService {

	private KeyboardView keyboardView;
	private MyKeyboard myKeyboard;
	private List<String> candidatesList = new ArrayList<String>();
	private StringBuilder composing = new StringBuilder();
	private List<String> selection = new ArrayList<String>();
	private MyCandidateView candidatesView;

	public SampleIME() {
		candidatesList.add("aaa");
		candidatesList.add("aab");
		candidatesList.add("abb");
		candidatesList.add("bbb");
		candidatesList.add("bba");
		candidatesList.add("baa");

	}

	@Override
	public AbstractInputMethodImpl onCreateInputMethodInterface() {
		return super.onCreateInputMethodInterface();
	}

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
		myKeyboard = new MyKeyboard(this, R.xml.mykeyboard);
	}

	@Override
	public View onCreateInputView() {
		keyboardView = (KeyboardView) getLayoutInflater().inflate(
				R.layout.keyboard, null);

		keyboardView.setKeyboard(myKeyboard);
		keyboardView.setOnKeyboardActionListener(listener);
		return keyboardView;
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		super.onStartInputView(info, restarting);
	}

	private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

		public void onKey(int primaryCode, int[] keyCodes) {
			StringBuffer sb = new StringBuffer();
			sb.append((char) primaryCode);
			if (primaryCode == -1) {
				getCurrentInputConnection().commitText(composing,
						composing.length());
				composing.delete(0, composing.length());
				// 選択肢を非表示
				setCandidatesViewShown(false);
				updateInputViewShown();
			} else {
				composing.append((char) primaryCode);
				getCurrentInputConnection().setComposingText(composing,
						composing.length());
				// 選択肢を表示
				setCandidatesViewShown(true);
				updateCandidate();
				updateInputViewShown();
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

	public void updateCandidate() {
		String start = composing.toString();
		selection.clear();
		for (String candidate : candidatesList) {
			if (candidate.startsWith(start)) {
				selection.add(candidate);
			}
		}
		candidatesView.setCandidates(selection);
	}

	@Override
	public View onCreateCandidatesView() {
		LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.candidates, null);
		candidatesView = (MyCandidateView) layout
				.findViewById(R.id.candidatesListView);
		candidatesView.setSampleIME(this);
		return layout;
	}


	@Override
	public void onStartCandidatesView(EditorInfo info, boolean restarting) {
		super.onStartCandidatesView(info, restarting);
		Log.d("SampleIME", "call onStartCandidatesView(" + info.toString()
				+ ", " + restarting + ")");
	}

	public void onCandidateSelect(String str) {
		getCurrentInputConnection().commitText(str, str.length());
		composing.delete(0, composing.length());
		setCandidatesViewShown(false);
	}
}
