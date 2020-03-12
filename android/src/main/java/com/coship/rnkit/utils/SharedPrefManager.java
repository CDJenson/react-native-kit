package com.coship.rnkit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Set;


/**
 *  author: zoujunda
 *  date: 2019/6/28 16:55
 *	version: 1.0
 *  description: One-sentence description
 */
public class SharedPrefManager {
	private SharedPreferences shareMgr;
	private static String PREFERENCE_NAME = "PREFERENCE_OF_BASE";
	private static SharedPrefManager instance;

	private SharedPrefManager(Context context, String shareName) {
		shareMgr = context.getSharedPreferences(shareName, Context.MODE_PRIVATE);
	}

	/**
	 * singleton pattern
	 *
	 * @param context Context
	 * @return SharedPrefManager
	 */
	public static SharedPrefManager getInstance(Context context) {
		if(instance == null){
			synchronized (SharedPrefManager.class) {
				if(instance == null){
					instance = new SharedPrefManager(context, PREFERENCE_NAME);
				}
			}
		}
		return instance;
	}

	public void put(String key, boolean value){
		Editor edit = shareMgr.edit();
		if(edit != null){
			edit.putBoolean(key, value);
			edit.commit();
		}
	}

	public void put(String key, String value){
		Editor edit = shareMgr.edit();
		if(edit != null){
			edit.putString(key, value);
			edit.commit();
		}
	}

	public void put(String key, int value){
		Editor edit = shareMgr.edit();
		if(edit != null){
			edit.putInt(key, value);
			edit.commit();
		}
	}

	public void put(String key, float value){
		Editor edit = shareMgr.edit();
		if(edit != null){
			edit.putFloat(key, value);
			edit.commit();
		}
	}

	public void put(String key, long value){
		Editor edit = shareMgr.edit();
		if(edit != null){
			edit.putLong(key, value);
			edit.commit();
		}
	}

	public void put(String key,Object obj){
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;
		try {
			Editor edit = shareMgr.edit();
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			os.writeObject(obj);
			String bytesToHexString = HexUtils.encodeHexStr(bos.toByteArray());
			edit.putString(key, bytesToHexString);
			edit.commit();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("shar","save the object failed");
		} finally{
			IOUtils.close(os);
			IOUtils.close(bos);
		}
	}

	public void put(String key, Set<String> value){
		Editor edit = shareMgr.edit();
		if(edit != null){
			edit.putStringSet(key, value);
			edit.commit();
		}
	}

	public String get(String key, String defValue){
		return shareMgr.getString(key, defValue);
	}

	public boolean get(String key, boolean defValue){
		return shareMgr.getBoolean(key, defValue);
	}

	public int get(String key, int defValue){
		return shareMgr.getInt(key, defValue);
	}

	public float get(String key, float defValue){
		return shareMgr.getFloat(key, defValue);
	}

	public long get(String key, long defValue){
		return shareMgr.getLong(key, defValue);
	}

	public Set<String> get(String key, Set<String> defValue){
		return shareMgr.getStringSet(key, defValue);
	}


	/**
	 *
	 * @param key
	 * @param defaultObject
	 * @return
	 */
	public Object get(String key,Object defaultObject){
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		
		try {
			if (shareMgr.contains(key)) {
				String string = shareMgr.getString(key, "");
				if(TextUtils.isEmpty(string)){
					return null;
				}else{
					byte[] stringToBytes = HexUtils.decodeHex(string.toCharArray());
					bis = new ByteArrayInputStream(stringToBytes);
					ois = new ObjectInputStream(bis);
					Object readObject = ois.readObject();
					return readObject;
				}
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally{
			IOUtils.close(ois);
			IOUtils.close(bis);
		}
		return defaultObject;
	}

}
