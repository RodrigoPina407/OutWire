package com.outwire.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.outwire.R;
import com.outwire.fragments.register.CompleteRegistrationFragment;
import com.outwire.fragments.register.EmailVerificationScreenFragment;
import com.outwire.fragments.register.RegisterEmailFragment;
import com.outwire.objects.User;
import com.outwire.util.FirebaseHelper;
import com.outwire.util.FirebaseStorageHelper;
import com.outwire.util.SharedPreferencesHelper;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    private MaterialToolbar mToolbar;
    private Button mNextButton;
    private Button mBackButton;

    private Fragment mRegisterEmailFragment;
    private Fragment mCompleteRegistrationFragment;
    private Fragment mEmailVerificationFragment;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initializeUI();

        mToolbar.setNavigationOnClickListener(v -> finish());

        if (savedInstanceState == null) {
            initializeFragments();
            SharedPreferencesHelper.UserPreferences.removeUserRegister(this);
        } else {

            mFragmentManager = getSupportFragmentManager();
            mRegisterEmailFragment = mFragmentManager.findFragmentByTag("register_email");
            mCompleteRegistrationFragment = mFragmentManager.findFragmentByTag("complete_register");
            mEmailVerificationFragment = mFragmentManager.findFragmentByTag("email_verification");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        setupNextButton();
        setupBackButton();
    }


    private void register() {

        User user = SharedPreferencesHelper.UserPreferences.getUserRegister(this);

        showProgressBar();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getDisplayName())
                .build();


        FirebaseHelper.getAuth()
                .createUserWithEmailAndPassword(user.getUserEmail(), SharedPreferencesHelper.getString(this,
                        getString(R.string.user_shared_file), getString(R.string.user_register_pwd_key))
                )
                .addOnCompleteListener(createUserTask -> {

                    if (createUserTask.isSuccessful()) {

                        hideProgressBar();

                        Objects.requireNonNull(
                                FirebaseHelper.getFirebaseUser())
                                .updateProfile(profileUpdates);

                        user.setWithEmailLogin(true);
                        user.setTimeOfRegistration(System.currentTimeMillis());

                        saveUserDataToFirebase(user);


                    } else {

                        Toast toast = Toast
                                .makeText(getApplicationContext(), getString(R.string.register_failed), Toast.LENGTH_LONG);
                        toast.show();

                        hideProgressBar();
                    }
                });

    }

    private void saveUserDataToFirebase(User user) {


        FirebaseStorageHelper transfer = new FirebaseStorageHelper() {

            @Override
            public void executeOnFailure() {
                super.executeOnFailure();
                FirebaseHelper.getUserReference().set(user)
                        .addOnSuccessListener(aVoid -> {
                            mNextButton.setVisibility(View.GONE);
                            SharedPreferencesHelper.UserPreferences.saveUser(user, getBaseContext());
                            toEmailVerificationScreen();
                        });
            }

            @Override
            public void executeOnSuccess() {
                super.executeOnSuccess();
                user.setPhotoUri(getDownloadUri().toString());
                SharedPreferencesHelper.UserPreferences.saveUserRegister(user, getBaseContext());

                FirebaseHelper.getUserReference().set(user)
                        .addOnSuccessListener(aVoid -> {
                            mNextButton.setVisibility(View.GONE);
                            SharedPreferencesHelper.UserPreferences.saveUser(user, getBaseContext());
                            toEmailVerificationScreen();
                        });
            }
        };

        transfer.uploadFile(this, FirebaseStorageHelper.getStream(),
                "images/" + SharedPreferencesHelper.UserPreferences.getUserRegister(this).getPhotoUri(),
                getString(R.string.img_upload_failed));
    }

    private void initializeFragments() {

        mRegisterEmailFragment = new RegisterEmailFragment();
        mCompleteRegistrationFragment = new CompleteRegistrationFragment();
        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction()
                .replace(R.id.register_container, mRegisterEmailFragment, "register_email")
                .commit();


    }

    private void initializeUI() {
        mToolbar = findViewById(R.id.main_toolbar);
        mNextButton = findViewById(R.id.register_nextButton);
        mBackButton = findViewById(R.id.register_backButton);

    }

    private void toEmailVerificationScreen() {

        mEmailVerificationFragment = new EmailVerificationScreenFragment();
        mFragmentManager
                .beginTransaction()
                .replace(R.id.register_container, mEmailVerificationFragment, "email_verification")
                .addToBackStack("")
                .commit();


    }

    private void setupNextButton() {

        mNextButton.setOnClickListener(v -> {


            if ((mRegisterEmailFragment.isVisible())) {
                boolean validate = ((RegisterEmailFragment) mRegisterEmailFragment).saveValue(this);
                mCompleteRegistrationFragment = new CompleteRegistrationFragment();

                if (validate) {
                    mFragmentManager
                            .beginTransaction()
                            .replace(R.id.register_container, mCompleteRegistrationFragment, "complete_register")
                            .addToBackStack("")
                            .commit();

                }

            } else if (mCompleteRegistrationFragment.isVisible()) {

                boolean userFieldCompleted = ((CompleteRegistrationFragment) mCompleteRegistrationFragment).saveValue(this);

                if (userFieldCompleted)
                    register();

            }
        });

    }

    private void setupBackButton() {

        mBackButton.setOnClickListener(v -> {
            mFragmentManager.popBackStack();
            if (mCompleteRegistrationFragment.isVisible())
                mBackButton.setVisibility(View.GONE);
        });

    }


    private void showProgressBar() {
        findViewById(R.id.register_progressbar).setVisibility(View.VISIBLE);
        findViewById(R.id.view).setVisibility(View.VISIBLE);
        findViewById(R.id.view).setEnabled(false);
    }


    private void hideProgressBar() {
        findViewById(R.id.register_progressbar).setVisibility(View.GONE);
        findViewById(R.id.view).setVisibility(View.GONE);
        findViewById(R.id.view).setEnabled(true);
    }


}