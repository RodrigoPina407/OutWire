package com.outwire.fragments.register;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.outwire.R;
import com.outwire.objects.User;
import com.outwire.util.SharedPreferencesHelper;

import static com.outwire.util.HelperMethods.isValidEmail;
import static com.outwire.util.HelperMethods.isValidPassword;

public class RegisterEmailFragment extends Fragment implements RegisterInterface{

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_register_email, container, false);
        return view;
    }

    public boolean saveValue(Context context) {
        User user = new User();

        if(SharedPreferencesHelper.checkKeyExists(context,
                getString(R.string.user_shared_file), getString(R.string.user_register_key)))
            user = SharedPreferencesHelper.UserPreferences.getUserRegister(context);

        boolean valid = false;

        EditText email = view.findViewById(R.id.register_email);
        EditText password = view.findViewById(R.id.register_password);

        String e = email.getText().toString().trim();
        String p = password.getText().toString();

        if (isValidEmail(e, email)){
            user.setUserEmail(e);
            valid = true;
        }

        if (isValidPassword(p, password))
            SharedPreferencesHelper.saveString(context, p, getString(R.string.user_shared_file), getString(R.string.user_register_pwd_key));
        else
            valid = false;

        SharedPreferencesHelper.UserPreferences.saveUserRegister(user,getContext());

        return valid;
    }
}
