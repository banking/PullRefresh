package com.banking.pullrefresh;




import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.banking.pullrefresh.PullRefreshLayout.OnPullListener;
import com.banking.pullrefresh.PullRefreshLayout.OnPullStateListener;



public class PullRefreshActivity extends Activity implements OnPullListener, OnPullStateListener{

	private final String TAG = "PullRefreshActivity";
	
	private Animation mRotateUpAnimation;
	private Animation mRotateDownAnimation;
	private boolean mInLoading = false;
	private PullRefreshLayout mPullLayout;
	private TextView mActionText;
	private TextView mTimeText;
	private View mProgress;
	private View mActionImage;
	private Handler mHandler;
	private TextView contentTv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        
        initPullRefreshView();
        dataLoadFinished();
    }
    
    
    /**
	 * 下拉刷新view初始化
	 */
	private void initPullRefreshView() {
		mHandler = new Handler();
		mRotateUpAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_down);

		mInLoading = true;
		contentTv = (TextView)findViewById(R.id.content_tv);
		mPullLayout = (PullRefreshLayout) findViewById(R.id.pull_container);
		mPullLayout.setOnActionPullListener(this);
		mPullLayout.setOnPullStateChangeListener(this);
		mPullLayout.setEnableStopInActionView(true);
		mProgress = findViewById(android.R.id.progress);
		mProgress.setVisibility(View.VISIBLE);
		mActionImage = findViewById(android.R.id.icon);
		mActionImage.setVisibility(View.GONE);
		mActionText = (TextView) findViewById(R.id.pull_note);
		mActionText.setText("下拉刷新");
		contentTv.setText("初始数据.");
		mTimeText = (TextView) findViewById(R.id.refresh_time);
		//		mTimeText.setText(R.string.note_not_update);
	}


	@Override
	public void onPullOut() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_refresh);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateUpAnimation);
		}
	}


	@Override
	public void onPullIn() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_down);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateDownAnimation);
		}
	}


	@Override
	public void onSnapToTop() {// 下拉后，弹到顶部时，开始刷新数据
		if (!mInLoading) {
			mInLoading = true;
			mPullLayout.setEnableStopInActionView(true);
			mActionImage.clearAnimation();
			mActionImage.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
			mActionText.setText(R.string.note_pull_loading);

			pullRefreshData();
		}
	}


	@Override
	public void onShow() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onHide() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 当当前页面其他元素加载完成后调用该方法
	 */
	private void dataLoadFinished() {
		Log.d(TAG, " dataLoaded -->> mInLoading=" + mInLoading);
		if (mInLoading) {
			mInLoading = false;
			mPullLayout.setEnableStopInActionView(false);
			mPullLayout.hideActionView();
			mActionImage.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);

			if (mPullLayout.isPullOut()) {
				mActionText.setText(R.string.note_pull_refresh);
				mActionImage.clearAnimation();
				mActionImage.startAnimation(mRotateUpAnimation);
			} else {
				mActionText.setText(R.string.note_pull_down);
			}

			mTimeText.setText(getString(R.string.note_update_at, formatDate(new Date(System.currentTimeMillis()))));
		}
	}
	
	/**
	 * 这里加入相关网络请求
	 */
	private void pullRefreshData() {

		new Thread() {
			public void run() {
				Log.d("PullRefreshActivity","pullRefreshData-->data fresh");
				//TODO  网络请求
				//还原控件，更新页面
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						contentTv.setText(""+formatDate(new Date(System.currentTimeMillis()))+"时刻数据");
						dataLoadFinished();
					}
				});
				
			};
		}.start();
	}
	
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Date parseDate(String dateStr) throws ParseException {
		try {
			return dateFormat.parse(dateStr);
		} catch (ParseException e) {
			throw e;
		}
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}
}
