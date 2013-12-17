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
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Like extends Activity {
	
	/**
	 * Used to set the correct View for the Fragment.
	 */
	View view;
	
	/**
	 * String containing the name of the Shared Preference.
	 */
	private static final String PREFS_NAME = "user_conditions";
	
	/**
	 * Integer value used to determine which Channel was selected.
	 */
	private int productType;
	
	/**
	 * Used to apply the String array to the ListView.
	 */
	ArrayAdapter<String> myAdapter;
	
	/**
	 * Used to display the items that were liked..
	 */
	ListView lv;
	
	/**
	 * Used to hold the descriptions of each liked product. This array is
	 * applied to the ListView.
	 */
	String[] descriptions;
	
	/**
	 * Used to hold the productID of each liked product.
	 */
	String[] productIds;
	
	/**
	 * Overridden method that is called when the Activity is created.
	 * <p>
	 * This method prepares the Activity for use by retrieving the Liked
	 * products from the server then displaying them in the ListView.
	 * @param savedInstanceState	If the activity is being re-initialized
	 * after previously being shut down then this Bundle contains the data
	 * it most recently supplied in onSaveInstanceState(Bundle). Note:
	 * Otherwise it is null.
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.like);
        
        // Used to temporarily allow for network requests to happen on the main thread.
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
        
        getUserLikes();
        
        ArrayList<String> channels = new ArrayList<String>();
		channels.addAll(Arrays.asList(descriptions));
		myAdapter = new ArrayAdapter<String>(this, R.layout.one_row, channels);
			
		lv = (ListView) findViewById(R.id.list_like);
		lv.setAdapter(myAdapter);	
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			}
		});
    }
	
	/**
	 * Retrieves the Liked products from the server. The productType (Channel)
	 * is retrieved from the Bundle sent over from UserLikes.java. An HTTP
	 * request is made that retrieves the Liked products specific to the
	 * appropriate Channel. The results are returned to the application
	 * in JSON, parsed, and set to the appropriate String array.
	 */
	private void getUserLikes() {
		Bundle data = getIntent().getExtras();
		productType = data.getInt("productType");
		InputStream is = null;
    	StringBuilder sb = new StringBuilder();
    	String result = new String();
	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 1);
	    String userId = settings.getString("userId", "0");
	    nameValuePairs.add(new BasicNameValuePair("userId", userId));
	    nameValuePairs.add(new BasicNameValuePair("productType", Integer.toString(productType)));
    	try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/getUserLikes.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
        String[] arrayProductId = new String[jArray.length()];
        String[] arrayDescription = new String[jArray.length()];
        try{
            
            
            JSONObject json_data = new JSONObject();
            for(int i=0;i<jArray.length();i++){
                   json_data = jArray.getJSONObject(i);
                   arrayDescription[i] = json_data.getString("Description");//"Type" is the column name in database
                   arrayProductId[i] = json_data.getString("ProductId");
            }
        }
        catch(JSONException e1){
             showToast("No Data Found");
        } catch (ParseException e1) {
        	e1.printStackTrace();
       }
        descriptions = arrayDescription;
        productIds = arrayProductId;
	}
	
	/**
	 * Displays a toast message.
	 * @param message	String message to be displayed.
	 */
	public void showToast(String message) {
	    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
