package com.outwire.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.outwire.R;
import com.outwire.custom.views.OutWireAlertDialog;
import com.outwire.custom.views.OutWireSignInButton;
import com.outwire.main.MainActivity;
import com.outwire.objects.User;
import com.outwire.util.FirebaseHelper;
import com.outwire.util.FirebaseStorageHelper;
import com.outwire.util.SharedPreferencesHelper;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.outwire.util.HelperMethods.getEditTextString;
import static com.outwire.util.HelperMethods.isValidEmail;


public class LoginActivity extends AppCompatActivity {

    private final int GOOGLE_SIGN_IN = 1;
    private final int FACEBOOK_SIGN_IN = 2;
    private final int FINISH_SIGN_UP_GOOGLE = 3;
    private GoogleSignInClient mGoogleSignInClient;
    private OutWireSignInButton signInButton;
    private OutWireSignInButton googleSignInButton;
    private OutWireSignInButton facebookButton;

    private String tokenId = "";
    private TextView createAccount;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState != null)
            tokenId = savedInstanceState.getString("token_id");

        initializeUI();
        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        createAccount.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        signInButton.setOnClickListener(v -> signInValidation());
        googleSignInButton.setOnClickListener(v -> signInGoogle());


    }


    @Override
    public void onStart() {
        super.onStart();
        hideProgressBar();

        // Check if user is signed in (non-null) and update UI accordingly.

        if (FirebaseHelper.getFirebaseUser() != null) {
            if (FirebaseHelper.getFirebaseUser().isEmailVerified())
                startMainActivity();
        }
    }

    private void initializeUI() {
        createAccount = findViewById(R.id.create_account_text);
        signInButton = findViewById(R.id.sign_in_button);
        googleSignInButton = findViewById(R.id.google_sign_button);
        facebookButton = findViewById(R.id.facebook_sign_button);
        initializeGoogleSignIn();

    }

    private void initializeGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInGoogle() {
        mGoogleSignInClient.signOut();
        showProgressBar();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken, int requestCode) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseHelper.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Login", "signInWithCredential:success");

                        if (requestCode == FINISH_SIGN_UP_GOOGLE) {
                            saveUserDataToFirebase();
                        } else if (requestCode == GOOGLE_SIGN_IN) {
                            loginSuccessfulToast();
                            startMainActivity();
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Login", "signInWithCredential:failure", task.getException());

                    }
                });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("token_id", tokenId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                assert account != null;
                tokenId = account.getIdToken();
                FirebaseHelper.getUserCollection()
                        .whereEqualTo("userEmail", account.getEmail())
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {

                    //If firestore document exists login else complete registration to create document
                    if (!queryDocumentSnapshots.isEmpty()) {
                        User user = null;
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                            user = documentSnapshot.toObject(User.class);

                        //if account already has email and password auth, show account already exists error,
                        //sign in using other auth providers and link auth methods in account settings

                        if (user != null && user.isWithEmailLogin() && !user.isWithGoogleLogin()) {
                            wrongAuthAlert();
                            mGoogleSignInClient.signOut();

                        } else {
                            SharedPreferencesHelper.UserPreferences.saveUser(user, this);

                            firebaseAuthWithGoogle(account.getIdToken(), requestCode);
                        }

                    } else {
                        User user = new User();
                        user.setUserEmail(account.getEmail());
                        user.setDisplayName(account.getDisplayName());
                        user.setPhotoUri(Objects.requireNonNull(account.getPhotoUrl()).toString());
                        user.setWithGoogleLogin(true);

                        SharedPreferencesHelper.UserPreferences.saveUserRegister(user, this);

                        Intent intent = new Intent(LoginActivity.this, CompleteRegistrationActivity.class);
                        startActivityForResult(intent, FINISH_SIGN_UP_GOOGLE);
                    }
                });


            } catch (ApiException e) {

                Log.w("login", "Google sign in failed", e);
                String text = getString(R.string.google_sign_in_failed);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
                hideProgressBar();
            }
        } else if (requestCode == FACEBOOK_SIGN_IN) {

        } else if (requestCode == FINISH_SIGN_UP_GOOGLE) {

            if (resultCode == Activity.RESULT_OK)
                firebaseAuthWithGoogle(tokenId, requestCode);

        }
    }


    public void signInValidation() {


        EditText userCredentialEditText = findViewById(R.id.login_user_credential);
        EditText passwordEditText = findViewById(R.id.login_password);


        if (getEditTextString(passwordEditText).isEmpty()) {
            passwordEditText.setError(getString(R.string.password_field_empty));
        }

        if (isValidEmail(getEditTextString(userCredentialEditText).trim(), userCredentialEditText)) {
            showProgressBar();
            signIn(getEditTextString(userCredentialEditText).trim(),
                    getEditTextString(passwordEditText));

        }


    }

    private void signIn(String email, String password) {

        FirebaseHelper.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseHelper.getUserCollection()
                                .whereEqualTo("userEmail", email)
                                .get().addOnSuccessListener(queryDocumentSnapshots -> {

                            if (!queryDocumentSnapshots.isEmpty()) {
                                User user = new User();
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                    user = documentSnapshot.toObject(User.class);

                                SharedPreferencesHelper.UserPreferences.saveUser(user, this);

                                if (FirebaseHelper.getFirebaseUser().isEmailVerified()) {

                                    loginSuccessfulToast();
                                    startMainActivity();

                                } else if (user != null && (System.currentTimeMillis() - user.getTimeOfRegistration()) / 60000 > 60) {
                                    //delete user and user data if user is unverified for more than one hour
                                    FirebaseHelper.deleteUserFromFirebase();
                                    invalidAccountAlert();
                                } else {
                                    //if email is unverified in less than one hour show alert
                                    FirebaseHelper.getAuth().signOut();
                                    unverifiedEmailAlert();
                                }


                            } else {
                                String text = getString(R.string.problem_ocurred);
                                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                                toast.show();
                                FirebaseHelper.getAuth().signOut();
                                hideProgressBar();
                            }
                        });


                    } else {
                        Log.e("Login:", "Wrong credentials");
                        wrongCredentialsAlert(email);

                    }
                });
    }

    private void startMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        hideProgressBar();
        this.finish();
    }

    private void wrongAuthAlert() {

        OutWireAlertDialog alertDialog = new OutWireAlertDialog(LoginActivity.this) {
            @Override
            public void leftButtonOnClick() {
                super.leftButtonOnClick();
                dismiss();
            }
        };

        alertDialog.create();
        alertDialog.setTitle(getString(R.string.email_in_use));
        alertDialog.setMessage(getString(R.string.link_auth_methods));
        alertDialog.setTextButtonLeft(getString(R.string.try_again));
        alertDialog.setLeftButtonVisibility(true);
        alertDialog.show();
        hideProgressBar();
    }

    private void wrongCredentialsAlert(String e) {

        OutWireAlertDialog alertDialog = new OutWireAlertDialog(LoginActivity.this) {
            @Override
            public void leftButtonOnClick() {
                super.leftButtonOnClick();
                dismiss();
            }
        };

        alertDialog.create();
        alertDialog.setTitle(getString(R.string.alert_incorrect_password_title) + e);
        alertDialog.setMessage(getString(R.string.alert_message_incorrect_password));
        alertDialog.setTextButtonLeft(getString(R.string.try_again));
        alertDialog.setLeftButtonVisibility(true);
        alertDialog.show();
        hideProgressBar();

    }

    private void unverifiedEmailAlert() {
        OutWireAlertDialog alertDialog = new OutWireAlertDialog(LoginActivity.this) {
            @Override
            public void leftButtonOnClick() {
                super.leftButtonOnClick();
                dismiss();
            }
        };

        alertDialog.create();
        alertDialog.setTitle(getString(R.string.verify_email));
        alertDialog.setMessage(getString(R.string.pls_verify_email_data_will_be_deleted));
        alertDialog.setTextButtonLeft(getString(R.string.ok));
        alertDialog.setLeftButtonVisibility(true);
        alertDialog.show();
        hideProgressBar();

    }


    private void invalidAccountAlert() {

        OutWireAlertDialog alertDialog = new OutWireAlertDialog(LoginActivity.this) {
            @Override
            public void leftButtonOnClick() {
                super.leftButtonOnClick();
                dismiss();
            }
        };

        alertDialog.create();
        alertDialog.setTitle(getString(R.string.invalid_account));
        alertDialog.setMessage(getString(R.string.invalid_acc_pls_register));
        alertDialog.setTextButtonLeft(getString(R.string.ok));
        alertDialog.setLeftButtonVisibility(true);
        alertDialog.show();
        hideProgressBar();

    }


    private void showProgressBar() {
        findViewById(R.id.login_progressbar).setVisibility(View.VISIBLE);
        findViewById(R.id.view).setVisibility(View.VISIBLE);
        findViewById(R.id.view).setEnabled(false);
    }

    private void hideProgressBar() {
        findViewById(R.id.login_progressbar).setVisibility(View.GONE);
        findViewById(R.id.view).setVisibility(View.GONE);
        findViewById(R.id.view).setEnabled(true);
    }

    private void loginSuccessfulToast() {
        String text = getString(R.string.login_successful);
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void registrationSuccessfulToast() {
        String text = getString(R.string.register_successful);
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }


    private void saveUserDataToFirebase() {

        User user = SharedPreferencesHelper.UserPreferences.getUser(this);

        FirebaseHelper.getUserCollection()
                .whereEqualTo("userEmail", user.getUserEmail())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (!queryDocumentSnapshots.isEmpty()) {
                invalidAccountAlert();
                SharedPreferencesHelper.UserPreferences.removeUserRegister(getBaseContext());
                SharedPreferencesHelper.UserPreferences.removeUser(getBaseContext());
            } else {

                showProgressBar();
                FirebaseStorageHelper transfer = new FirebaseStorageHelper() {

                    @Override
                    public void executeOnFailure() {
                        super.executeOnFailure();
                        FirebaseHelper.getUserReference().set(user)
                                .addOnSuccessListener(aVoid -> {
                                    hideProgressBar();
                                    registrationSuccessfulToast();
                                    startMainActivity();
                                });
                    }

                    @Override
                    public void executeOnSuccess() {
                        super.executeOnSuccess();
                        user.setPhotoUri(getDownloadUri().toString());

                        SharedPreferencesHelper.UserPreferences.saveUser(user, getBaseContext());

                        FirebaseHelper.getUserReference().set(user)
                                .addOnSuccessListener(aVoid -> {
                                    hideProgressBar();
                                    registrationSuccessfulToast();
                                    startMainActivity();
                                });
                    }
                };

                transfer.uploadFile(this, FirebaseStorageHelper.getStream(),
                        "images/" + user.getPhotoUri(),
                        getString(R.string.img_upload_failed));
            }
    });


}
}