package com.sodaservices.basin;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.sodaservices.basin.R;

public class Profile extends Activity {

	DBAdapter db;
	ArrayAdapter<String> myAdapter;
	String[] images, opinions;
	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		db = new DBAdapter(this);
		db.open();
		
		Cursor c = db.getAllOpinions();
		images = new String[c.getCount()];
		opinions = new String[c.getCount()];
		
		for (int pos=0; pos<c.getCount(); pos++) {
	        c.moveToPosition(pos);
	        images[pos] = c.getString(2);
	        opinions[pos] = c.getString(1);
		}
		
		ArrayList<String> drugNames = new ArrayList<String>();
		drugNames.addAll(Arrays.asList(images));
		myAdapter = new ArrayAdapter<String>(this, R.layout.one_row, drugNames);
			
		lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(myAdapter);	
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				showToast(opinions[pos]);
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	
	public void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

}
