package com.randymcollier.basin;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Product extends Activity {
	
	ImageButton btn_down, btn_up;
	ImageView image;
	
	static int min_drawable = 0x7f020000;
	static int max_drawable = 0x7f020025;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		
		btn_down = (ImageButton) findViewById(R.id.btn_down_vote);
		btn_up = (ImageButton) findViewById(R.id.btn_up_vote);
		image = (ImageView) findViewById(R.id.product_image);
		
		setImage();
		
		setOnClickListeners();
	}
	
	private void setImage() {
		image.setImageResource(min_drawable + (int)(Math.random() * ((max_drawable - min_drawable) + 1)));
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
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

}
