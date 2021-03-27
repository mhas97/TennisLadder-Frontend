package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
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

        /* Fetch user data from the bundle. */
        Intent mainIntent = getIntent();
        Bundle mainExtras = mainIntent.getExtras();
        user = mainExtras.getParcelable("user");

        /* Identify and create both tab layout and view pager. */
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        /* Connect the view pager and tab layout so the highlighted tab corresponds to the correct fragment. */
        tabLayout.setupWithViewPager(viewPager);

        /* Create a tab for the 3 primary fragments. */
        tabLayout.addTab(tabLayout.newTab().setText("Challenges"));
        tabLayout.addTab(tabLayout.newTab().setText("Ladder"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));

        /* Create an adapter and attach it to the identified view pager. */
        MainAdapter mainAdapter= new MainAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(mainAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    /**
     * To ensure that forms aren't re-entered, the back button acts as a logout button.
     */
    @Override
    public void onBackPressed() {
        /* Create a dialog to check if the user wants to logout. */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            /* Navigate to the login screen */
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        builder.setNegativeButton("No", (dialog, which) -> {    // Cancel.
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Fragments use of this function to acquire app user data.
     * @return TennisUser object.
     */
    public static TennisUser getUser() {
        return user;
    }

    /**
     * An adapter to handle the switching between fragments. This includes
     * returning the corresponding view, as well as the correct tab names.
     */
    private static class MainAdapter extends FragmentPagerAdapter {

        private final int numTabs;

        public MainAdapter(FragmentManager fm, int numTabs) {
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
            // In the case of an error, return the challenges fragment.
            return new ChallengesFragment();
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