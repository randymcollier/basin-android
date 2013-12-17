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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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

import android.OnSwipeTouchListener.OnSwipeTouchListener;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductFragment extends Fragment {
	
	/**
	 * String containing the name of the Shared Preference.
	 */
	private static final String PREFS_NAME = "user_conditions";
	
	private int productType;
	
	/**
	 * String containing the URL where the images are stored.
	 */
	private final String URL = "http://www.sodaservices.com/basin/images/";
	
	/**
	 * ImageView object used to manipulate the ImageView on the layout.
	 */
	private ImageView image;
	
	/**
	 * TextView object used to manipulate the TextView on the layout.
	 */
	private TextView tv_type, tv_description;
	
	/**
	 * String to store the value that will be assigned to the Type TextView.
	 */
	private String type = new String();
	
	/**
	 * String to store the value that will be assigned to the Description
	 * TextView.
	 */
	private String description = new String();
	
	/**
	 * View object to hold the layout View.
	 */
	private View view;
	
	/**
	 * Integer value of the corresponding image to retrieve from the server.
	 * currentImage will be the integer value of the file name of the image. 
	 */
	private int currentImage;
	
	/**
	 * Integer value to represent the amount of time in milliseconds to display
	 * a Toast message.
	 */
	private final int TOASTTIME = 500;
	
	/**
	 * Overridden method that is called when the Fragment is created.
	 * <p>
	 * This method prepares the Fragment for use by inflating the proper
	 * layout, assigning the Views, applying the OnTouchListener, and
	 * setting the first image.
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
        view = inflater.inflate(R.layout.product, container, false);
	    
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		setComponents(view);
		
		setOnTouchListener();
		
		getChannel();
		
		setImageURL();
		
		return view;
	}
	
	/**
	 * Retrieves and sets the last Channel the user accessed.
	 * The Channel is retrieved from the application's stored preferences and
	 * is stored as an integer.
	 */
	private void getChannel() {
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 1);
	    productType = settings.getInt("channel", MainActivity.JEWELRY);
	}
	
	/**
	 * This is used by other fragments to reload the image data after a change.
	 * <p>
	 * For example, when a new Channel is selected, ChannelFragment calls
	 * this to reload the image data with the new selected Channel.
	 */
	protected void getData() {
		getChannel();
		setImageURL();
	}

	/**
	 * Assigns the previously declared variables to the appropriate views.
	 * <p>
	 * This allows us to manipulate the components later on.
	 * @param view	Selection layout
	 */
    private void setComponents(View view) {
		image = (ImageView) view.findViewById(R.id.product_image);
		tv_type = (TextView) view.findViewById(R.id.type);
		tv_description = (TextView) view.findViewById(R.id.description);
	}

	/**
	 * Applies a touch listener to the ImageView. The method also implements the
	 * methods from the OnSwipeTouchListener class to process swipes.
	 * @see android.OnSwipeTouchListener
	 */
    private void setOnTouchListener() {
		image.setOnTouchListener(new OnSwipeTouchListener() {
		    public void onSwipeRight() {
		    	showToast("Like");
		    	
		    	SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 1);
        	    String userId = settings.getString("userId", "0");
        	    
        	    InputStream is = null;
        	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        	    nameValuePairs.add(new BasicNameValuePair("userId", userId));
        	    nameValuePairs.add(new BasicNameValuePair("productId", Integer.toString(currentImage)));
        	    try {
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/insertUserLikes.php");
		            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		            HttpResponse response = httpclient.execute(httppost);
		            HttpEntity entity = response.getEntity();
		            is = entity.getContent();
		        } catch (Exception e) {
		            Log.e("log_tag", "Error in http connection" + e.toString());
		        }
		        
		        nameValuePairs.clear();
        	    
				setImageURL();
		    }
		    public void onSwipeLeft() {
		    	showToast("Dislike");
		    	
		    	SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 1);
        	    String userId = settings.getString("userId", "0"); 
        	    
        	    InputStream is = null;
        	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        	    nameValuePairs.add(new BasicNameValuePair("userId", userId));
        	    nameValuePairs.add(new BasicNameValuePair("productId", Integer.toString(currentImage)));
        	    try {
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/insertUserDislikes.php");
		            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		            HttpResponse response = httpclient.execute(httppost);
		            HttpEntity entity = response.getEntity();
		            is = entity.getContent();
		        } catch (Exception e) {
		            Log.e("log_tag", "Error in http connection" + e.toString());
		        }
		        
		        nameValuePairs.clear();
		    	
				setImageURL();
		    }
		    public void onSwipeBottom() {
		    	showToast("Pass");
		    	setImageURL();
		    }
		    public void onSwipeTop() {
		    	showToast("Pass");
		    	setImageURL();
		    }
		});
		
	}
	
	/**
	 * Requests the integer value of the image's file name from the server.
	 * <p>
	 * An HTTP POST is made to the provided url in String form. The result is then
	 * converted to a String. The converted String is then parsed for it's
	 * integer value and returned.
	 * @return integer value of the file name for the corresponding image
	 */
    private int getImage() {
    	String productId = new String();
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
        String result = new String();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("productType", Integer.toString(productType)));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/getImage.php");
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
             result=sb.toString();
             }catch(Exception e){
                   Log.e("log_tag", "Error converting result "+e.toString());
           }
        JSONArray jArray = new JSONArray();
        try{
            jArray = new JSONArray(result);
            JSONObject json_data = new JSONObject();
            for(int i=0;i<jArray.length();i++){
                   json_data = jArray.getJSONObject(i);
                   productId = json_data.getString("ProductId");//"ProductId" is the column name in database
               }
            }
            catch(JSONException e1){
             Toast.makeText(getActivity(), "No Data Found" ,Toast.LENGTH_LONG).show();
            } catch (ParseException e1) {
         e1.printStackTrace();
       }
    	
    	nameValuePairs.clear();
        
        //return the Integer version of the result
        return Integer.parseInt(productId);
	}
	
	/**
	 * Asynchronous method that retrieves a bitmap and information
	 * corresponding to that bitmap from a server and database.
	 * <p>
	 * currentImage is set by making a call to getImage(). An HTTP POST is
	 * then made to a separate url with currentImage as an entity. The
	 * resulting JSON is then converted to a String and parsed.
	 * <p>
	 * The url specific to the image is formed by concatenating URL,
	 * currentImage, and .jpg. A request is then made to the url to retrieve
	 * the image.
	 * <p>
	 * Once all the above has completed, the information retrieved from the
	 * server is assigned to the appropriate View and displayed.
	 * <p>
	 * A ProgressDialog is displayed while the tasks are performed.
	 */
	private void setImageURL() {
	        new AsyncTask<Void, Void, Bitmap>() {
	        	final ProgressDialog dialog = new ProgressDialog(getActivity());
	        	String imageURL = new String();
	            protected Bitmap doInBackground(Void... p) {
	            	currentImage = getImage();
	            	InputStream is = null;
	            	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	            	StringBuilder sb = new StringBuilder();
	            	String result = new String();
	            	nameValuePairs.add(new BasicNameValuePair("productId", Integer.toString(currentImage)));
	            	try {
	                    HttpClient httpclient = new DefaultHttpClient();
	                    HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/getProductInfo.php");
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
	                     result=sb.toString();
	                     }catch(Exception e){
	                           Log.e("log_tag", "Error converting result "+e.toString());
	                   }
	                JSONArray jArray = new JSONArray();
	                try{
	                    jArray = new JSONArray(result);
	                    JSONObject json_data = new JSONObject();
	                    for(int i=0;i<jArray.length();i++){
	                           json_data = jArray.getJSONObject(i);
	                           type = json_data.getString("Type");//"Type" is the column name in database
	                           description = json_data.getString("Description");//"Description" is the column name in database
	                       }
	                    }
	                    catch(JSONException e1){
	                     Toast.makeText(getActivity(), "No Data Found" ,Toast.LENGTH_LONG).show();
	                    } catch (ParseException e1) {
	                 e1.printStackTrace();
	               }
	            	
	            	nameValuePairs.clear();
	            	
	                Bitmap bm = null;
	                try {
	                	imageURL = URL + currentImage + ".jpg";
	                    URL aURL = new URL(imageURL);
	                    URLConnection conn = aURL.openConnection();
	                    conn.setConnectTimeout(5000);
	                    conn.setUseCaches(true);
	                    conn.connect();
	                    is = conn.getInputStream();
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
	            	dialog.setMessage("Loading...");
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
	                showImage(bmp);
	            }
	        }.execute();
	}

	/**
	 * Assigns the bitmap and information retrieved from the server
	 * to the appropriate views.
	 * @param bitmap	the bitmap of the image
	 */
	private void showImage(Bitmap bitmap) {
		image.setImageBitmap(bitmap);
		tv_type.setText(type);
        tv_description.setText(description);
	}
	
	/**
	 * Displays a Toast message for the determined amount of time in milliseconds.
	 * @param message	String message to display
	 */
	private void showToast(String message) {
		final Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.show();
		Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               toast.cancel(); 
           }
        }, TOASTTIME);
	}
	
}
