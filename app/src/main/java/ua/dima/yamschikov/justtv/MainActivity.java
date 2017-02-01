package ua.dima.yamschikov.justtv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    View headerview;
    ImageView loginBtn;
    TextView nameProfile, emailProfile;
    JSONObject response, profile_pic_data, profile_pic_url;
    String jsondata;
    String gname;
    String gemail;
    String gpic;
    final String SAVED_TEXT = "saved_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*Intent intent = getIntent();

        String t1 = intent.getStringExtra("t1");*/

        Log.d("QQ","QQ");

        headerview = navigationView.getHeaderView(0);
        nameProfile = (TextView) headerview.findViewById(R.id.name_profile);
        //nameProfile.setText("t1");
        emailProfile = (TextView) headerview.findViewById(R.id.email_profile);
        loginBtn = (ImageView) headerview.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        loadText();
    }

    void loadText() {
        SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        String restoredText = prefs.getString(SAVED_TEXT, null);
        String restoredText2 = prefs.getString("SAVED_TEXT2", null);
        nameProfile.setText(restoredText);
        emailProfile.setText(restoredText2);
        Log.d("LOADD1",restoredText);
        if (restoredText != null) {
            String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            Log.d("LOAD2",restoredText);
        }
        Log.d("LOAD3",restoredText);
        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
    }


    public  void  setUserProfile(String jsondata){
               try {
                response = new JSONObject(jsondata);
                nameProfile.setText(response.get("name").toString());
                   Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show();
                emailProfile.setText(response.get("email").toString());
                  profile_pic_data = new JSONObject(response.get("picture").toString());
                           profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                   Glide.with(this).load(profile_pic_url.getString("url"))
                   .into(loginBtn);

                    } catch (Exception e){
                e.printStackTrace();
        }
    }

    public  void  setUserProfileGoogle(){
        try {
            nameProfile.setText(gname);
            emailProfile.setText(gemail);
            Toast.makeText(this, "setUserProfileGoogle", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(gpic).into(loginBtn);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                Intent i = new Intent(MainActivity.this, AutentificacionAvtivity.class);
                startActivityForResult(i, 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = data;
        jsondata = intent.getStringExtra("jsondata");

        Intent gintent = data;
        gname = gintent.getStringExtra("gname");
        gemail = gintent.getStringExtra("gemail");
        gpic = gintent.getStringExtra("gpic");

        Intent intentcheck = data;

        int check = intentcheck.getIntExtra("CHECK",3);
        Log.d("check", Integer.toString(check));

        if(check==0) {

            setUserProfile(jsondata);

        }else
        if (check==1){
            setUserProfileGoogle();
        }
    }
}
