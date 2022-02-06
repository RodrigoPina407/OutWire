package com.outwire.fragments.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.outwire.R;
import com.outwire.main.MainActivity;
import com.outwire.util.FirebaseHelper;
import com.outwire.util.SharedPreferencesHelper;
import com.outwire.util.UserHelper;

import java.util.Objects;

import static com.outwire.util.SharedPreferencesHelper.*;

public class EmailVerificationScreenFragment extends Fragment {

    private Runnable runnable;
    private Handler handler;
    private static long timer = 0;
    private View view;
    private static boolean verificationEmailSent = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null)
            verificationEmailSent = savedInstanceState.getBoolean("verification_sent");

        view = inflater.inflate(R.layout.fragment_emailverification, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupSendEmailButton();
        if(!verificationEmailSent)
            sendVerificationEmail();
        setupIsVerifiedListener();
    }

    private void sendVerificationEmail() {

        FirebaseHelper.getFirebaseUser().sendEmailVerification()
                .addOnSuccessListener(task -> {

                    startTimer();
                    verificationEmailSentToast();
                    verificationEmailSent = true;

                });


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("verification_sent", verificationEmailSent);
    }

    private void startTimer() {

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer = millisUntilFinished / 1000;
            }

            @Override
            public void onFinish() {
                timer = 0;
                verificationEmailSent = false;
            }
        }.start();

    }

    private void setupSendEmailButton() {

        TextView button = view.findViewById(R.id.send_verification_again_textview);

        button.setOnClickListener(v -> {

            if (verificationEmailSent)
                Toast.makeText(getContext(), getString(R.string.send_again_in) + timer + "s", Toast.LENGTH_SHORT)
                        .show();
            else
                sendVerificationEmail();


        });


    }

    private void setupIsVerifiedListener() {

        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                FirebaseHelper.getFirebaseUser().reload().addOnSuccessListener(a -> {

                    if (FirebaseHelper.getFirebaseUser().isEmailVerified())
                        signIn();
                    else
                        handler.postDelayed(this, 1000);
                });

            }
        };

        handler.postDelayed(runnable, 1000);
    }

    private void signIn() {

        registrationSuccessfulToast();
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        requireActivity().finish();

    }

    private void verificationEmailSentToast() {

        Toast.makeText(getContext(), getString(R.string.verification_email_sent_to) +
                UserPreferences.getUserRegister(getContext()).getUserEmail(), Toast.LENGTH_SHORT)
                .show();

    }

    private void registrationSuccessfulToast() {
        Toast.makeText(getContext(), getString(R.string.register_successful), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        handler = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        verificationEmailSent = false;
    }
}
