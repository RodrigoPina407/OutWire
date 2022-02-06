package com.outwire.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.outwire.R;
import com.outwire.fragments.RecyclerFragment;
import com.outwire.objects.PagerAdapter;
import com.outwire.objects.User;
import com.outwire.util.HelperMethods;
import com.outwire.util.SharedPreferencesHelper;
import com.outwire.util.UserHelper;

public class ProfileFragment extends Fragment {

    private View mView;
    private User mUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile_layout, container, false);
        mUser = SharedPreferencesHelper.UserPreferences.getUser(getContext());
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupProfilePicture();
        setupTabLayout();


    }

    private void setupTabLayout() {

        TabLayout tabLayout = mView.findViewById(R.id.profile_tabLayout);
        int[] tabs = {R.string.my_events, R.string.going};

        ViewPager2 viewPager2 = mView.findViewById(R.id.profile_pager);
        viewPager2.setSaveEnabled(false);

        viewPager2.setAdapter(new PagerAdapter(this, new Fragment[]{new RecyclerFragment(), new RecyclerFragment()}));
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(tabs[position])
        ).attach();

    }

    private void setupProfilePicture() {

        ImageView img = mView.findViewById(R.id.image_profile);

        int targetWidth = img.getLayoutParams().width;
        int targetHeight = img.getLayoutParams().height;

        Glide.with(this)
                .load(mUser.getPhotoUri())
                .apply(new RequestOptions()
                        .override(targetWidth, targetHeight)
                        .circleCrop())
                .into(img);

    }
}
