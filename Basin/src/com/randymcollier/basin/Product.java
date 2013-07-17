package com.randymcollier.basin;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.OnSwipeTouchListener.OnSwipeTouchListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.net.URL;
import java.net.URLConnection;

public class Product<ProfileGridView> extends Activity {
	
	private ImageButton btn_down, btn_up;
	private ImageView image;
	private RelativeLayout layout;
	
	static final int MIN_DRAWABLE = 0x7f020021;
	static final int MAX_DRAWABLE = 0x7f02004b;
	
	int current_drawable;
	
	//Cameron's house
	//static final String URL = "http://192.168.0.20/basin/images/";	
	
	//Randy's house
	//static final String URL = "http://192.168.1.3/basin/images/";
	
	//New domain
	static final String URL = "http://www.sodaservices.com/basin/images/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		
		//allows for the network connection to be performed in the main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		setComponents();
		
		setImage();
		
		setOnClickListeners();
		
		setOnTouchListener();
	}
	
	private void setComponents() {
		btn_down = (ImageButton) findViewById(R.id.btn_down_vote);
		btn_up = (ImageButton) findViewById(R.id.btn_up_vote);
		image = (ImageView) findViewById(R.id.product_image);
		layout = (RelativeLayout) findViewById(R.id.product_layout);		
	}

	private void setOnTouchListener() {
		layout.setOnTouchListener(new OnSwipeTouchListener() {
		    public void onSwipeRight() {
		    	//showToast("You like this item.");
//		    	TranslateAnimation anim = new TranslateAnimation(-1000, 0, 0, 0);
//				anim.setDuration(50);
//				anim.setFillAfter(true);
//				image.startAnimation(anim);
				setImage();
		    }
		    public void onSwipeLeft() {
		    	//showToast("You don't like this item.");
//		    	TranslateAnimation anim = new TranslateAnimation(1000, 0, 0, 0);
//				anim.setDuration(50);
//				anim.setFillAfter(true);
//				image.startAnimation(anim);
				setImage();
		    }
		    public void onSwipeBottom() {
		    }
		    public void onSwipeTop() {
		    }
		});
		
	}

	private void setImage() {
		//current_drawable = MIN_DRAWABLE + (int)(Math.random() * ((MAX_DRAWABLE - MIN_DRAWABLE) + 1));
		current_drawable = 1 + (int) (Math.random() * ((107 - 1) + 1));
		try {
			//Bitmap bitmap = BitmapImageLoader.loadBitmap(URL);
			//image.setImageResource(current_drawable);
			//image.setImageBitmap(bitmap);
			
			//async function to set image
			setImageURL(URL + current_drawable);
			
			//sets image to bitmap retrieved from url
//			URL newurl = new URL(URL + current_drawable);
//			Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
//			image.setImageBitmap(mIcon_val);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	private void setOnClickListeners() {
		btn_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//showToast("You don't like this item.");
				setImage();
			}
			
		});
		
		btn_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//showToast("You like this item.");
				setImage();
			}
			
		});
	}

	public void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.profile:
	        	Intent i = new Intent("com.randymcollier.basin.Profile");
	            startActivity(i);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	//async setting the bitmap
	public void setImageURL(final String url) {
		final ProgressDialog dialog = new ProgressDialog(Product.this);
	        new AsyncTask<Void, Void, Bitmap>() {
	            protected Bitmap doInBackground(Void... p) {
	                Bitmap bm = null;
	                try {
	                    URL aURL = new URL(url);
	                    URLConnection conn = aURL.openConnection();
	                    conn.setUseCaches(true);
	                    conn.connect();
	                    InputStream is = conn.getInputStream();
	                    BufferedInputStream bis = new BufferedInputStream(is);
	                    bm = BitmapFactory.decodeStream(bis);
	                    bis.close();
	                    is.close();
	                } catch(IOException e) {
	                    e.printStackTrace();
	                }

	                if(bm == null) {
	                    return null;
	                }
	                return bm;
	            }
	            
	            protected void onPreExecute() {
	            	dialog.setMessage("Loading image...");
	                dialog.show();
	            }

	            protected void onPostExecute(Bitmap bmp) {
	                if(bmp == null) {
	                    return;
	                }
	                dialog.dismiss();
	                try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	                setImageBitmap(bmp);
	            }
	        }.execute();
	}

	private void setImageBitmap(Bitmap bitmap) {
		image.setImageBitmap(bitmap);
	}

}
