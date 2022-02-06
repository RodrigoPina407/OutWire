package com.outwire.custom.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.outwire.R;
import com.outwire.util.HelperMethods;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class CountryCodePicker extends Dialog {

    private final ArrayList<String> listData = new ArrayList<>();
    private final ArrayList<Integer> listPhoneCode = new ArrayList<>();
    private final ArrayList<String> listCountryName = new ArrayList<>();
    private final ArrayList<String> listCountryCode = new ArrayList<>();

    private ArrayAdapter<String> arrayAdapter;
    private String selectedCountry;

    public CountryCodePicker(@NonNull Context context) {
        super(context);
        setupList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.country_code_picker);
        setupListView();
        setupSearchView();
    }


    private void setupSearchView() {

        SearchView search = findViewById(R.id.picker_search);
        search.setSubmitButtonEnabled(false);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (TextUtils.isEmpty(newText)) {
                    arrayAdapter.getFilter().filter(null);
                } else {
                    arrayAdapter.getFilter().filter(newText);
                }

                return true;
            }
        });


    }

    private  void setupList(){

        JSONArray jsonArray = null;
        InputStream inputStream = getContext().getResources().openRawResource(R.raw.country_codes);

        String jsonString = HelperMethods.inputStreamToString(inputStream);

        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < Objects.requireNonNull(jsonArray).length(); i++) {

            try {
                listPhoneCode.add(jsonArray.getJSONObject(i).getInt("phone_code"));
                listCountryName.add(jsonArray.getJSONObject(i).getString("country_en"));
                listCountryCode.add(jsonArray.getJSONObject(i).getString("country_code"));

                listData.add(getContext().getString(R.string.country_code_picker_item, listCountryName.get(i), listPhoneCode.get(i)));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void setupListView() {

        ListView list = findViewById(R.id.picker_list);
        arrayAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                listData);

        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener((parent, view, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            int p = listData.indexOf(item);
            selectedCountry = "+" + listPhoneCode.get(p) + " (" + listCountryCode.get(p) + ")";
            dismiss();

        });

    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public String getDefaultCountry() {

        int i = getIndexFromValue(listCountryName, getContext().getResources().getConfiguration().locale.getDisplayCountry());

        if (i == -1)
            return "+351 (PT)";
        else
            return "+" + listPhoneCode.get(i) + " (" + listCountryCode.get(i) + ")";
    }

    private int getIndexFromValue(ArrayList<String> list, String value) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(value))
                return i;
        }
        return -1;
    }
}
