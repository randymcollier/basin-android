/**
 * Copyright (C) 2013 Randy Collier
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The author can be contacted via email at randymcollier@gmail.com
 */

package com.randymcollier.basin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ChannelsFragment extends Fragment {
	
	/**
	 * Used to set the correct View for the Fragment.
	 */
	View view;
	
	/**
	 * Used to hold the Channel names. This String array is
	 * applied to the ListView.
	 */
	String[] names;
	
	/**
	 * Used to apply the String array to the ListView.
	 */
	ArrayAdapter<String> myAdapter;
	
	/**
	 * Used to display the available Channels.
	 */
	ListView lv;
	
	/**
	 * An instance of the MainActivity used to statically access its members.
	 */
	MainActivity main = new MainActivity();
	
	/**
	 * String containing the name of the Shared Preference.
	 */
	private static final String PREFS_NAME = "user_conditions";
	
	/**
	 * Overridden method that is called when the Fragment is created.
	 * <p>
	 * This method prepares the Fragment for use by retrieving the Channels
	 * from the server and displaying them in the ListView. Then, the
	 * onClickListener is applied to the ListView. Depending on which Channel
	 * is selected, a value is placed in the Shared Preference to let the
	 * ProductFragment know which Channel is now active.
	 * @param inflater	The LayoutInflater object that can be used to inflate
	 * any views in the fragment,
	 * @param container	If non-null, this is the parent view that the
	 * fragment's UI should be attached to. The fragment should not add the
	 * view itself, but this can be used to generate the LayoutParams of the
	 * view.
	 * @param savedInstanceState	If non-null, this fragment is being
	 * re-constructed from a previous saved state as given here.
	 * @return the view for the Fragment's UI
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.channels, container, false);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
        
        names = getChannels();
        ArrayList<String> channels = new ArrayList<String>();
		channels.addAll(Arrays.asList(names));
		myAdapter = new ArrayAdapter<String>(getActivity(), R.layout.one_row, channels);
			
		lv = (ListView) view.findViewById(R.id.list_channels);
		lv.setAdapter(myAdapter);	
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 1);
	        SharedPreferences.Editor editor = settings.edit();
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				switch (pos + 1) { 
				  case MainActivity.TOPS:
					  editor.putInt("channel", MainActivity.TOPS);
					  break;
				  case MainActivity.BOTTOMS:
					  editor.putInt("channel", MainActivity.BOTTOMS);
					  break;
				  case MainActivity.DRESSES:
					  editor.putInt("channel", MainActivity.DRESSES);
					  break;
				  case MainActivity.SUNGLASSES:
					  editor.putInt("channel", MainActivity.SUNGLASSES);
					  break;
				  case MainActivity.SHOES:
					  editor.putInt("channel", MainActivity.SHOES);
					  break;
				  case MainActivity.JEWELRY:
					  editor.putInt("channel", MainActivity.JEWELRY);
					  break;
				  case MainActivity.BAGS:
					  editor.putInt("channel", MainActivity.BAGS);
					  break;
				  case MainActivity.SWIMWEAR:
					  editor.putInt("channel", MainActivity.SWIMWEAR);
					  break;
				  default:
					  showToast("Error. Please try again.");
					  break;
				}
				editor.commit();
				ProductFragment fragment = (ProductFragment) getFragmentManager().findFragmentById(R.id.selectionFragment);
                if (fragment != null) {
                    fragment.getData();
                }
                getActivity().getSupportFragmentManager().popBackStack();   
			}
		});
        
        return view;
    }

	/**
	 * Makes an HTTP request to retrieve the Channels from the database.
	 * The results are returned to the application in JSON, then parsed and
	 * returned as an array.
	 * @return	String[] of Channels.
	 */
	private String[] getChannels() {
		InputStream is = null;
    	StringBuilder sb = new StringBuilder();
    	String result = new String();
    	try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/getChannels.php");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection" + e.toString());
        }
    	
    	//convert response to string
        try{
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            sb = new StringBuilder();
            sb.append(reader.readLine() + "\n");

            String line="0";
            while ((line = reader.readLine()) != null) {
                 sb.append(line + "\n");
             }
             is.close();
             result = sb.toString();
             }catch(Exception e){
                   Log.e("log_tag", "Error converting result "+e.toString());
           }
        //parse JSON
        JSONArray jArray = new JSONArray();
        try {
			jArray = new JSONArray(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        String[] array = new String[jArray.length()];
        try{
            
            
            JSONObject json_data = new JSONObject();
            for(int i=0;i<jArray.length();i++){
                   json_data = jArray.getJSONObject(i);
                   array[i] = json_data.getString("Type");//"Type" is the column name in database
            }
        }
        catch(JSONException e1){
             Toast.makeText(getActivity(), "No Data Found" ,Toast.LENGTH_LONG).show();
        } catch (ParseException e1) {
        	e1.printStackTrace();
       }
        
        return array;
		
	}
	
	/**
	 * Displays a toast message.
	 * @param message	String message to be displayed.
	 */
	public void showToast(String message) {
	    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

}
