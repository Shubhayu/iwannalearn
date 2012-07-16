package com.iwannalearn.progresstracker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgressTracker extends RelativeLayout {

	private final String TAG = "ProgressTracker";
	private int mBarId;
	private int mDescriptionId;
	private int mButton1Id;
	private int mStatusId;
	
	private ProgressBar mBar = null;
	private TextView mDescription = null;
	private Button mButton1 = null;
	private ImageView mStatus = null;
	
	/*
	 * Interface to keep track of the ProgressTracker's status 
	 */
	public interface ProgressTrackerListener{
		
		public void onProgressStopped(ProgressStatus status);
		
	}
	
	private ProgressTrackerListener listener = null;
	
	/*
	 * Enum to define the different states of the ProgressTracker
	 */
	public enum ProgressStatus{
		NOTSTARTED,
		INPROGRESS,
		SUCCESS,
		FAILED
	}
	
	private ProgressStatus mProgressStatus;
	
	public ProgressTracker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressTracker, defStyle, 0);

		//Check for the validity of the non-optional items
		int barId = a.getResourceId(R.styleable.ProgressTracker_progressBar, 0);
		if( barId == 0 ){
			throw new IllegalArgumentException("The progressBar attribute is required and must refer "
                    + "to a valid child.");
		}
		
		int button1Id = a.getResourceId(R.styleable.ProgressTracker_progressActionButton1, 0);
		if( button1Id == 0 ){
			throw new IllegalArgumentException("The progressActionButton1 attribute is required and must refer "
                    + "to a valid child.");
		}
	
		mBarId = barId;
		mButton1Id = button1Id;
		//We don't check for the description and the status image as these are optional
		mDescriptionId = a.getResourceId(R.styleable.ProgressTracker_progressTaskDescription, 0);
		mStatusId = a.getResourceId(R.styleable.ProgressTracker_progressStatusImage, 0);
		
		//Don't forget to call recycle()
		a.recycle();
		
		resetProgressTracker();
	}

	public ProgressTracker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/*
	 * Overriding this so that at the end of the inflation, the view objects can
	 * be referenced and used in the rest of the code
	 * @see android.view.View#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		mBar = (ProgressBar) findViewById(mBarId);
		if (mBar == null) {
            throw new IllegalArgumentException("The progressBar attribute must refer to an"
                    + " existing child.");
        }
		
		mButton1 = (Button) findViewById(mButton1Id);
		if (mButton1 == null) {
            throw new IllegalArgumentException("The progressActionButton1 attribute must refer to an"
                    + " existing child.");
        }
		
		//Extra bit of check to see whether the item is present in the layout at all
		if( mDescriptionId != 0 ){
			mDescription = (TextView) findViewById(mDescriptionId);
			if (mDescription == null) {
	            throw new IllegalArgumentException("The progressTaskDescription attribute must refer to an"
	                    + " existing child.");
	        }
		}
		
		if( mStatusId != 0 ){
			mStatus = (ImageView) findViewById(mStatusId);
			if (mStatus == null) {
	            throw new IllegalArgumentException("The progressStatusImage attribute must refer to an"
	                    + " existing child.");
	        }
		}
	}
	
	/**** Delegate methods ****/
	/*
	 * Set the text of the TextView which displays the description of 
	 * the ProgressTracker
	 */
	public void setText(String text) {
		if( mDescription != null )
			mDescription.setText(text);
	}

	/*
	 * Returns the currently set max value of the ProgressBar
	 */
	public int getMax() {
		if( mBar != null )
			return mBar.getMax();
		else
			return 0;
	}

	/*
	 * Returns the current progress of the Tracker
	 */
	public int getProgress() {
		if( mBar != null )
			return mBar.getProgress();
		else
			return 0;
	}

	/*
	 * Increment by a specific delta
	 */
	public final void incrementProgressBy(int diff) {
		if( mBar != null ){
			
			int currProgress = mBar.getProgress();
			int max = mBar.getMax();
			mBar.incrementProgressBy(diff);
			
			//find out whether the progress is complete or not and 
			//accordingly set the status
			if( (currProgress + diff) < max)
				setProgressStatus(ProgressStatus.INPROGRESS);
			else{
				setProgressStatus(ProgressStatus.SUCCESS);
//				if( listener != null )
//					listener.onProgressStopped(mProgressStatus);
			}
		}
	}

	/*
	 * Set the Max level of the ProgressBar
	 */
	public void setMax(int max) {
		resetProgressTracker();
		if( mBar != null )
			mBar.setMax(max);
	}

	/*
	 * Set the current progress of the ProgressTracker
	 */
	public void setProgress(int progress) {
		if( mBar != null ){
			int max = mBar.getMax();
			mBar.setProgress(progress);
			
			//find out whether the progress is complete or not and 
			//accordingly set the status
			if( progress < max )
				setProgressStatus(ProgressStatus.INPROGRESS);			
			else{
				setProgressStatus(ProgressStatus.SUCCESS);
//				if( listener != null )
//					listener.onProgressStopped(mProgressStatus);
			}
		}
	}
	
	/*
	 * Set the text of the Button included in the ProgressTracker
	 */
	public void setButtontext(String str) {
		if( mButton1 != null )
			mButton1.setText(str);
	}
	
	/*
	 * This function is used to notify the ProgressTracker that there
	 * has been some error from its source, ie, the component that
	 * has been sending it the progress update. When the ProgressTracker
	 * receives this notification, it sets its status to FAILED. The 
	 * component listening to the ProgressTracker gets intimated about the
	 * error.
	 */
	public void notifyError(String errmsg){
		setProgressStatus(ProgressStatus.FAILED);
		Log.i(TAG, errmsg);
		
	}
	
	/*
	 * Returns the current status of the ProgressTracker
	 */
	public ProgressStatus getProgressStatus(){
		return mProgressStatus;
	}
	
	public void setOnProgressTrackerListener( ProgressTrackerListener listener ){
		if( listener != null )
			this.listener = listener;
	}
	
	/*
	 * This resets the ProgressTracker
	 */
	public void resetProgressTracker(){
		setProgressStatus(ProgressStatus.NOTSTARTED);
		this.setVisibility(VISIBLE);
	}

	/*
	 * This sets the onClickListener of the Button so that the Button click
	 * can be handled by the user as per requirement
	 */
	public void setActionButtonOnClickListener(OnClickListener l) {
		if( mButton1 != null )
			mButton1.setOnClickListener(l);
	}
	
	/*
	 * The status of the ProgressTracker is set here and if the
	 * ImageView is specified in the layout then, the drawable
	 * as specified by the level-list is displayed at the 
	 * corresponding status 
	 */
	private void setProgressStatus( ProgressStatus status ){
		if( mProgressStatus == status )
			return;
		mProgressStatus = status;
		if( mStatus != null ){
			switch(status){
			case NOTSTARTED:
				mStatus.setImageLevel(0);
				break;
			case INPROGRESS:
				mStatus.setImageLevel(1);
				break;
			case SUCCESS:
				mStatus.setImageLevel(2);
				break;
			case FAILED:
				mStatus.setImageLevel(3);
				break;
			}
		}
		if( listener != null )
			listener.onProgressStopped(mProgressStatus);
	}
}
