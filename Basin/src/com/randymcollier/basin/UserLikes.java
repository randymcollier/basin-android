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

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UserLikes extends FragmentActivity {
	
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
	 * Integer value used to determine which Channel was selected.
	 */
	private int productType;
	
	/**
	 * Overridden method that is called when the Fragment is created.
	 * <p>
	 * This method prepares the Fragment for use by retrieving the Channels
	 * from the server and displaying them in the ListView. Then, the
	 * onClickListener is applied to the ListView. When a Channel is selected,
	 * productType is set to the appropriate value. productType is then passed
	 * in a Bundle to Like.java.
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_likes);
		
		// Used to temporarily allow for network requests to happen on the main thread.
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
        
        names = getChannels();
        ArrayList<String> channels = new ArrayList<String>();
		channels.addAll(Arrays.asList(names));
		myAdapter = new ArrayAdapter<String>(this, R.layout.one_row, channels);
			
		lv = (ListView) findViewById(R.id.list_user_likes);
		lv.setAdapter(myAdapter);	
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				switch (pos + 1) { 
				  case MainActivity.TOPS:
					  productType = MainActivity.TOPS;
					  break;
				  case MainActivity.BOTTOMS:
					  productType = MainActivity.BOTTOMS;
					  break;
				  case MainActivity.DRESSES:
					  productType = MainActivity.DRESSES;
					  break;
				  case MainActivity.SUNGLASSES:
					  productType = MainActivity.SUNGLASSES;
					  break;
				  case MainActivity.SHOES:
					  productType = MainActivity.SHOES;
					  break;
				  case MainActivity.JEWELRY:
					  productType = MainActivity.JEWELRY;
					  break;
				  case MainActivity.BAGS:
					  productType = MainActivity.BAGS;
					  break;
				  case MainActivity.SWIMWEAR:
					  productType = MainActivity.SWIMWEAR;
					  break;
				  default:
					  showToast("Error. Please try again.");
					  break;
				}
				Bundle data = new Bundle();
				data.putInt("productType", productType);
				Intent i = new Intent(UserLikes.this, Like.class);
				i.putExtras(data);
				startActivity(i);
			}
		});
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
             showToast("No Data Found");
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
	    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
