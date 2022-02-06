package com.outwire.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.outwire.R;
import com.outwire.fragments.register.CompleteRegistrationFragment;
import com.outwire.objects.User;
import com.outwire.util.SharedPreferencesHelper;

public class CompleteRegistrationActivity extends AppCompatActivity {

    private CompleteRegistrationFragment mCompleteRegistrationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);

        if (savedInstanceState == null)
            mCompleteRegistrationFragment = new CompleteRegistrationFragment();
        else
            mCompleteRegistrationFragment = (CompleteRegistrationFragment) getSupportFragmentManager()
                    .findFragmentByTag("complete_register_fragment");

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.complete_register_container, mCompleteRegistrationFragment, "complete_register_fragment")
                .commit();
        setupNextButton();
    }

    public void setupNextButton() {

        findViewById(R.id.complete_register_nextButton)
                .setOnClickListener(v -> {

                    if ((mCompleteRegistrationFragment).saveValue(this)) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        User user = SharedPreferencesHelper.UserPreferences.getUserRegister(this);
                        SharedPreferencesHelper.UserPreferences.saveUser(user, this);
                        finish();
                    }

                });


    }

}