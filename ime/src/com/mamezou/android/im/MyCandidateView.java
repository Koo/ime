package com.mamezou.android.im;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyCandidateView extends View {

	private static final int OUT_OF_BOUNDS = -1;
	private SampleIME sampleIME;
	private List<String> candidates;
	private Paint paint;
	private int touchX = OUT_OF_BOUNDS;
	private int selectedIndex;
	private int backgroundColor;
	private int foregroundColor;
	
	/**
	 * 文字の余白
	 */
	private static final int X_GAP = 10;

	public void setSampleIME(SampleIME sampleIME) {
		this.sampleIME = sampleIME;
	}

	public MyCandidateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		candidates = new ArrayList<String>();
		backgroundColor = context.getResources().getColor(R.color.candidateListBackground);
		foregroundColor = context.getResources().getColor(R.color.candidateListForeground);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.candidate_font_height));
		paint.setStrokeWidth(0);

	}


	public void setCandidates(List<String> selection) {
		this.candidates = selection;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (candidates == null) {
			return;
		}

		int height = getHeight();

		int x = 0;
		int y = (int) (((height - paint.getTextSize()) / 2) - paint.ascent());

		for (int i = 0; i < candidates.size(); i++) {
			String candidate = candidates.get(i);
			float textWidth = paint.measureText(candidate);
			final int wordWidth = (int) textWidth + X_GAP * 2;
			
			paint.setColor(backgroundColor);
			canvas.drawRect(x, 0, x + X_GAP * 2, y, paint);

			paint.setColor(foregroundColor);
			canvas.drawText(candidate, x + X_GAP, y, paint);

			if (touchX >= x && touchX < x + wordWidth) {
				// onTouchの後であれば、選択されている文字を更新
				selectedIndex = i;
			}
			x += wordWidth;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		Log.d("SampleIME", "onTouch(" + me + ")");
		int action = me.getAction();
		int x = (int) me.getX();
		int y = (int) me.getY();
		touchX = x;

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// タッチしている箇所が変わった場合は再描画を行い、選択箇所を更新
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (y <= 0) {
				// CandidateViewから外れたら、クリックされたのと同じ扱い
				if (selectedIndex >= 0) {
					sampleIME.onCandidateSelect(candidates.get(selectedIndex));
					selectedIndex = -1;
				}
			}

			// タッチしている箇所が変わった場合は再描画を行い、選択箇所を更新
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			if (selectedIndex >= 0) {
				sampleIME.onCandidateSelect(candidates.get(selectedIndex));
			}
			selectedIndex = -1;
			requestLayout();
			break;
		}
		return true;
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = resolveSize(50, widthMeasureSpec);
        
        // Viewのサイズを計算
        Rect padding = new Rect();
        final int desiredHeight = ((int)paint.getTextSize()) + 2
                + padding.top + padding.bottom;
        
        setMeasuredDimension(measuredWidth,
                resolveSize(desiredHeight, heightMeasureSpec));
    }

}
