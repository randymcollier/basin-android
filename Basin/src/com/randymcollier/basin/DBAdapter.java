// This class is the communication medium to the database.

package com.randymcollier.basin;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "basin";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE_OPINIONS = 
			"create table opinions(opinionid integer primary key autoincrement, " +
			     "opinion text, " +
			     "resource text);";
	private final Context context;
	
	protected DatabaseHelper DBHelper;
	protected SQLiteDatabase db;
	
	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {			
			try {
				db.execSQL(DATABASE_CREATE_OPINIONS);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS opinions;");
			onCreate(db);
		}
	}
		
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
		
	public void close() {
		DBHelper.close();
	}
	
	public Cursor getAllOpinions() {
		return db.query("opinions", new String[] {"opinionid", "opinion", "resource"}, 
				null, null, null, null, "resource asc");
	}
	
	public Cursor getSingleOpinion(int opinionid) {
		String query = "SELECT name, class, drugid FROM drugs WHERE drugid='" + opinionid + "';";
		return db.rawQuery(query, null);
	}
	
	public Cursor getLikes() {
		String query = "SELECT resource FROM opinions WHERE opinion = 'like';";
		return db.rawQuery(query, null);
	}
	
	public Cursor getDislikes() {
		String query = "SELECT resource FROM opinions WHERE opinion = 'dislike';";
		return db.rawQuery(query, null);
	}
	
	public long addOpinion(String opinion, String resource) {
		ContentValues args = new ContentValues();
		args.put("opinion", opinion);
		args.put("resource", resource);
		return db.insert("opinions", null, args);
	}
	
	public void checkUpgrade() {
		if (db.needUpgrade(DATABASE_VERSION)) {
			DBHelper.onUpgrade(db, (db.getVersion()), DATABASE_VERSION);
		}
	}
}
