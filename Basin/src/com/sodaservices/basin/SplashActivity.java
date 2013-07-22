package com.sodaservices.basin;

import com.mmdock.basin.GifWebView;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;
import com.sodaservices.basin.R;

/**
 * @author Morgan Dock
 * 
 * This class will display a splash screen that will appear for a developer/company logo on start up.
 */
public class SplashActivity extends Activity {

	private Handler handler;
	private Thread thread;
	//gif as Movie: 
	//GifMovieView view;
	GifWebView view;
	static int count;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		count = 0;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//Movie gif:
		//view = view.initGifMovieView(this, "splashlogo.gif");
		//setContentView(view);
		
		//Web gif:
		view = new GifWebView(this, "file:///android_asset/splashlogo.gif");
		setContentView(view);
		//setContentView(R.layout.splashlogo);  //This is needed, but splashlogo needs to be added. This is for a simple image splash logo.
		handler = new Handler();
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);//Wait 3 seconds
					handler.post(new Runnable() {
						public void run() {
							goToNextScreen();
						}
					});
				} catch (InterruptedException e) {
				}
			}
		};

		thread.start();
	}

	/**
	 * Proceed to the Menu
	 */
	protected void goToNextScreen() {
<<<<<<< HEAD:Basin/src/com/randymcollier/basin/SplashActivity.java
		Intent intent = new Intent(this, com.randymcollier.basin.MainActivity.class);
		count++;
		startActivityForResult(intent, 1);
=======
		Intent intent = new Intent(this, com.sodaservices.basin.MainActivity.class);
		startActivity(intent);
>>>>>>> 3a6a0f649ea6f9556437867da70f82666c8ad42f:Basin/src/com/sodaservices/basin/SplashActivity.java
	}
	
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		//showToast("count is " + count);
//	    super.onActivityResult(requestCode, resultCode, data);
//	    if (count >= 1)
//	    	this.finish();
//	    count++;
//	    //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//	}

}