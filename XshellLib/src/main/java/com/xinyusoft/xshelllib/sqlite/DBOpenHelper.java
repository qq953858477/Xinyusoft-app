package com.xinyusoft.xshelllib.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{
	
	public static String DBNAME = "xinyusoft";
	
	public static int VERSION = 1;
	
	public static String CREATETAB = "create table if not exists user ("
      + "userId integer primary key autoincrement,"       
      + "taskGoal  varchar not null UNIQUE,"
      + "type  varchar not null,"
      + "time  varchar,"
      + "totalNum  integer,"
      + "lastMessage  varchar,"
      + "lastSender  varchar,"
      + "unReadMessageNum  integer"                         
      + ")";
	
	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
	}
	
	public DBOpenHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATETAB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
