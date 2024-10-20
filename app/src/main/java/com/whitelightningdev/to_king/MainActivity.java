package com.whitelightningdev.to_king;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the toolbar as the app bar

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Setup drawer toggle (hamburger icon)
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Ensure ActionBar is not null

        // Create a map for menu items and their corresponding fragments
        Map<Integer, Fragment> menuFragmentMap = new HashMap<>();
        menuFragmentMap.put(R.id.nav_jwt, new JwtTokenFragment());
        menuFragmentMap.put(R.id.nav_oauth_access, new OAuthAccessTokenFragment());
        menuFragmentMap.put(R.id.nav_oauth_refresh, new OAuthRefreshTokenFragment());
        menuFragmentMap.put(R.id.nav_saml, new SAMLTokenFragment());
        menuFragmentMap.put(R.id.nav_api_keys, new ApiKeyFragment());
        menuFragmentMap.put(R.id.nav_csrf, new CSRFTokenFragment());
        menuFragmentMap.put(R.id.nav_uuid, new UUIDTokenFragment());
        menuFragmentMap.put(R.id.nav_hmac, new HMACFragment());
        menuFragmentMap.put(R.id.nav_bearer, new BearerTokenFragment());
        menuFragmentMap.put(R.id.nav_session, new SessionTokenFragment());
        menuFragmentMap.put(R.id.nav_pat, new PATFragment());
        menuFragmentMap.put(R.id.nav_encrypted, new EncryptedTokenFragment());
        menuFragmentMap.put(R.id.nav_erc20, new ERC20TokenFragment());
        menuFragmentMap.put(R.id.nav_erc721, new ERC721TokenFragment());

        // Handle navigation menu item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragment = menuFragmentMap.get(item.getItemId());
            if (fragment != null) {
                loadFragment(fragment);
            }

            // Close the drawer after selection
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        // Replace the fragment in the content frame
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the drawer toggle
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
