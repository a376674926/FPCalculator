
package cn.stj.calculator.model;


import cn.stj.calculator.R;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class EventListener implements View.OnKeyListener,
        View.OnClickListener,
        View.OnLongClickListener {
    private static final String TAG = EventListener.class.getSimpleName() ;
	Logic mHandler;
    ViewPager mPager;
    //加按钮
  	private Button mAddBtn;
  	//等于按钮
  	private Button mEqualBtn;
  	//乘按钮
  	private Button mMulBtn ;
  	//除按钮
  	private Button mDivBtn ;
  	//减按钮
  	private Button mSubBtn ;
  	//清除按钮
  	private Button mClearBtn ;
  	//标识触发清除按钮是否长按
  	private long mCount ;

    public void setHandler(Logic handler, ViewPager pager) {
        mHandler = handler;
        mPager = pager;
    }

    @Override
    public void onClick(View view) {
    	String text = "" ;
    	switch (view.getId()) {
		case R.id.add_btn:
			text = "+" ;
			break;
		case R.id.sub_btn:
			text = "\u2212" ;
			break;
		case R.id.mul_btn:
			text = "\u00d7" ;
			break;
		case R.id.div_btn:
			text = "\u00f7" ;
			break;
		case R.id.equal_btn:
			text = null ;
			mHandler.onEnter();
			break;
		case R.id.bottom_left_button:
			text = null ;
			mHandler.onDelete(); ;
			break ;
		default:
			break;
		}
    	if(!TextUtils.isEmpty(text)){
    		mHandler.insert(text);
    	}
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == R.id.bottom_left_button) {
            mHandler.onClear();
            return true;
        }
    	return false ;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        int action = keyEvent.getAction();

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean eat = mHandler.eatHorizontalMove(keyCode == KeyEvent.KEYCODE_DPAD_LEFT);
        }

        //Work-around for spurious key event from IME, bug #1639445
        if (action == KeyEvent.ACTION_MULTIPLE && keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            return true; 
        }

        if (keyEvent.getUnicodeChar() == '=') {
            if (action == KeyEvent.ACTION_UP) {
                mHandler.onEnter();
            }
            return true;
        }

        if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER &&
                keyCode != KeyEvent.KEYCODE_DPAD_UP &&
                keyCode != KeyEvent.KEYCODE_DPAD_DOWN &&
                keyCode != KeyEvent.KEYCODE_ENTER && 
                keyCode != KeyEvent.KEYCODE_DPAD_LEFT &&
                keyCode != KeyEvent.KEYCODE_DPAD_RIGHT &&
                keyCode != KeyEvent.KEYCODE_MENU) {
            if (keyEvent.isPrintingKey() && action == KeyEvent.ACTION_UP) {
                // Tell the handler that text was updated.
                mHandler.onTextChanged();
            }
            return false;
        }

        
       /*We should act on KeyEvent.ACTION_DOWN, but strangely
       sometimes the DOWN event isn't received, only the UP.
       So the workaround is to act on UP...
       http://b/issue?id=1022478
      */         
		if (action == KeyEvent.ACTION_UP) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
				mEqualBtn.performClick();
				mEqualBtn.setSelected(false) ;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				mMulBtn.performClick();
				mMulBtn.setSelected(false) ;
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				mAddBtn.performClick();
				mAddBtn.setSelected(false) ;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				mSubBtn.performClick();
				mSubBtn.setSelected(false) ;
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				mDivBtn.performClick();
				mDivBtn.setSelected(false) ;
				break;
			case KeyEvent.KEYCODE_MENU:
				if(mCount > 0){
					mClearBtn.performLongClick() ;
				}else{
					mClearBtn.performClick();
				}
				mClearBtn.setSelected(false) ;
				break ;
			default:
				break;
			}
			return true ;
		}else if (action == KeyEvent.ACTION_DOWN) {
			mCount = keyEvent.getRepeatCount() ;
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
				mEqualBtn.setSelected(true) ;
				break ;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				mMulBtn.setSelected(true) ;
				break ;
			case KeyEvent.KEYCODE_DPAD_UP:
				mAddBtn.setSelected(true) ;
				break ;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				mSubBtn.setSelected(true) ;
				break ;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				mDivBtn.setSelected(true) ;
				break ;
			case KeyEvent.KEYCODE_MENU:
				mClearBtn.setSelected(true) ;
				break ;
			default:
				break;
			}
		}
        
        return true;
    }

	public Button getmAddBtn() {
		return mAddBtn;
	}

	public void setmAddBtn(Button mAddBtn) {
		this.mAddBtn = mAddBtn;
	}

	public Button getmEqualBtn() {
		return mEqualBtn;
	}

	public void setmEqualBtn(Button mEqualBtn) {
		this.mEqualBtn = mEqualBtn;
	}

	public Button getmMulBtn() {
		return mMulBtn;
	}

	public void setmMulBtn(Button mMulBtn) {
		this.mMulBtn = mMulBtn;
	}

	public Button getmDivBtn() {
		return mDivBtn;
	}

	public void setmDivBtn(Button mDivBtn) {
		this.mDivBtn = mDivBtn;
	}

	public Button getmSubBtn() {
		return mSubBtn;
	}

	public void setmSubBtn(Button mSubBtn) {
		this.mSubBtn = mSubBtn;
	}

	public Button getmClearBtn() {
		return mClearBtn;
	}

	public void setmClearBtn(Button mClearBtn) {
		this.mClearBtn = mClearBtn;
	}
}
