package com.xinyusoft.xshelllib.sqlite;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserService {

	private DBOpenHelper dbOpenHelper;

	public UserService(Context context) {
		dbOpenHelper = new DBOpenHelper(context);
	}

	public void save(UserBean ub) {
		SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("taskGoal", ub.getTaskGoal());
		contentValues.put("type", ub.getType());
		contentValues.put("time", ub.getTime());
		contentValues.put("totalNum", ub.getTotalNum());
		contentValues.put("lastMessage", ub.getLastMessage());
		contentValues.put("lastSender", ub.getLastSender());
		contentValues.put("unReadMessageNum", ub.getUnReadMessageNum());
		database.insert("user", null, contentValues);
		database.close();

	}

	
	public int updateFramTaskGoal(String taskGoal){
		SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("unReadMessageNum", 0);
		int i = database.update("user", contentValues, "taskGoal=?", new String[]{taskGoal});
		database.close();
		return i;
	}
	
	public void update(UserBean ub) {
		SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("taskGoal", ub.getTaskGoal());
		contentValues.put("type", ub.getType());
		contentValues.put("time", ub.getTime());
		contentValues.put("totalNum", ub.getTotalNum());
		contentValues.put("lastMessage", ub.getLastMessage());
		contentValues.put("lastSender", ub.getLastSender());
		contentValues.put("unReadMessageNum", ub.getUnReadMessageNum());
		database.update("user", contentValues, "taskGoal=?",
				new String[] { ub.getTaskGoal() });
		database.close();
	}

	public UserBean find(String taskGoal) {
		SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
		Cursor cursor = database.query("user", new String[] { "taskGoal",
				"type", "time", "totalNum", "lastMessage", "lastSender",
				"unReadMessageNum" }, "taskGoal=?", new String[] { taskGoal },
				null, null, null);

		if (cursor.moveToNext()) {
			UserBean mi = new UserBean();
			mi.setTaskGoal(cursor.getString(0));
			mi.setType(cursor.getString(1));
			mi.setTime(cursor.getString(2));
			mi.setTotalNum(cursor.getInt(3));
			mi.setLastMessage(cursor.getString(4));
			mi.setLastSender(cursor.getString(5));
			mi.setUnReadMessageNum(cursor.getInt(6));
			database.close();
			return mi;
		}
		database.close();
		return null;
	}

	public int delete(String taskGoal) {
		SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
		int i = database.delete("user", "taskGoal=?", new String[] { taskGoal });
		database.close();
		return i;
	}

	public UserBean jsonToUserBean(JSONObject json, UserBean ub) {
		int unReadMeassageNum = 0;
		if (ub == null) {
			ub = new UserBean();
			try {
				unReadMeassageNum = json.getInt("totalNum");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				
				unReadMeassageNum = json.getInt("totalNum") - ub.getTotalNum() + ub.getUnReadMessageNum();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			ub.setTaskGoal(json.getString("taskGoal"));
			ub.setType(json.getString("type"));
			ub.setTime(json.getString("time"));
			ub.setTotalNum(json.getInt("totalNum"));
			ub.setLastMessage(json.getString("lastMessage"));
			ub.setLastSender(json.getString("lastSender"));
			ub.setUnReadMessageNum(unReadMeassageNum);
			return ub;
		} catch (JSONException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	

}
