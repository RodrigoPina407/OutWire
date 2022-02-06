package com.outwire.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.outwire.R;
import com.outwire.objects.User;

import static android.content.Context.MODE_PRIVATE;

public class UserHelper {

    private static User mUser;
    private static String mRegisterPassword ="";


    public static User getUser() {
        return mUser;
    }

    public static void setUser(User mUser) {
        if (mUser == null)
            UserHelper.mUser = new User();
        else
            UserHelper.mUser = mUser;
    }


    public static String getRegisterPassword() {
        return mRegisterPassword;
    }

    public static void setRegisterPassword(String mRegisterPassword) {
        UserHelper.mRegisterPassword = mRegisterPassword;
    }

    public static void saveToSharedPreferences(User u, Context context){

        Gson gson = new Gson();
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.user_shared_file), Context.MODE_PRIVATE).edit();
        String userToJson = gson.toJson(u);
        editor.putString(context.getString(R.string.shared_pref_user_key), userToJson);
        editor.apply();
    }

    public static void saveUserRegistrationToSharedPreferences(User u, Context context){

        Gson gson = new Gson();
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.user_shared_file), Context.MODE_PRIVATE).edit();
        String userToJson = gson.toJson(u);
        editor.putString("USER_REGISTER", userToJson);
        editor.apply();
    }

    public static User getUserFromSharedPreferences(Context context) {

        Gson gson = new Gson();

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.user_shared_file), MODE_PRIVATE);
        String jsonToUser = sharedPreferences.getString(context.getString(R.string.shared_pref_user_key), null);
        return gson.fromJson(jsonToUser, User.class);

    }

    public static void removerUserFromSharedPreferences(Context context){

        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.user_shared_file), MODE_PRIVATE).edit();
        editor.remove(context.getString(R.string.shared_pref_user_key)).apply();
    }

    public static boolean checkUserExistsInSharedPreferences(Context context){
        return context.getSharedPreferences(context.getString(R.string.user_shared_file), MODE_PRIVATE)
                .contains(context.getString(R.string.shared_pref_user_key));
    }


}
