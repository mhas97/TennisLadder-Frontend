package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Signup"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        LoginSignupAdapter loginSignupAdapter = new LoginSignupAdapter(getSupportFragmentManager(), getApplicationContext(), 2);
        viewPager.setAdapter(loginSignupAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private class LoginSignupAdapter extends FragmentPagerAdapter {

        Context context;
        int numTabs;

        public LoginSignupAdapter(FragmentManager fm, Context context, int numTabs) {
            super(fm);
            this.context = context;
            this.numTabs = numTabs;
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LoginFragment();
                case 1:
                    return new SignupFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return numTabs;
        }
    }
}