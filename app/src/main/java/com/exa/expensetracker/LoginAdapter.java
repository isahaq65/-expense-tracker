package com.exa.expensetracker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class LoginAdapter extends FragmentPagerAdapter {
    private Context context;
        int totalTabs;

    public LoginAdapter(@NonNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    public Fragment getItem(int position){
        switch (position){
            case 0:
                LoginTabFragment loginTabFragment= new LoginTabFragment();
                return loginTabFragment;
            case 1:
                SignupTabFrament signupTabFrament= new SignupTabFrament();
                return signupTabFrament;
            default:
                return null;

        }
    }

}
