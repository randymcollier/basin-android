/**
 * Copyright (C) 2013 Randy Collier
 * 
 * Portions of this file were obtained from Facebook. You can access their
 * license information at
 * 		
 * 		http://www.apache.org/licenses/LICENSE-2.0
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class LoginFragment extends Fragment {
	
	/**
	 * String containing the name of the Shared Preference.
	 */
	public static final String PREFS_NAME = "user_conditions";
	
	/**
	 * A Log In/Log Out button that maintains session state and logs in/out
	 * for the app.
	 */
	LoginButton loginButton;
	
	/**
	 * This class helps to create, automatically open (if applicable), save,
	 * and restore the Active Session in a way that is similar to Android UI
	 * lifecycles.
	 */
	private UiLifecycleHelper uiHelper;
    
	/**
	 * Provides asynchronous notification of Session state changes.
	 */
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    /**
	 * Overridden method that is called when the Fragment is created.
	 * <p>
	 * This method prepares the Fragment for use by setting the Facebook
	 * UiLifecycleHelper, setting the content view, initializing the
	 * LoginButton, and setting permissions on the LoginButton.
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
    	uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        
        View view = inflater.inflate(R.layout.login, container, false);
        
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_birthday", "user_location", "user_hometown", "user_work_history", "user_education_history"));
        
        return view;
    }
	
    /**
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(),
	 * for your activity to start interacting with the user. Calls the
	 * UiLifecycleHelper onResume(). Sets isResumed to true.
	 */
    @Override
    public void onResume() {
        super.onResume();
       uiHelper.onResume();
    }
	
    /**
	 * Called as part of the activity lifecycle when an activity is going into
	 * the background, but has not (yet) been killed. The counterpart to
	 * onResume(). Calls the UiLifecycleHelper onPause(). Sets isResumed to
	 * false;
	 */
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    /**
	 * Performs final cleanup. Calls the UiLifecycleHelper onDestroy().
	 */
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
    
    /**
	 * Determines whether or not to make a request to Facebook or not based on
	 * the boolean value of loggedIn.
	 * @param session
	 * @param state
	 * @param exception
	 */
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            if (!state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
            	SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 1);
        	    boolean loggedIn = settings.getBoolean("loggedIn", false);
        	    if(!loggedIn){
        	    	makeMeRequest(session);
        	    }
            }
        }
    }

    /**
     * Makes request to Facebook to retrieve user information.
     * @param session
     */
    private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                    	buildUserInfo(user);
                    	SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 1);
 	                   	SharedPreferences.Editor editor = settings.edit();
 	                   	editor.putBoolean("loggedIn", true);
 	                   	editor.commit(); 
                    }
                }
                if (response.getError() != null) {
                    
                }
            }
        });
        request.executeAsync();

    }
    
    /**
     * Parses JSON data retrieved from Facebook and writes it to the
     * database.
     * @param user	User information retrieved from Facebook.
     */
    private void buildUserInfo(GraphUser user) {
    	InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String result = new String();
        StringBuilder sb = new StringBuilder();
        String userId = new String();
    	
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String birthday = user.getBirthday();
        String currentLocation = user.getLocation().getProperty("name").toString();
        String email = user.getProperty("email").toString();
        String gender = user.getProperty("gender").toString();
        String hometown = "";
        JSONObject town = (JSONObject)user.getProperty("hometown");
        hometown += (town.optString("name"));
        
        nameValuePairs.add(new BasicNameValuePair("firstName", firstName));
        nameValuePairs.add(new BasicNameValuePair("lastName", lastName));
        nameValuePairs.add(new BasicNameValuePair("birthday", birthday));
        nameValuePairs.add(new BasicNameValuePair("currentLocation", currentLocation));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("gender", gender));
        nameValuePairs.add(new BasicNameValuePair("hometown", hometown));
        
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/insertUser.php");
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
               sb.append(reader.readLine());
               is.close();
               result=sb.toString();
               userId = result;
        } catch(Exception e){
                      Log.e("log_tag", "Error converting result "+e.toString());
        }
        
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 1);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userId", userId);
        editor.commit(); 
        
        nameValuePairs.clear();
        
        StringBuilder work = new StringBuilder("");
        JSONArray workplaces = (JSONArray)user.getProperty("work");
        if (workplaces.length() > 0) {
            for (int i=0; i < workplaces.length(); i++) {
                JSONObject place = workplaces.optJSONObject(i);
                JSONObject employer = new JSONObject();
                JSONObject position = new JSONObject();
                JSONObject workLocation = new JSONObject();
                String startDate = new String();
                String endDate = new String();
				try {
					nameValuePairs.add(new BasicNameValuePair("userId", userId));
					employer = place.getJSONObject("employer");
					nameValuePairs.add(new BasicNameValuePair("employer", employer.getString("name")));
					work.append("Employer: " + employer.getString("name"));
					work.append("\n");
					workLocation = place.getJSONObject("location");
					nameValuePairs.add(new BasicNameValuePair("location", workLocation.getString("name")));
					work.append("Location: " + workLocation.getString("name"));
					work.append("\n");
					position = place.getJSONObject("position");
					nameValuePairs.add(new BasicNameValuePair("position", position.getString("name")));
					work.append("Position: " + position.getString("name"));
					work.append("\n");
					startDate = place.getString("start_date");
					nameValuePairs.add(new BasicNameValuePair("startDate", startDate));
					endDate = place.getString("end_date");
					nameValuePairs.add(new BasicNameValuePair("endDate", endDate));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/insertUserWorkHistory.php");
		            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		            HttpResponse response = httpclient.execute(httppost);
		            HttpEntity entity = response.getEntity();
		            is = entity.getContent();
		        } catch (Exception e) {
		            Log.e("log_tag", "Error in http connection" + e.toString());
		        }
		        
		        nameValuePairs.clear();
            }
        }
        
        StringBuilder education = new StringBuilder("");
        JSONArray schools = (JSONArray)user.getProperty("education");
        if (schools.length() > 0) {
            for (int i=0; i < schools.length(); i++) {
                JSONObject school = schools.optJSONObject(i);
                String type = "";
                JSONObject schoolName = new JSONObject();
                JSONObject year = new JSONObject();
				try {
					nameValuePairs.add(new BasicNameValuePair("userId", userId));
					type = school.getString("type");
					nameValuePairs.add(new BasicNameValuePair("type", type));
					education.append("Type: " + type.toString());
					education.append("\n");
					schoolName = school.getJSONObject("school");
					nameValuePairs.add(new BasicNameValuePair("name", schoolName.getString("name")));
					education.append("School: " + schoolName.getString("name"));
					education.append("\n");
					year = school.getJSONObject("year");
					nameValuePairs.add(new BasicNameValuePair("year", year.getString("name")));
					education.append("Year: " + year.getString("name"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpPost httppost = new HttpPost("http://www.sodaservices.com/basin/php/insertUserEducationHistory.php");
		            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		            HttpResponse response = httpclient.execute(httppost);
		            HttpEntity entity = response.getEntity();
		            is = entity.getContent();
		        } catch (Exception e) {
		            Log.e("log_tag", "Error in http connection" + e.toString());
		        }
				nameValuePairs.clear();
            }
        }
    }
}
