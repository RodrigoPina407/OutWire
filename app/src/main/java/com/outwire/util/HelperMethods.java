package com.outwire.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.outwire.R;
import com.outwire.objects.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.MODE_PRIVATE;

public class HelperMethods {


    public static String inputStreamToString(InputStream inputStream){

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;


        try {
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line).append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }



        return stringBuilder.toString();
    }

    public static boolean verifyDisplayName(EditText name) {
        String s = name.getText().toString().trim();

        if (s.isEmpty()) {
            name.setError(name.getContext().getString(R.string.name_field_empty));
            return false;
        } else
            return true;

    }


    public static boolean isValidEmail(CharSequence target, EditText email) {

        if(getEditTextString(email).isEmpty())
            email.setError(email.getContext().getString(R.string.email_field_empty));
        else if(Patterns.EMAIL_ADDRESS.matcher(target).matches())
            return true;
        else{
            email.setError(email.getContext().getString(R.string.invalid_email));
            email.requestFocus();
        }

        return false;
    }

    public static boolean isValidPassword(String s, EditText password) {

        if (s.isEmpty()) {
            password.setError(password.getContext().getString(R.string.password_field_empty));
            return false;
        } else if (s.length() < 8) {
            password.setError(password.getContext().getString(R.string.minimum_password_length));
            return false;
        } else
            return true;
    }

    public static String getEditTextString(EditText e) {
        return e.getText().toString();
    }

    public static String getRealImagePath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    public static int convertDpToPx(int dp, Context context){

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);

    }



}
