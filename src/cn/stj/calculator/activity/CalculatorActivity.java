package cn.stj.calculator.activity;

import cn.stj.calculator.R;
import cn.stj.calculator.adapter.HistoryAdapter;
import cn.stj.calculator.model.EventListener;
import cn.stj.calculator.model.History;
import cn.stj.calculator.model.Logic;
import cn.stj.calculator.model.Persist;
import cn.stj.calculator.widgets.CalculatorDisplay;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author jackey
 * 
 */
public class CalculatorActivity extends Activity implements Logic.Listener,
		View.OnClickListener, View.OnLongClickListener {

	private static final String TAG = CalculatorActivity.class.getSimpleName();
	private static final boolean DEBUG = false;
	private View mCalculateView;
	private CalculatorDisplay mDisplay;
	private Persist mPersist;
	private History mHistory;
	private Logic mLogic;
	private EventListener mListener = new EventListener();
	private Button mClearBtn;
	private Button mBackBtn;
	// 加按钮
	private Button mAddBtn;
	// 等于按钮
	private Button mEqualBtn;
	// 乘按钮
	private Button mMulBtn;
	// 除按钮
	private Button mDivBtn;
	// 减按钮
	private Button mSubBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		setContentView(R.layout.activity_calculator);

		init();
	}

	private void init() {
		initCacheData();
		initView();
	}

	private void initCacheData() {
		mPersist = new Persist(this);
		mPersist.load();
		mHistory = mPersist.history;
	}

	private void initView() {
		mClearBtn = (Button) findViewById(R.id.bottom_left_button);
		mBackBtn = (Button) findViewById(R.id.bottom_right_button);
		mAddBtn = (Button) findViewById(R.id.add_btn);
		mEqualBtn = (Button) findViewById(R.id.equal_btn);
		mMulBtn = (Button) findViewById(R.id.mul_btn);
		mDivBtn = (Button) findViewById(R.id.div_btn);
		mSubBtn = (Button) findViewById(R.id.sub_btn);
		mClearBtn.setOnClickListener(this);
		mClearBtn.setOnLongClickListener(this);
		mBackBtn.setOnClickListener(this);
		mAddBtn.setOnClickListener(this);
		mEqualBtn.setOnClickListener(this);
		mMulBtn.setOnClickListener(this);
		mDivBtn.setOnClickListener(this);
		mSubBtn.setOnClickListener(this);

		View displayView = findViewById(R.id.display);
		mDisplay = ((CalculatorDisplay) displayView
				.findViewById(R.id.calculator_display));
		mDisplay.setHistory((TextView) displayView.findViewById(R.id.history));
		mDisplay.setDisplayText((TextView) displayView
				.findViewById(R.id.display_text_view));
		mDisplay.setEqualText((TextView) displayView
				.findViewById(R.id.equal_text_view));

		mLogic = new Logic(this, mHistory, mDisplay);
		mLogic.setListener(this);
		mLogic.setDeleteMode(mPersist.getDeleteMode());
		mLogic.setLineLength(mDisplay.getMaxDigits());
		HistoryAdapter localHistoryAdapter = new HistoryAdapter(this, mHistory,
				mLogic);
		mHistory.setObserver(localHistoryAdapter);
		mListener.setHandler(mLogic, null);
		mListener.setmAddBtn(mAddBtn);
		mListener.setmDivBtn(mDivBtn);
		mListener.setmEqualBtn(mEqualBtn);
		mListener.setmMulBtn(mMulBtn);
		mListener.setmSubBtn(mSubBtn);
		mListener.setmClearBtn(mClearBtn);
		mDisplay.setOnKeyListener(mListener);
		mLogic.resumeWithHistory();

	}

	@Override
	public void onDeleteModeChange() {
	}

	public void onPause() {
		super.onPause();
		mLogic.updateHistory();
		mPersist.setDeleteMode(mLogic.getDeleteMode());
		mPersist.save();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bottom_right_button) {
			this.finish();
		} else {
			mListener.onClick(v);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		mListener.onLongClick(v);
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mBackBtn.setSelected(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mBackBtn.performClick();
			mBackBtn.setSelected(false);
		}
		return super.onKeyDown(keyCode, event);
	}

}
