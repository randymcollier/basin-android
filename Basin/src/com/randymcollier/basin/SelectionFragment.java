package com.randymcollier.basin;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SelectionFragment extends Fragment {
	
	private static final String TAG = "SelectionFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.selection, 
	            container, false);
	    return view;
	}

}
