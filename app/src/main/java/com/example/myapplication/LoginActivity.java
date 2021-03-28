package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import org.jetbrains.annotations.NotNull;

/**
 * Behaves as a fragment manager for both the login and signup activities. An
 * adapter instance is created to handle fragment switching and interaction.
 *
 * The following tutorial on fragment handling was used during implementation:
 * https://www.youtube.com/watch?v=ayKMfVt2Sg4
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Identify tab layout and view pager elements, this allows for fragments to be switched upon user request. */
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        /* Create a tab for both login and signup fragments. */
        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Signup"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        /* Connect the view pager and tab layout so the highlighted tab corresponds to the correct fragment. */
        tabLayout.setupWithViewPager(viewPager);

        /* Create an adapter and attach it to the identified view pager. */
        LoginSignupAdapter loginSignupAdapter = new LoginSignupAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(loginSignupAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    /**
     * An adapter to handle the switching between fragments. This includes
     * returning the corresponding view, as well as the correct tab names.
     */
    private static class LoginSignupAdapter extends FragmentPagerAdapter {

        private final int numTabs;

        public LoginSignupAdapter(FragmentManager fm, int numTabs) {
            super(fm);
            this.numTabs = numTabs;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LoginFragment();
                case 1:
                    return new SignupFragment();
            }
            return new LoginFragment();     // In the case of an error, return the login fragment.
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Login";
                case 1:
                    return "Signup";
            }
            return null;
        }

        @Override
        public int getCount() {
            return numTabs;
        }
    }
}