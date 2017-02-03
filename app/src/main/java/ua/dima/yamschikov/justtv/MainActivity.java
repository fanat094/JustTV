package ua.dima.yamschikov.justtv;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

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
    String push;
    SharedPreferences prefs;
    CallbackManager callbackManager;
    SharedPreferences.Editor editor;
    final String SAVED_TEXT_GN = "saved_text_googleName";
    final String SAVED_TEXT_GE = "saved_text_googleName";
    final String SAVED_TEXT_FK = "saved_text_facebook";


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

        Log.d("QQ","QQ");

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        headerview = navigationView.getHeaderView(0);
        nameProfile = (TextView) headerview.findViewById(R.id.name_profile);
        //nameProfile.setText("t1");
        emailProfile = (TextView) headerview.findViewById(R.id.email_profile);
        loginBtn = (ImageView) headerview.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

      /*  if(prefs!=null) {
            loadText();
        }else {
            nameProfile.setText("ZAL");
        }*/
        loadText();
        loadSharedPrefs("MY_PREFS_JUSTTV");
    }

    void loadText() {

        prefs = getSharedPreferences("MY_PREFS_JUSTTV", MODE_PRIVATE);
        editor = getSharedPreferences("MY_PREFS_JUSTTV", MODE_PRIVATE).edit();
        String restoredText = prefs.getString(SAVED_TEXT_GN, null);
        String restoredText2 = prefs.getString(SAVED_TEXT_GE, null);
        if (restoredText!= null && restoredText2!= null) {
            nameProfile.setText(restoredText);
            emailProfile.setText(restoredText2);
            Log.d("LOADD1", restoredText);
            Log.d("LOAD2", restoredText2);
        }

        try {
            String restoredText3 = prefs.getString(SAVED_TEXT_FK, null);
            response = new JSONObject(restoredText3);
            nameProfile.setText(response.get("name").toString());
            Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show();
            emailProfile.setText(response.get("email").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Glide.with(this).load(profile_pic_url.getString("url"))
                    .into(loginBtn);

        } catch (Exception e) {
            e.printStackTrace();
        }


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

                prefs = getSharedPreferences("MY_PREFS_JUSTTV", MODE_PRIVATE);
                editor = getSharedPreferences("MY_PREFS_JUSTTV", MODE_PRIVATE).edit();
                String restoredText = prefs.getString(SAVED_TEXT_GN, null);
                String restoredText2 = prefs.getString(SAVED_TEXT_GE, null);
                String restoredText3 = prefs.getString(SAVED_TEXT_FK, null);
                if (restoredText == null && restoredText2 == null && restoredText3 == null) {

                    Intent i = new Intent(MainActivity.this, AutentificacionAvtivity.class);
                    startActivityForResult(i, 1);

                }else {

                    loginBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle( "Cast Recording" )
                                    .setMessage( "Вийти з акаунту?" )
                                    .setPositiveButton( "Так", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.d( "AlertDialog", "Positive" );
                                            LoginManager.getInstance().logOut();
                                            editor.remove(SAVED_TEXT_GN);
                                            editor.remove(SAVED_TEXT_GE);
                                            editor.remove(SAVED_TEXT_FK);
                                            editor.commit();
                                            Log.d("REMOVE","REMOVE");
                                        }
                                    })
                                    .setNegativeButton( "Ні", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.d( "AlertDialog", "Negative" );
                                        }
                                    } )
                                    .show();
                        }
                    });

                    /*AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Alert message to be shown");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    LoginManager.getInstance().logOut();
                                    editor.remove(SAVED_TEXT);
                                    editor.remove("SAVED_TEXT2");
                                    editor.commit();
                                    Log.d("REMOVE","REMOVE");
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();*/
                }
                break;
            default:
                break;
        }
    }

    public void loadSharedPrefs(String ... prefs) {

        // Logging messages left in to view Shared Preferences. I filter out all logs except for ERROR; hence why I am printing error messages.

        Log.i("Loading Shared Prefs", "-----------------------------------");
        Log.i("----------------", "---------------------------------------");

        for (String pref_name: prefs) {

            SharedPreferences preference = getSharedPreferences(pref_name, MODE_PRIVATE);
            for (String key : preference.getAll().keySet()) {

                Log.i(String.format("Shared Preference JustTV : %s - %s", pref_name, SAVED_TEXT_GN),
                        preference.getString(key, "error!"));
                Log.i(String.format("Shared Preference JustTV : %s - %s", pref_name, SAVED_TEXT_GE),
                        preference.getString(key, "error!"));
                Log.i(String.format("Shared Preference JustTV : %s - %s", pref_name, SAVED_TEXT_FK),
                        preference.getString(key, "error!"));

            }

            Log.i("----------------", "---------------------------------------");

        }

        Log.i("Finished Shared Prefs", "----------------------------------");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
        Intent intent = data;
        jsondata = intent.getStringExtra("jsondata");

        Intent gintent = data;
        gname = gintent.getStringExtra("gname");
        gemail = gintent.getStringExtra("gemail");
        gpic = gintent.getStringExtra("gpic");

        Intent intentcheck = data;

        int check = intentcheck.getIntExtra("CHECK", 3);
        Log.d("check", Integer.toString(check));

        if (check == 0) {

            setUserProfile(jsondata);

        } else if (check == 1) {
            setUserProfileGoogle();
        }
    }
    }
}
