package com.outwire.fragments.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.outwire.R;
import com.outwire.main.MainActivity;
import com.outwire.objects.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home_layout, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configLayout(view);
    }


    private void configLayout(View v) {

        RecyclerView recyclerView = v.findViewById(R.id.home_recyclerview);


        List<String> x = new ArrayList<>();
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("Teste");
        x.add("TesteFim");
        RecyclerAdapter adapter = new RecyclerAdapter(x);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));


    }
}
