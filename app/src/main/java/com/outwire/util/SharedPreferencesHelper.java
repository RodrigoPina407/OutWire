package com.outwire.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.outwire.R;
import com.outwire.objects.User;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHelper {

    public static void saveString(Context context, String s, String filename, String key){

        SharedPreferences.Editor editor = context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit();

        editor.putString(key, s);
        editor.apply();
    }

    public static String getString(Context context, String filename, String key){

        SharedPreferences sharedPreferences = context.getSharedPreferences(filename, MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static <T> void saveToJson(Context context, T obj, String filename, String key){

        Gson gson = new Gson();
        SharedPreferences.Editor editor = context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit();
        String objectToJson = gson.toJson(obj);
        editor.putString(key, objectToJson);
        editor.apply();
    }

    public static <T> T getFromJson(Context context, Class<T> c, String filename, String key) {

        Gson gson = new Gson();

        SharedPreferences sharedPreferences = context.getSharedPreferences(filename, MODE_PRIVATE);
        String jsonToObj = sharedPreferences.getString(key, null);
        return gson.fromJson(jsonToObj, c);

    }

    public static void removeKey(Context context, String filename, String key){

        SharedPreferences.Editor editor = context.getSharedPreferences(filename, MODE_PRIVATE).edit();
        editor.remove(key).apply();
    }

    public static boolean checkKeyExists(Context context, String filename, String key){
        return context.getSharedPreferences(filename, MODE_PRIVATE)
                .contains(key);
    }

    public static class UserPreferences {

        public static void saveUserRegister(User user, Context context) {
            saveToJson(context, user
                    , context.getString(R.string.user_shared_file), context.getString(R.string.user_register_key));
        }

        public static User getUserRegister(Context context) {
            return SharedPreferencesHelper
                    .getFromJson(context, User.class
                            , context.getString(R.string.user_shared_file), context.getString(R.string.user_register_key));
        }

        public static void saveUser(User user, Context context) {
            saveToJson(context, user
                    , context.getString(R.string.user_shared_file), context.getString(R.string.shared_pref_user_key));
        }

        public static User getUser(Context context) {
            return SharedPreferencesHelper
                    .getFromJson(context, User.class
                            , context.getString(R.string.user_shared_file), context.getString(R.string.shared_pref_user_key));
        }

        public static void removeUserRegister(Context context){
            removeKey(context, context.getString(R.string.user_shared_file), context.getString(R.string.user_register_key));
            removeKey(context, context.getString(R.string.user_shared_file), context.getString(R.string.user_register_pwd_key));
        }

        public static void removeUser(Context context){
            removeKey(context, context.getString(R.string.user_shared_file), context.getString(R.string.shared_pref_user_key));
        }

    }
}
