package com.mmdock.basin;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;

/**
 * @author Morgan Dock
 * 
 * This class will display a splash screen that will appear for a developer/company logo on start up.
 * 
 * Added code to account for if your logo or start up splash is a gif rather than an image.  GifMovieView will play the gif after content view is set.
 * As GifMovieView: Movie-class is not able to deal with every type of animated GIFs. 
 * 			For some formats, the first frame will be drawn well but every other won’t. So when you walk this route, make sure your GIFs are displayed correctly.
 * As GifWebView: More reliable Because WebKit is already implemented native, it’s memory footprint is really low, especially when compared to a bitmap decoding method. 
 * 			Operations on images like scaling can be performed simply by using HTML and code is small. Has a smaller target user group since webview is best for Android 2.2+(which soon will
 * 			be everyone anyway.
 */
public class SplashActivity extends Activity {

	private Handler handler;
	private Thread thread;
	//gif as Movie: 
	//GifMovieView view;
	GifWebView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
					Thread.sleep(5000);//Wait 5 seconds
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
		Intent intent = new Intent(this, com.randymcollier.basin.MainActivity.class);
		startActivity(intent);
	}

}
