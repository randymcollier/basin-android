package com.randymcollier.basin;

import android.OnSwipeTouchListener.OnSwipeTouchListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class Product extends Activity {
	
	private ImageButton btn_down, btn_up;
	private ImageView image;
	private RelativeLayout layout;
	
	static final int MIN_DRAWABLE = 0x7f020021;
	static final int MAX_DRAWABLE = 0x7f02004b;
	
	int current_drawable;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		
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
		    	showToast("You like this item.");
		    	TranslateAnimation anim = new TranslateAnimation(-1000, 0, 0, 0);
				anim.setDuration(50);
				anim.setFillAfter(true);
				image.startAnimation(anim);
				setImage();
		    }
		    public void onSwipeLeft() {
		    	showToast("You don't like this item.");
		    	TranslateAnimation anim = new TranslateAnimation(1000, 0, 0, 0);
				anim.setDuration(50);
				anim.setFillAfter(true);
				image.startAnimation(anim);
				setImage();
		    }
		    public void onSwipeBottom() {
		    }
		    public void onSwipeTop() {
		    }
		});
		
	}

	private void setImage() {
		current_drawable = MIN_DRAWABLE + (int)(Math.random() * ((MAX_DRAWABLE - MIN_DRAWABLE) + 1));
		image.setImageResource(current_drawable);
	}

	private void setOnClickListeners() {
		btn_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToast("You don't like this item.");
				setImage();
			}
			
		});
		
		btn_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToast("You like this item.");
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

}
