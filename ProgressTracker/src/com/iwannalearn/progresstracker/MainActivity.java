package com.iwannalearn.progresstracker;

import java.util.Timer;
import java.util.TimerTask;

import com.iwannalearn.progresstracker.ProgressTracker.ProgressStatus;
import com.iwannalearn.progresstracker.ProgressTracker.ProgressTrackerListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private final String TAG = "MainActivity";
	ProgressTracker progress = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		progress = (ProgressTracker) findViewById(R.id.progress_tracker);
		progress.setText("Calculating your Awesomeness...");
		progress.setButtontext("Hide");

		progress.setActionButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progress.setVisibility(View.GONE);
			}
		});

		progress.setMax(100);
		progress.setOnProgressTrackerListener(ptListener);

		Button successBtn = (Button) findViewById(R.id.btn_example1);
		Button unsuccessBtn = (Button) findViewById(R.id.btn_example2);

		successBtn.setOnClickListener(successBtnListener);
		unsuccessBtn.setOnClickListener(unsuccessBtnListener);
		
		final ProgressTracker progress_minimal = (ProgressTracker) findViewById(R.id.progress_tracker_minimal);
		progress_minimal.setButtontext("Start");
		progress_minimal.setActionButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runProgressTracker(progress_minimal, true);
			}
		});
		progress_minimal.setMax(100);
		progress_minimal.setOnProgressTrackerListener(ptListener);
		
		final ProgressTracker progress_rearranged = (ProgressTracker) findViewById(R.id.progress_tracker_rearranged);
		progress_rearranged.setMax(100);
		progress_rearranged.setButtontext("Start");
		progress_rearranged.setActionButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runProgressTracker(progress_rearranged, false);
			}
		});
		progress_rearranged.setOnProgressTrackerListener(new ProgressTrackerListener() {
			
			@Override
			public void onProgressStopped(ProgressStatus status) {
				switch (status) {
				case SUCCESS:
					progress_rearranged.setText("The Task was successfull!");
					incrementProgress.cancel();
					timer.purge();
					break;
				case FAILED:
					progress_rearranged.setText("The Task failed!");
					timer.purge();
					break;
				case INPROGRESS:
					progress_rearranged.setText("Calculating your Awesomeness!!!");
					break;
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private View.OnClickListener successBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			progress.resetProgressTracker();
			runProgressTracker(progress, true);
		}
	};

	private View.OnClickListener unsuccessBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			progress.resetProgressTracker();
			runProgressTracker(progress, false);
		}
	};

	TimerTask incrementProgress;
	Timer timer = new Timer();
	int prog = 0;

	private void runProgressTracker(final ProgressTracker progressTracker, final boolean successRun) {

		prog = 0;
		incrementProgress = new TimerTask() {

			@Override
			public void run() {
				if (!successRun && prog >= 65) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							progressTracker.notifyError("Oh No! The Timer stopped at 65!");				
						}
					});
					cancel();
				}else{
					prog += 5;
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							progressTracker.setProgress(prog);						
						}
					});
				}				
			}
		};

		timer.schedule(incrementProgress, 100, 100);
	}

	private ProgressTrackerListener ptListener = new ProgressTrackerListener() {

		@Override
		public void onProgressStopped(ProgressStatus status) {
			switch (status) {
			case SUCCESS:
				Log.i(TAG, "The Task was successfull!");
				incrementProgress.cancel();
				timer.purge();
				break;
			case FAILED:
				Log.i(TAG, "The Task failed!");
				timer.purge();
				break;
			}
		}
	};
}
