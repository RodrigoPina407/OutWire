package com.outwire.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.outwire.R;
import com.outwire.custom.views.OutWireAlertDialog;
import com.outwire.fragments.main.ChatFragment;
import com.outwire.fragments.main.FeedFragment;
import com.outwire.fragments.main.HomeFragment;
import com.outwire.fragments.main.ProfileFragment;
import com.outwire.login.LoginActivity;
import com.outwire.objects.User;
import com.outwire.util.FirebaseHelper;
import com.outwire.util.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;

    private Fragment homeFragment;
    private Fragment feedFragment;
    private Fragment chatFragment;
    private Fragment profileFragment;

    private FragmentManager fragmentManager;

    private User mUser = null;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab = findViewById(R.id.create_event_fab);

        getUserInfo();


        if (savedInstanceState == null) {
            initializeFragments();
        } else {
            retrieveFragments();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        setupToolbar();
        setupFAB();
        setupBottomNavigation();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (!homeFragment.isVisible())
            bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
        else
            super.onBackPressed();
    }


    private void setupToolbar() {

        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerElevation(12f);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        ConstraintLayout content = findViewById(R.id.content);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
            }
        };


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void signOut() {

        FirebaseHelper.getAuth().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        mGoogleSignInClient.signOut();

        SharedPreferencesHelper.removeKey(this,
                getString(R.string.user_shared_file), getString(R.string.shared_pref_user_key));

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);

        finish();
    }

    private void confirmSignOut() {

        OutWireAlertDialog alert = new OutWireAlertDialog(MainActivity.this) {
            @Override
            public void leftButtonOnClick() {
                super.leftButtonOnClick();
                dismiss();

            }

            @Override
            public void rightButtonOnClick() {
                super.rightButtonOnClick();
                dismiss();
                signOut();
            }
        };

        alert.setLeftButtonVisibility(true);
        alert.setRightButtonVisibility(true);

        alert.setTextButtonRight(getString(R.string.yes));
        alert.setTextButtonLeft(getString(R.string.no));

        alert.setTitle(getString(R.string.sign_out));
        alert.setMessage(getString(R.string.sure_want_signout));
        alert.show();

    }

    private void retrieveFragments() {

        fragmentManager = getSupportFragmentManager();
        homeFragment = fragmentManager.findFragmentByTag("home");
        feedFragment = fragmentManager.findFragmentByTag("feed");
        chatFragment = fragmentManager.findFragmentByTag("chat");
        profileFragment = fragmentManager.findFragmentByTag("profile");

    }

    private void setupBottomNavigation() {


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            FragmentTransaction ft = fragmentManager.beginTransaction();

            if (item.getItemId() == R.id.bottom_nav_home) {
                ft.replace(R.id.fragment_container, homeFragment, "home")
                        .commit();


            } else if (item.getItemId() == R.id.bottom_nav_feed) {
                ft.replace(R.id.fragment_container, feedFragment, "feed")
                        .commit();


            } else if (item.getItemId() == R.id.bottom_nav_chat) {
                ft.replace(R.id.fragment_container, chatFragment, "chat")
                        .commit();


            } else if (item.getItemId() == R.id.bottom_nav_profile) {
                ft.replace(R.id.fragment_container, profileFragment, "profile")
                        .commit();

            }

            return true;
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.drawer_signout) {
            confirmSignOut();
        } else if (item.getItemId() == R.id.drawer_account) {

        } else if (item.getItemId() == R.id.drawer_help) {

        } else if (item.getItemId() == R.id.drawer_notifications) {


        } else if (item.getItemId() == R.id.drawer_settings) {


        }


        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void initializeFragments() {

        homeFragment = new HomeFragment();
        feedFragment = new FeedFragment();
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment, "home")
                .commit();

    }

    private void initializeHeader() {

        View navHeader = navigationView.getHeaderView(0);
        TextView navEmail = navHeader.findViewById(R.id.nav_email_text);
        TextView navDisplayName = navHeader.findViewById(R.id.nav_display_name);
        TextView navUserName = navHeader.findViewById(R.id.nav_username_text);
        ImageView navProfile = navHeader.findViewById(R.id.nav_profile);

        int targetWidth = navProfile.getLayoutParams().width;
        int targetHeight = navProfile.getLayoutParams().height;


        if (mUser.getPhotoUri() != null) {

            Glide.with(this)
                    .load(mUser.getPhotoUri())
                    .apply(new RequestOptions()
                            .override(targetWidth, targetHeight)
                            .circleCrop())
                    .into(navProfile);
        } else
            Glide.with(this)
                    .load(R.drawable.logo_transparent)
                    .apply(new RequestOptions()
                            .override(targetWidth, targetHeight)
                            .circleCrop())
                    .into(navProfile);


        navEmail.setText(mUser.getUserEmail());

        navDisplayName.setText(mUser.getDisplayName());

        String atUsername = "@" + mUser.getUserName();

        navUserName.setText(atUsername);


    }

    private void getUserInfo() {

        if (SharedPreferencesHelper.checkKeyExists(this,
                getString(R.string.user_shared_file), getString(R.string.shared_pref_user_key))) {

            mUser = SharedPreferencesHelper
                    .getFromJson(this, User.class,
                            getString(R.string.user_shared_file), getString(R.string.shared_pref_user_key));

            initializeHeader();
        }
        else signOut();

    }


    private void setupFAB() {

        fab.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivity(i);
        });


    }

}