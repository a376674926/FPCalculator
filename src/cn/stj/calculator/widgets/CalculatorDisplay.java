package cn.stj.calculator.widgets;

import cn.stj.calculator.R;
import cn.stj.calculator.model.Logic;
import cn.stj.calculator.util.CalculatorEditable;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * Provides vertical scrolling for the input/result EditText.
 */
public class CalculatorDisplay extends ViewSwitcher {

	private static final String ATTR_MAX_DIGITS = "maxDigits";
	private static final int DEFAULT_MAX_DIGITS = 10;

	// only these chars are accepted from keyboard
	private static final char[] ACCEPTED_CHARS = "0123456789.+-*/\u2212\u00d7\u00f7()!%^"
			.toCharArray();

	private static final int ANIM_DURATION = 250;

	public enum Scroll {
		UP, DOWN, NONE
	}

	TranslateAnimation inAnimUp;
	TranslateAnimation outAnimUp;
	TranslateAnimation inAnimDown;
	TranslateAnimation outAnimDown;
	ScaleAnimation zoomOutAnimation;
	AnimationSet animationSet = new AnimationSet(true);

	private Editable previousText;
	private TextView mDisplayText;
	private TextView mEqualText;
	private TextView mHistoryDisplay;
	private boolean mIsDisplayViewHasText;

	private int mMaxDigits = DEFAULT_MAX_DIGITS;
	private int mMaxInputLength;

	private Animation.AnimationListener mAnimListener = new Animation.AnimationListener() {
		public void onAnimationEnd(Animation paramAnonymousAnimation) {
			if (mIsDisplayViewHasText) {
				mHistoryDisplay.setText(previousText);
				mHistoryDisplay.setVisibility(View.VISIBLE);
				mEqualText.setVisibility(View.VISIBLE);
			} else {
				mHistoryDisplay.setVisibility(View.INVISIBLE);
				mEqualText.setVisibility(View.INVISIBLE);
			}
			mDisplayText.setVisibility(View.GONE);

			mDisplayText.clearAnimation();
		}

		public void onAnimationRepeat(Animation paramAnonymousAnimation) {
		}

		public void onAnimationStart(Animation paramAnonymousAnimation) {
			mDisplayText.setVisibility(View.VISIBLE);
			mHistoryDisplay.setVisibility(View.INVISIBLE);
			mEqualText.setVisibility(View.INVISIBLE);
		}
	};

	public CalculatorDisplay(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMaxInputLength = context.getResources().getInteger(
				R.integer.max_input_length);
		mMaxDigits = attrs.getAttributeIntValue(null, ATTR_MAX_DIGITS,
				DEFAULT_MAX_DIGITS);
	}

	public int getMaxDigits() {
		return mMaxDigits;
	}

	public void setLogic(Logic logic) {
		NumberKeyListener calculatorKeyListener = new NumberKeyListener() {
			public int getInputType() {
				return InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
			}

			@Override
			protected char[] getAcceptedChars() {
				return ACCEPTED_CHARS;
			}

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				/*
				 * the EditText should still accept letters (eg. 'sin') coming
				 * from the on-screen touch buttons, so don't filter anything.
				 */
				return null;
			}
		};

		Editable.Factory factory = new CalculatorEditable.Factory(logic);
		for (int i = 0; i < 2; ++i) {
			EditText text = (EditText) getChildAt(i);
			text.setBackgroundDrawable(null);
			text.setEditableFactory(factory);
			text.setKeyListener(calculatorKeyListener);
			text.setSingleLine();
			text.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					mMaxInputLength) });
		}
	}

	public void setDisplayText(TextView view) {
		mDisplayText = view;
	}

	public void setEqualText(TextView view) {
		mEqualText = view;
	}

	public void setHistory(TextView view) {
		mHistoryDisplay = view;
	}

	@Override
	public void setOnKeyListener(OnKeyListener l) {
		getChildAt(0).setOnKeyListener(l);
		getChildAt(1).setOnKeyListener(l);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		float xDelta = -(getResources().getDimension(
				R.dimen.history_equal_width) + getResources().getDimension(
				R.dimen.history_equal_margin_right));
		float scale = getResources().getDimension(R.dimen.history_text_size)
				/ getResources().getDimension(R.dimen.display_text_size);

		inAnimUp = new TranslateAnimation(0, 0, h, 0);
		inAnimUp.setDuration(ANIM_DURATION);
		outAnimUp = new TranslateAnimation(0.0F, xDelta / scale, 0.0F, -h);
		outAnimUp.setDuration(ANIM_DURATION);
		outAnimUp.setAnimationListener(mAnimListener);
		inAnimDown = new TranslateAnimation(0, 0, -h, 0);
		inAnimDown.setDuration(ANIM_DURATION);
		outAnimDown = new TranslateAnimation(0, 0, 0, h);
		outAnimDown.setDuration(ANIM_DURATION);
		zoomOutAnimation = new ScaleAnimation(1.0F, scale, 1.0F, scale,
				Animation.RELATIVE_TO_SELF, 1.0F, Animation.RELATIVE_TO_SELF,
				0.0F);
		zoomOutAnimation.setDuration(ANIM_DURATION);

		animationSet = new AnimationSet(true);
		animationSet.addAnimation(outAnimUp);
		animationSet.addAnimation(zoomOutAnimation);
		animationSet.setFillAfter(true);
	}

	public void insert(String delta) {
		EditText editor = (EditText) getCurrentView();
		int textLength = editor.getText().length();
		if (textLength >= mMaxInputLength) {
			Toast.makeText(
					getContext(),
					getContext().getResources().getString(
							R.string.number_too_long), Toast.LENGTH_SHORT)
					.show();
		}
		int cursor = editor.getSelectionStart();
		editor.getText().insert(cursor, delta);
	}

	public EditText getEditText() {
		return (EditText) getCurrentView();
	}

	public Editable getText() {
		EditText text = (EditText) getCurrentView();
		return text.getText();
	}

	public void setText(CharSequence text, Scroll dir) {
		setText(text, dir, false);
	}

	public void setText(CharSequence text, Scroll dir, boolean isClear) {
		Log.d("daiq", "text=" + text);
		previousText = getText();
		mIsDisplayViewHasText = (previousText != null)
				&& (!"".equals(previousText));

		if (getText().length() == 0) {
			dir = Scroll.NONE;
		}
		// Calculator.log("setText: dir=" + dir);

		if (dir == Scroll.UP) {
			// setInAnimation(inAnimUp);
			// setOutAnimation(outAnimUp);
			if (mIsDisplayViewHasText) {
				setInAnimation(inAnimUp);
				mDisplayText.setText(previousText);
				mDisplayText.startAnimation(animationSet);
			} else {
				mDisplayText.setText("");
				// mDisplayText.setText("0");
			}
		} else if (dir == Scroll.DOWN) {
			setInAnimation(inAnimDown);
			setOutAnimation(outAnimDown);
		} else { // Scroll.NONE
			setInAnimation(null);
			setOutAnimation(null);
			mHistoryDisplay.setVisibility(View.INVISIBLE);
			mEqualText.setVisibility(View.INVISIBLE);
		}

		EditText editText = (EditText) getNextView();
		editText.setText(text);
		// Calculator.log("selection to " + text.length() + "; " + text);
		editText.setSelection(text.length());
		showNext();
	}

	public int getSelectionStart() {
		EditText text = (EditText) getCurrentView();
		return text.getSelectionStart();
	}

	@Override
	protected void onFocusChanged(boolean gain, int direction, Rect prev) {
		// Calculator.log("focus " + gain + "; " + direction + "; " + prev);
		if (!gain) {
			requestFocus();
		}
	}
}
