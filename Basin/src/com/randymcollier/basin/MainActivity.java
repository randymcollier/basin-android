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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends FragmentActivity {
	
	/**
	 * Integer value to represent the Splash fragment.
	 */
	public static final int SPLASH = 0;
	
	/**
	 * Integer value to represent the Selection fragment.
	 */
	public static final int SELECTION = 1;
	
	/**
	 * Integer value to represent the Logout fragment.
	 */
	public static final int LOGOUT = 2;
	
	/**
	 * Integer value to represent the Channels fragment.
	 */
	public static final int CHANNELS = 3;
	
	/**
	 * Integer value to represent the number of fragments.
	 */
	public static final int FRAGMENT_COUNT = CHANNELS +1;
	
	/**
	 * Integer value to represent the Tops Channel.
	 * Used by ChannelsFragment.java and UserLikes.java to determine
	 * which Channel the user selected from the list.
	 */
	public static final int TOPS = 1;
	
	/**
	 * Integer value to represent the Bottoms Channel.
	 * Used by ChannelsFragment.java and UserLikes.java to determine
	 * which Channel the user selected from the list.
	 */
	public static final int BOTTOMS = 2;
	
	/**
	 * Integer value to represent the Dresses Channel.
	 * Used by ChannelsFragment.java and UserLikes.java to determine
	 * which Channel the user selected from the list.
	 */
	public static final int DRESSES = 3;
	
	/**
	 * Integer value to represent the Sunglasses Channel.
	 * Used by ChannelsFragment.java and UserLikes.java to determine
	 * which Channel the user selected from the list.
	 */
	public static final int SUNGLASSES = 4;
	
	/**
	 * Integer value to represent the Jewelry Channel.
	 * Used by ChannelsFragment.java and UserLikes.java to determine
	 * which Channel the user selected from the list.
	 */
	public static final int JEWELRY = 5;
	
	/**
	 * Integer value to represent the Shoes Channel.
	 * Used by ChannelsFragment.java and UserLikes.java to determine
	 * which Channel the user selected from the list.
	 */
	public static final int SHOES = 6;
	
	/**
	 * Integer value to represent the Bags Channel.
	 * Used by ChannelsFragment.java and UserLikes.java to determine
	 * which Channel the user selected from the list.
	 */
	public static final int BAGS = 7;
	
	/**
	 * Integer value to represent the Swimwear Channel.
	 * Used by ChannelsFragment.java and UserLikes.java to determine
	 * which Channel the user selected from the list.
	 */
	public static final int SWIMWEAR = 8;
	
	/**
	 * Menu option.
	 */
	private MenuItem logout, channels, likes;

	/**
	 * Array of fragments used to hide or show the appropriate fragment.
	 */
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	/**
	 * Boolean value to determine whether the user is logged in or not.
	 */
	private static boolean isLoggedIn = false;
	
	/**
	 * This class helps to create, automatically open (if applicable), save,
	 * and restore the Active Session in a way that is similar to Android UI
	 * lifecycles.
	 */
	private UiLifecycleHelper uiHelper;
	
	/**
	 * Provides asynchronous notification of Session state changes.
	 */
	private Session.StatusCallback callback = 
	    new Session.StatusCallback() {
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	/**
	 * Boolean value to determine if the activity is visible.
	 */
	private boolean isResumed = false;

	/**
	 * Overridden method that is called when the Activity is created.
	 * <p>
	 * This method prepares the Activity for use by setting the Facebook
	 * UiLifecycleHelper, setting the content view, initializing the
	 * fragments, and hiding the fragments.
	 * @param savedInstanceState	If the activity is being re-initialized
	 * after previously being shut down then this Bundle contains the data
	 * it most recently supplied in onSaveInstanceState(Bundle). Note:
	 * Otherwise it is null.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // Print out the key hash
	    //getFacebookKeyhash();
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);

	    setContentView(R.layout.main);
	    
	    initializeFragments();
	}

	private void getFacebookKeyhash() {
		try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.randymcollier.basin", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
	}
	
	/**
	 * Uses the current fragment manager to initialize each fragment, then
	 * hide each fragment.
	 */
	private void initializeFragments() {
		FragmentManager fm = getSupportFragmentManager();
	    fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
	    fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
	    fragments[LOGOUT] = fm.findFragmentById(R.id.userSettingsFragment);
	    fragments[CHANNELS] = fm.findFragmentById(R.id.channelsFragment);

	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = 0; i < fragments.length; i++) {
	        transaction.hide(fragments[i]);
	    }
	    transaction.commit();
	}
	
	/**
	 * Shows the appropriate fragment and hides all the rest.
	 * The integer parameter fragmentIndex is used to determine which fragment
	 * in the fragment array is to be shown. The boolean parameter
	 * addToBackStack determines whether or not the transaction is to be added
	 * to the backstack or not.
	 * @param fragmentIndex		Integer value to represent the fragment to be
	 * shown.
	 * @param addToBackStack	Boolean value to determine whether the
	 * transaction is to be added to the backstack or not.
	 */
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
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
	    isResumed = true;
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
	    isResumed = false;
	}
	
	/**
	 * Determines which fragment to show based on whether the SessionState is
	 * opened or closed.
	 * @param session
	 * @param state
	 * @param exception
	 */
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	        	isLoggedIn = true;
	            showFragment(SELECTION, false);
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	        	isLoggedIn = false;
	            showFragment(SPLASH, false);
	        }
	    }
	}
	
	/**
	 * This is the fragment-orientated version of onResume(). Determines
	 * which fragment to show based on the current Session.
	 */
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the selection fragment
	    	isLoggedIn = true;
	        showFragment(SELECTION, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	    	isLoggedIn = false;
	        showFragment(SPLASH, false);
	    }
	}
	
	/**
	 * Called when the activity exits. Called immediately before onResume()
	 * when the activity is re-starting. Calls the UiLifecycleHelper
	 * onActivityResult().
	 * @param requestCode	The integer request code originally supplied to
	 * startActivityForResult(), allowing you to identify who this result came
	 * from.
	 * @param resultCode	The integer result code returned by the child
	 * activity through its setResult().
	 * @param data	An Intent, which can return result data to the caller
	 * (various data can be attached to Intent "extras").
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
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
	 * Called to retrieve per-instance state from an activity before being
	 * killed so that the state can be restored in onCreate(Bundle) or
	 * onRestoreInstanceState(Bundle) (the Bundle populated by this method will
	 * be passed to both).
	 * @param outState Bundle in which to place your saved state.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	/**
	 * Prepare the Screen's standard options menu to be displayed. This is
	 * called right before the menu is shown, every time it is shown.
	 * @param menu	The options menu as last shown or first initialized by
	 * onCreateOptionsMenu().
	 * @return		You must return true for the menu to be displayed; if you
	 * return false it will not be shown.
	 */
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[SELECTION].isVisible()) {
            if (menu.size() == 0) {
                logout = menu.add(R.string.logout);
                channels = menu.add(R.string.channels);
                likes = menu.add(R.string.likes);
            }
            return true;
        } else {
            menu.clear();
            logout = null;
            channels = null;
            likes = null;
        }
        return false;
    }

    /**
     * Called whenever an item in the menu is selected.
     * @param item	The menu item selected.
     * @return		boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.equals(logout)) {
            showFragment(LOGOUT, true);
            return true;
        }
        else if (item.equals(channels)) {
        	showFragment(CHANNELS, true);
        	return true;
        }
        else if (item.equals(likes)) {
        	Intent i = new Intent(MainActivity.this, UserLikes.class);
        	startActivity(i);
        	return true;
        }
        return false;
    }
	
	/**
	 * Determines whether or not the user is currently logged in.
	 * @return	isLoggedIn
	 */
    public static boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	/**
	 * Displays a toast message.
	 * @param message
	 */
    public void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
	
}
