package com.outwire.fragments.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.outwire.R;
import com.outwire.custom.views.CountryCodePicker;
import com.outwire.objects.User;
import com.outwire.util.FirebaseHelper;
import com.outwire.util.FirebaseStorageHelper;
import com.outwire.util.SharedPreferencesHelper;

import static com.outwire.util.HelperMethods.getEditTextString;
import static com.outwire.util.HelperMethods.verifyDisplayName;

public class CompleteRegistrationFragment extends Fragment implements RegisterInterface{

    private final int PICK_IMAGE = 1;
    private EditText name;
    private EditText username;
    private ImageView profileImage;
    private Spinner mGenderSpinner;
    private Button mCountryCodeButton;
    private boolean validateUsername = false;
    private User mUser = null;

    private final Phonenumber.PhoneNumber mPhoneNumber = new Phonenumber.PhoneNumber();


    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_complete_registration, container, false);
        return mView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCountryCodeButton = mView.findViewById(R.id.country_button);
        name = view.findViewById(R.id.register_name);
        username = view.findViewById(R.id.register_username);
        profileImage = view.findViewById(R.id.register_image);
        mGenderSpinner = mView.findViewById(R.id.gender_spinner);

        mUser = SharedPreferencesHelper.UserPreferences.getUserRegister(view.getContext());

        setupGenderSpinner();
        setupCountryCodePicker();
        setupUsernameTextWatcher();
        setupPhoneEditText();
        setupProfileImage();

        if (savedInstanceState != null){
            mGenderSpinner.setSelection(savedInstanceState.getInt("selected_gender_index"));
            mCountryCodeButton.setText(savedInstanceState.getString("country_code_text"));
            mPhoneNumber.setCountryCode(savedInstanceState.getInt("country_code_int"));
            mPhoneNumber.setNationalNumber(savedInstanceState.getLong("national_number_long"));

            FirebaseStorageHelper.setStream(mView.getContext(), null);
            mUser.setPhotoUri(null);

        }



    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected_gender_index", mGenderSpinner.getSelectedItemPosition());
        outState.putString("country_code_text", (String) mCountryCodeButton.getText());
        outState.putInt("country_code_int", mPhoneNumber.getCountryCode());
        outState.putLong("national_number_long", mPhoneNumber.getNationalNumber());

    }

    @Override
    public void onStart() {
        super.onStart();

        if (mUser.getDisplayName() != null)
            name.setText(mUser.getDisplayName());

    }

    private void setupProfileImage() {

        profileImage.setOnClickListener(v -> {
            Intent pickImageFileIntent = new Intent(Intent.ACTION_PICK);
            pickImageFileIntent.setType("image/*");


            String pickTitle = getString(R.string.select_img_gallery);
            Intent chooserIntent = Intent.createChooser(pickImageFileIntent, pickTitle);
            startActivityForResult(chooserIntent, PICK_IMAGE);


        });
    }

    public boolean saveValue(Context context) {

        Spinner spinner = mView.findViewById(R.id.gender_spinner);

        boolean validateGender, validatePhone;
        if (spinner.getSelectedItem().toString().equals(mView.getContext().getString(R.string.select_gender))) {
            validateGender = false;
            ((TextView) spinner.getSelectedView()).setError("");
        } else
            validateGender = true;

        if (getEditTextString(username).isEmpty())
            username.setError(getString(R.string.username_three_characters));

        if (PhoneNumberUtil.getInstance().isValidNumber(mPhoneNumber)) {
            String s = "+" + mPhoneNumber.getCountryCode() + mPhoneNumber.getNationalNumber();
            mUser.setUserPhoneNumber(s);
            validatePhone = true;
        } else {
            EditText e = mView.findViewById(R.id.phone_number_edittext);
            e.setError(mView.getContext().getString(R.string.invalide_phone_number));
            validatePhone = false;
        }

        if (verifyDisplayName(name) && validateUsername && validateGender && validatePhone) {
            mUser.setDisplayName(getEditTextString(name).trim());
            mUser.setUserName(getEditTextString(username).trim());

            SharedPreferencesHelper.UserPreferences.saveUserRegister(mUser, context);

            return true;
        }

        return false;
    }

    private void setupUsernameTextWatcher() {

        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                FirebaseHelper.getUserCollection()
                        .whereEqualTo("userName", s.toString())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (s.toString().length() < 3 || s.toString().length() > 30) {
                                username.setError(getString(R.string.username_three_characters));
                                validateUsername = false;
                            } else if (!queryDocumentSnapshots.isEmpty()) {
                                validateUsername = false;
                                username.setError(getString(R.string.username_unavailable));
                            } else
                                validateUsername = true;

                        });
            }
        });


    }

    private void setupGenderSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mView.getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mGenderSpinner.setAdapter(adapter);


    }

    private void setupPhoneEditText() {

        EditText e = mView.findViewById(R.id.phone_number_edittext);

        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty())
                    mPhoneNumber.setNationalNumber(Long.parseLong(s.toString()));
                else
                    mPhoneNumber.setNationalNumber(1L);
            }
        });
    }

    private void setupCountryCodePicker() {


        CountryCodePicker ccp = new CountryCodePicker(mView.getContext());
        mCountryCodeButton.setText(ccp.getDefaultCountry());


        mCountryCodeButton.setOnClickListener(v -> {

            ccp.show();

            ccp.setOnDismissListener(dialog -> {
                String code = ccp.getSelectedCountry();
                if(code != null){
                    mPhoneNumber.setCountryCode(Integer.parseInt(code.replaceAll("[^0-9]", "")));
                    mCountryCodeButton.setText(code);}
            });

        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                int width = profileImage.getLayoutParams().width;
                int height = profileImage.getLayoutParams().height;

                mUser.setPhotoUri(FirebaseStorageHelper
                        .getFilenameFromUri(data.getDataString()));

                FirebaseStorageHelper.setStream(mView.getContext(), data.getData());

                Glide.with(mView.getContext())
                        .load(data.getData())
                        .apply(new RequestOptions()
                                .override(width, height)
                                .circleCrop()
                        /*.diskCacheStrategy(DiskCacheStrategy.DATA)*/)
                        .into(profileImage);



            } else {
                FirebaseStorageHelper.setStream(mView.getContext(), null);
                Toast.makeText(getContext(), getString(R.string.failed_load_img), Toast.LENGTH_SHORT).show();
            }
        }


    }

}














