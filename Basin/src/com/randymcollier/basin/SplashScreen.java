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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashScreen extends Activity {
 
    /**
     * Splash screen timer.
     */
    private static int SPLASH_TIME_OUT = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Sets the splash screen to the GIF
        GIFView gifView = new GIFView(this);
        setContentView(gifView);
 
        new Handler().postDelayed(new Runnable() {
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Starts the app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    
    public void showToast(String message) {
		final Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.show();
		Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               toast.cancel(); 
           }
        }, 500);
	}
 
}