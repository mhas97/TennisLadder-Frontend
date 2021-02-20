package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private static TennisUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In order to track the logged in user, send some important info from the login procedure.
        // This is passed and retrieved using intents, in this case my player class implements the
        // Parcelable interface, allowing objects to be passed between activities.
        Intent mainIntent = getIntent();
        Bundle mainExtras = mainIntent.getExtras();
        user = mainExtras.getParcelable("user");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Challenges"));
        tabLayout.addTab(tabLayout.newTab().setText("Ladder"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        MainAdapter mainAdapter= new MainAdapter(getSupportFragmentManager(), getApplicationContext(), 3);
        viewPager.setAdapter(mainAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public static TennisUser getUser() {
        return user;
    }

    private static class MainAdapter extends FragmentPagerAdapter {

        private final int numTabs;

        public MainAdapter(FragmentManager fm, Context context, int numTabs) {
            super(fm);
            this.numTabs = numTabs;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ChallengesFragment();
                case 1:
                    return new LadderFragment();
                case 2:
                    return new ProfileFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Challenges";
                case 1:
                    return "Ladder";
                case 2:
                    return "Profile";
            }
            return null;
        }

        @Override
        public int getCount() {
            return numTabs;
        }
    }
}