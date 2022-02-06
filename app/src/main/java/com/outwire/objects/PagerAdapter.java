package com.outwire.objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.outwire.fragments.RecyclerFragment;

public class PagerAdapter extends FragmentStateAdapter {

    Fragment[] fragmentToCreate;

    public PagerAdapter(@NonNull Fragment fragment, Fragment[] fragmentToCreate) {
        super(fragment);
        this.fragmentToCreate = fragmentToCreate;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentToCreate[position];
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
