package com.example.superchat.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.superchat.Fragments.CallFragments;
import com.example.superchat.Fragments.ChatsFragment;
import com.example.superchat.Fragments.StatusFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case  0: return new ChatsFragment();
            case  1: return new StatusFragment();
            case  2: return new CallFragments();

            default: return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position==0){
            title = "Chats";
        }
        if (position==1){
            title = "";
        }
        if (position==2){
            title = "";
        }
        return title;
    }
}
