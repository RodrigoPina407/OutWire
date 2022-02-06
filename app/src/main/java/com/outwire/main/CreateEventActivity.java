package com.outwire.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.outwire.R;
import com.outwire.custom.views.OutWireSignInButton;
import com.outwire.fragments.register.CompleteRegistrationFragment;
import com.outwire.fragments.register.RegisterEmailFragment;
import com.outwire.objects.Event;
import com.outwire.util.CurrencyTextWatcher;
import com.outwire.util.FirebaseHelper;
import com.outwire.util.SharedPreferencesHelper;

import java.util.Arrays;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class CreateEventActivity extends AppCompatActivity {

    private static final long DAYS = 24 * 60 * 60 * 1000;
    private Event mNewEvent;
    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private EditText mEditTextNumberParticipants;
    private EditText mEditTextPrice;
    private DatePicker mDatePicker;
    private FrameLayout mMapsFrame;

    private SwitchMaterial mSwitchNumberParticipants;
    private SwitchMaterial mSwitchPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initializeUI();

        mNewEvent = new Event();
        mNewEvent.setEventCreator(SharedPreferencesHelper.UserPreferences.getUser(this).getUserName());

    }

    @Override
    protected void onStart() {
        super.onStart();
        setupParticipantsSwitch();
        setupPriceSwitch();
        setupToolbar();
        setupCreateEventButton();
    }

    private void initializeUI() {

        mEditTextTitle = findViewById(R.id.edittext_event_title);
        mEditTextDescription = findViewById(R.id.editText_event_description);
        mEditTextNumberParticipants = findViewById(R.id.editText_number_participants);
        mEditTextPrice = findViewById(R.id.edittText_event_price);
        mSwitchNumberParticipants = findViewById(R.id.switch_event_number_of_participants);
        mSwitchPrice = findViewById(R.id.switch_event_price);
        mDatePicker = findViewById(R.id.date_picker_event);

        mDatePicker.setMinDate(System.currentTimeMillis() - 1000);
        mDatePicker.setMaxDate(System.currentTimeMillis() + DAYS * 60);


        Places.initialize(getApplicationContext(), "AIzaSyCZYGwokaCXCg7TeeBHKoKhssBQGMV_uLw");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);
            }
        });




    }

    private void setupParticipantsSwitch(){

        mSwitchNumberParticipants.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                mEditTextNumberParticipants.setEnabled(true);
                mEditTextNumberParticipants.setHint(R.string._000);

            }
            else{
                mEditTextNumberParticipants.setEnabled(false);
                mEditTextNumberParticipants.setText("0");
                mEditTextNumberParticipants.setHint(R.string.infinity_symbol);
            }
        });
    }

    private void setupPriceSwitch(){

        CurrencyTextWatcher watcher = new CurrencyTextWatcher(mEditTextPrice);
        mSwitchPrice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                mEditTextPrice.addTextChangedListener(watcher);
                mEditTextPrice.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                mEditTextPrice.setEnabled(true);
                mEditTextPrice.setHint(R.string._0_00);
            }
            else{
                mEditTextPrice.removeTextChangedListener(watcher);
                mEditTextPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mEditTextPrice.setEnabled(false);
                mEditTextPrice.setText("0");
                mEditTextPrice.setHint(R.string.no_price);

            }
        });
    }

    private void setupToolbar(){

        MaterialToolbar toolbar = findViewById(R.id.create_event_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

    }

    private void setupCreateEventButton(){

        OutWireSignInButton button = findViewById(R.id.create_event_button);

        button.setOnClickListener(this::onClick);


    }

    private boolean validateEventDetails(){

        return true;
    }

    private void onClick(View v) {

        if (validateEventDetails()) {

            mNewEvent.setEventName(mEditTextTitle.getText().toString());
            mNewEvent.setEventDescription(mEditTextDescription.getText().toString());
            mNewEvent.setNumParticipants(Integer.parseInt(mEditTextNumberParticipants.getText().toString()));
            mNewEvent.setPrice(mEditTextPrice.getText().toString());

            Calendar date = Calendar.getInstance();
            date.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            mNewEvent.setDate(date.getTime());

            FirebaseHelper.getUserEventsCollection().document().set(mNewEvent)
                    .addOnSuccessListener(aVoid -> {

                        Toast.makeText(getApplicationContext(), getString(R.string.event_created), Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    });

        }

    }
}