package com.randymcollier.basin;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.OnSwipeTouchListener.OnSwipeTouchListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Product extends Activity {
	
	private Button btn_pass;
	private ImageView image;
	private RelativeLayout layout;
	
	static final int MIN_DRAWABLE = 0x7f020021;
	static final int MAX_DRAWABLE = 0x7f02004b;
	
	int current_drawable;
	
	DBAdapter db;
	
	//Cameron's house
	//static final String URL = "http://192.168.0.20/basin/images/";	
	
	//Randy's house
	//static final String URL = "http://192.168.1.3/basin/images/";
	
	//New domain
	static final String URL = "http://www.sodaservices.com/basin/images/0/0/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		
		db = new DBAdapter(this);
		db.open();
		
		//allows for the network connection to be performed in the main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		setComponents();
		
		setImage();
		
		setOnClickListeners();
		
		setOnTouchListener();
		
		current_drawable = 0;
	}
	
	private void setComponents() {
		btn_pass = (Button) findViewById(R.id.btn_pass);
		image = (ImageView) findViewById(R.id.product_image);
		layout = (RelativeLayout) findViewById(R.id.product_layout);		
	}

	private void setOnTouchListener() {
		layout.setOnTouchListener(new OnSwipeTouchListener() {
		    public void onSwipeRight() {
		    	showToast("Like");
		    	if (!db.isAdded(String.valueOf(current_drawable))) {
		    		db.addOpinion("like", String.valueOf(current_drawable));
		    	}
//		    	TranslateAnimation anim = new TranslateAnimation(-1000, 0, 0, 0);
//				anim.setDuration(50);
//				anim.setFillAfter(true);
//				image.startAnimation(anim);
				setImage();
		    }
		    public void onSwipeLeft() {
		    	showToast("Dislike");
		    	if (!db.isAdded(String.valueOf(current_drawable))) {
		    		db.addOpinion("dislike", String.valueOf(current_drawable));
		    	}
//		    	TranslateAnimation anim = new TranslateAnimation(1000, 0, 0, 0);
//				anim.setDuration(50);
//				anim.setFillAfter(true);
//				image.startAnimation(anim);
				setImage();
		    }
		    public void onSwipeBottom() {
		    	showToast("Pass");
		    	setImage();
		    }
		    public void onSwipeTop() {
		    	showToast("Pass");
		    	setImage();
		    }
		});
		
	}

	private void setImage() {
		//current_drawable = MIN_DRAWABLE + (int)(Math.random() * ((MAX_DRAWABLE - MIN_DRAWABLE) + 1));
		//current_drawable = 1 + (int) (Math.random() * ((107 - 1) + 1));
		current_drawable = (current_drawable + 3) % 107;
		if (current_drawable == 6)
			current_drawable++;
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
		btn_pass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToast("Pass");
				setImage();
			}
			
		});
	}

	public void showToast(String message) {
		final Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		toast.show();
		Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               toast.cancel(); 
           }
        }, 500);
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
	                    //conn.setConnectTimeout(5000);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater=getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId())
	    {
	    case R.id.profile_menu:
	    	Intent i = new Intent("com.randymcollier.basin.Profile");
	    	startActivity(i);
	        break;
	    }
	    return true;
	}
}
