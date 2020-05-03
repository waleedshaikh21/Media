package com.example.media;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import Fragments.CameraFragment;
import Fragments.HomeFragment;
import Fragments.ProfileFragment;
import Fragments.SearchFragment;
import Fragments.UploadFragment;

public class MainPage extends AppCompatActivity {

    private NavigationView objectNavigationView;
    private ImageView headerIV;

    TextView name, mail;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseAuth;
    private DatabaseReference reference;

    BottomNavigationView objecBNV;
    HomeFragment homeFragment;

    ProfileFragment profileFragment;
    SearchFragment searchFragment;

    UploadFragment uploadFragment;
    private GoogleSignInClient mGoogleSignInClient;

    private DrawerLayout objectDrawerLayout;
    private Toolbar objectToolbar;

    CameraFragment cameraFragment;
    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        contextOfApplication = getApplicationContext();

        try {
            homeFragment = new HomeFragment();
            profileFragment = new ProfileFragment();

            searchFragment = new SearchFragment();
            uploadFragment = new UploadFragment();

            getStartedObjects();
            cameraFragment = new CameraFragment();

            objecBNV = findViewById(R.id.BNV);
            objecBNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.item_home:
                            changeFragment(homeFragment);
                            return true;
                        case R.id.item_Search:
                            changeFragment(searchFragment);
                            return true;

                        case R.id.item_Camera:
                            changeFragment(cameraFragment);
                            return true;
                        case R.id.item_upload:
                            changeFragment(uploadFragment);
                            return true;
                        case R.id.item_profile:
                            changeFragment(profileFragment);
                            return true;
                        default:
                            return false;
                    }
                }
            });

        } catch (Exception Ex) {
            Toast.makeText(this, "onCreate: " + Ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getStartedObjects() {
        try {
            objectNavigationView = findViewById(R.id.navigationView);
            View navHeader = objectNavigationView.getHeaderView(0);

            headerIV = navHeader.findViewById(R.id.header_profileIV);
            name = navHeader.findViewById(R.id.name);
            mail = navHeader.findViewById(R.id.mail);

            firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
            if( firebaseAuth !=null ){
                name.setText(firebaseAuth.getDisplayName());
                mail.setText(firebaseAuth.getEmail());
            }

            mAuth = FirebaseAuth.getInstance();
            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (signInAccount != null) {
                name.setText(signInAccount.getDisplayName());
                mail.setText(signInAccount.getEmail());
            }
            objectDrawerLayout = findViewById(R.id.drawerLayout);

            objectToolbar = findViewById(R.id.ourToolBar);
            headerIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeFragment(profileFragment);
                    Toast.makeText(MainPage.this, "Opening Profile", Toast.LENGTH_SHORT).show();
                    closeMyDrawer();
                }
            });
            objectNavigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            if (item.getItemId() == R.id.item_Archive) {
                                Toast.makeText(MainPage.this, "Archive here", Toast.LENGTH_SHORT).show();
                                closeMyDrawer();
                                return true;
                            } else if (item.getItemId() == R.id.item_Activity) {
                                Toast.makeText(MainPage.this, "Activity here", Toast.LENGTH_SHORT).show();
                                closeMyDrawer();
                                return true;
                            } else if (item.getItemId() == R.id.Nametag) {
                                Toast.makeText(MainPage.this, "NameTag here", Toast.LENGTH_SHORT).show();
                                closeMyDrawer();
                                return true;
                            } else if (item.getItemId() == R.id.CloseF) {
                                Toast.makeText(MainPage.this, "Close Firends here", Toast.LENGTH_SHORT).show();
                                closeMyDrawer();
                                return true;
                            } else if (item.getItemId() == R.id.Discover) {
                                Toast.makeText(MainPage.this, "Opening Discover", Toast.LENGTH_SHORT).show();
                                closeMyDrawer();
                                changeFragment(searchFragment);
                                return true;
                            } else if (item.getItemId() == R.id.Saved) {
                                Toast.makeText(MainPage.this, "Saved here", Toast.LENGTH_SHORT).show();
                                closeMyDrawer();
                                return true;
                            } else if (item.getItemId() == R.id.Settings) {
                                Intent intent = new Intent(getApplicationContext(), Settings.class);
                                startActivity(intent);
                                Toast.makeText(MainPage.this, "Opening Settings", Toast.LENGTH_SHORT).show();
                                closeMyDrawer();
                                return true;
                            }
                            return false;
                        }
                    }
            );

            setUpHamBurgerIcon();
        } catch (Exception e) {
            Toast.makeText(this, "getStartedObjects:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void closeMyDrawer() {
        try {
            objectDrawerLayout.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            Toast.makeText(this, "closeMyDrawer:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpHamBurgerIcon() {
        try {
            ActionBarDrawerToggle objectActionBarDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    objectDrawerLayout,
                    objectToolbar,
                    R.string.open
                    , R.string.close
            );

            objectActionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.
                    color.colorWhite));

            objectActionBarDrawerToggle.syncState();
        } catch (Exception e) {
            Toast.makeText(this, "setUpHamBurgerIcon:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void changeFragment(Fragment object) {
        try {
            FragmentTransaction objectFragmentTransaction = getSupportFragmentManager().beginTransaction();
            objectFragmentTransaction.replace(R.id.FL, object);
            objectFragmentTransaction.commit();
        } catch (Exception ex) {
            Toast.makeText(this, "ChangeFragment: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
}
