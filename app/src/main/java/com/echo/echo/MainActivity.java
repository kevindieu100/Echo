package com.echo.echo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.design.widget.NavigationView;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    NavigationView navigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mainFragment fragment = new mainFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile_Button) {
            Intent fromMainToMyProfileIntent = new Intent(getApplicationContext(),MyProfileActivity.class);
            startActivity(fromMainToMyProfileIntent);

        } else if (id == R.id.MyConnection_Button) {
            Intent fromMainToViewConIntent = new Intent(getApplicationContext(),ManageConnectionsActivity.class);
            startActivity(fromMainToViewConIntent);
        } else if (id == R.id.Request_Button) {
            Intent fromMainToAddUserIntent = new Intent(getApplicationContext(), Add_By_Username_Activity.class);
            startActivity(fromMainToAddUserIntent);
        } else if (id == R.id.sendText_Button){
            Intent fromMainToSendTextIntent = new Intent(getApplicationContext(), TextGuestActivity.class);
            startActivity(fromMainToSendTextIntent);
        } else if (id == R.id.logout_Button){
            LocalUser user = new LocalUser();
            user.getInstance();
            user.saveUser(user); //saves the user
             Intent IntentLogout = new Intent(getApplicationContext(), LoginActivity.class);
             startActivity(IntentLogout);
        } else if (id == R.id.addConnection_Button) {

        } else if (id == R.id.ViewRequest_Button) {
            Intent IntentRequest = new Intent(getApplicationContext(), RequestListActivity.class);
            startActivity(IntentRequest);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}