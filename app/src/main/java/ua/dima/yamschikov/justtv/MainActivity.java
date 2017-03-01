package ua.dima.yamschikov.justtv;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tcking.github.com.giraffeplayer.GiraffePlayer;
import ua.dima.yamschikov.justtv.adapters.BoxAdapter;
import ua.dima.yamschikov.justtv.adapters.ChanelAdapter;
import ua.dima.yamschikov.justtv.constructors.Chanel;

import static ua.dima.yamschikov.justtv.R.array.title_chanels;
import static ua.dima.yamschikov.justtv.R.array.url_chanels;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        AdapterView.OnItemClickListener{

    View headerview;
    ImageView loginBtn;
    TextView nameProfile, emailProfile;
    JSONObject response, profile_pic_data, profile_pic_url;
    String jsondata;
    String gname;
    String gemail;
    String gpic;
    final String SAVED_TEXT = "saved_text";
    SharedPreferences prefs;
    CallbackManager callbackManager;
    SharedPreferences.Editor editor;
    final String SAVED_TEXT_GN = "saved_text_googleName";
    final String SAVED_TEXT_GE = "saved_text_googleEmail";
    final String SAVED_TEXT_GU = "saved_text_googleUrl";
    final String SAVED_TEXT_FK = "saved_text_facebook";
    DrawerLayout drawer;

    //private List<Chanel> chanelList = new ArrayList<>();
    ArrayList<Chanel> chanelList = new ArrayList<Chanel>();
    private RecyclerView recyclerView;
    private ChanelAdapter mAdapter;
    String push;

    ArrayList<Chanel> products = new ArrayList<Chanel>();
    BoxAdapter boxAdapter;
    ListView lvMain;

    ProgressDialog mProgressDialog;

    private GiraffePlayer player;
    public boolean fullScreenOnly;
    public boolean portrait;

    //private EasyVideoPlayer player;
    private static final String TEST_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private static final String newchanel = "rtmp://195.154.10.174:1935/app/novy?st=w_OxsCLwgRcWkSD1QcXg4w";
    private static final String tnt = "http://178.162.205.119:8081/liveg/tnt.stream/playlist.m3u8?wmsAuthSign=c2VydmVyX3RpbWU9Mi85LzIwMTcgNzowMjozNSBQTSZoYXNoX3ZhbHVlPVBPRE9lamR5ZE1TSVpEU2ZkT0w2WkE9PSZ2YWxpZG1pbnV0ZXM9MjAw";
    private static final String stb = "http://194.247.13.104:8081/stb/index.m3u8?wmsAuthSign=c2VydmVyX3RpbWU9OS8xNC8yMDE2IDU6MDE6NTgg172b70d07ec40ac4d7ae67296423addcbkEyZk92TzJrU3V3PT0mdmFsaWRtaW51dGVzPTIwMA==";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        player = new GiraffePlayer(this);
        player.play(TEST_URL);

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
        //loadSharedPrefs("MY_PREFS_JUSTTV");
        //ImageView ff = (ImageView) findViewById(R.id.orel);

       /* recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ChanelAdapter(chanelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);*/

        // создаем адаптер
        //fillData();
        chanelList.clear();
        prepareChanelData();
        boxAdapter = new BoxAdapter(this, chanelList);
        // настраиваем список
        lvMain = (ListView) findViewById(R.id.recycler_view);
        lvMain.setAdapter(boxAdapter);
        lvMain.setOnItemClickListener(this);

       // player = (EasyVideoPlayer) findViewById(R.id.player);
        // player = new EasyVideoPlayer(this);
       // player.setCallback(this);
       // player.setSource(Uri.parse(TEST_URL));
        displayView(R.id.nav_all_chanels);
    }

   // lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {}

    // генерируем данные для адаптера
    void fillData() {
        for (int i = 1; i <= 20; i++) {
           // chanel = new Chanel(R.mipmap.ic_launcher, "Мега", "");
            products.add(new Chanel(R.mipmap.ic_launcher, "Мега"+i, ""));
        }
    }

    void loadText() {

        prefs = getSharedPreferences("MY_PREFS_JUSTTV", MODE_PRIVATE);
        editor = getSharedPreferences("MY_PREFS_JUSTTV", MODE_PRIVATE).edit();
        String restoredText = prefs.getString(SAVED_TEXT_GN, null);
        String restoredText2 = prefs.getString(SAVED_TEXT_GE, null);
        String restoredText4 = prefs.getString(SAVED_TEXT_GU, null);
        if (restoredText!= null && restoredText2!= null) {
            nameProfile.setText(restoredText);
            emailProfile.setText(restoredText2);
            Glide.with(this).load(restoredText4).skipMemoryCache(true).into(loginBtn);
            Log.d("LOADD1", restoredText);
            Log.d("LOAD2", restoredText2);
            Log.d("LOADD4", restoredText4);
        }

        try {
            String restoredText3 = prefs.getString(SAVED_TEXT_FK, null);
            response = new JSONObject(restoredText3);
            nameProfile.setText(response.get("name").toString());
            //Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show();
            emailProfile.setText(response.get("email").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Glide.with(this).load(profile_pic_url.getString("url")).skipMemoryCache(true).into(loginBtn);

        } catch (Exception e) {
            e.printStackTrace();
        }


        //Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
    }

    public  void  setUserProfile(String jsondata){
               try {
                response = new JSONObject(jsondata);
                nameProfile.setText(response.get("name").toString());
                   //Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show();
                emailProfile.setText(response.get("email").toString());
                  profile_pic_data = new JSONObject(response.get("picture").toString());
                           profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                   Glide.with(this).load(profile_pic_url.getString("url")).skipMemoryCache(true).into(loginBtn);

                    } catch (Exception e){
                e.printStackTrace();
        }
    }

    public  void  setUserProfileGoogle(){
        try {
            nameProfile.setText(gname);
            emailProfile.setText(gemail);
            //Toast.makeText(this, "setUserProfileGoogle", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(gpic).skipMemoryCache(true).into(loginBtn);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        //finish();

        WindowManager.LayoutParams attrs = this.getWindow().getAttributes();

        if (!fullScreenOnly && !portrait) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.finish();
        }

        if (player != null && player.onBackPressed()) {
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            //Snackbar.make(this,"Please click BACK again to exit",Snackbar.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(lvMain,R.string.double_click_snackbar, Snackbar.LENGTH_LONG);
            snackbar.show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);//this handler helps to reset the variable after 2 second.
        }
    }

    private void displayView(int viewId) {

        String title_tb = getString(R.string.app_name);
        switch (viewId) {
            case R.id.nav_all_chanels:
                title_tb = getString(R.string.title_nav_all_chanels);
                break;

            case R.id.nav_ua_chanels:
                title_tb = getString(R.string.title_nav_ua_chanels);
                break;

            case R.id.nav_ru_chanels:
                title_tb = getString(R.string.title_nav_ru_chanels);
                break;

            case R.id.nav_about_app:
                title_tb = getString(R.string.title_nav_about_app);
                break;

            default:
                break;
        }
        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title_tb);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displayView(item.getItemId());
        int id = item.getItemId();

        if (id == R.id.nav_all_chanels) {
            // Handle the camera action
          // chanelList.clear();
            chanelList.clear();
            prepareChanelData();
            lvMain.setAdapter(boxAdapter);

        } else if (id == R.id.nav_ua_chanels) {

            //Toast.makeText(this,"nav_gallery",Toast.LENGTH_LONG).show();
            chanelList.clear();
            prepareChanelDataUA();
            lvMain.setAdapter(boxAdapter);

        } else if (id == R.id.nav_ru_chanels) {

            chanelList.clear();
            prepareChanelDataRU();
            lvMain.setAdapter(boxAdapter);

        } else if (id == R.id.nav_about_app) {

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
                String restoredText4 = prefs.getString(SAVED_TEXT_GU, null);
                String restoredText3 = prefs.getString(SAVED_TEXT_FK, null);
                if (restoredText == null && restoredText2 == null && restoredText3 == null && restoredText4 == null) {

                    Intent i = new Intent(MainActivity.this, AutentificacionAvtivity.class);
                    startActivityForResult(i, 1);

                }else {

                    loginBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle( "JusttV" )
                                    .setMessage( "Вийти з акаунту?" )
                                    .setPositiveButton( "Так", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.d( "AlertDialog", "Positive" );
                                            LoginManager.getInstance().logOut();
                                            editor.remove(SAVED_TEXT_GN);
                                            editor.remove(SAVED_TEXT_GE);
                                            editor.remove(SAVED_TEXT_GU);
                                            editor.remove(SAVED_TEXT_FK);
                                            editor.commit();
                                            nameProfile.setText("Увійти");
                                            emailProfile.setText("JustTV");
                                            drawer.closeDrawer(GravityCompat.START);
                                            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                                            startActivity(intent);
                                            finish();
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
                }
                break;
            default:
                break;
        }
    }

    /*public void loadSharedPrefs(String ... prefs) {

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

    }*/


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

    private void prepareChanelData() {

        String[] title_chanels = getResources().getStringArray(R.array.title_chanels);
        String[] url_chanels = getResources().getStringArray(R.array.url_chanels);

        Chanel chanel = new Chanel(R.drawable.dvaxdva_chanel, title_chanels[0], url_chanels[0]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.cartoonnetwork_chanel, title_chanels[1], url_chanels[1]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.disney_chanel, title_chanels[2], url_chanels[2]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.enter_film_chanel, title_chanels[3], url_chanels[3]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.hct_chanel, title_chanels[4], url_chanels[4]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ictv_chanel, title_chanels[5], url_chanels[5]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.lifenews_chanel, title_chanels[6], url_chanels[6]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.rutv_chanel, title_chanels[7], url_chanels[7]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.boec_chanel, title_chanels[8], url_chanels[8]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.domashnii_chanel, title_chanels[9], url_chanels[9]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.zvezda_chanel, title_chanels[10], url_chanels[10]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.inter_chanel, title_chanels[11], url_chanels[11]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.inter_chanel, title_chanels[12], url_chanels[12]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.k1_chanel, title_chanels[13], url_chanels[13]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.k2_chanel, title_chanels[14], url_chanels[14]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.chanel24_chanel, title_chanels[15], url_chanels[15]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.karusel_chanel, title_chanels[16], url_chanels[16]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.matchtv_chanel, title_chanels[17], url_chanels[17]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.mega_chanel, title_chanels[18], url_chanels[18]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.moyaplaneta_chanel, title_chanels[19], url_chanels[19]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.telekanal_novy, title_chanels[20], url_chanels[20]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ntv_chanel, title_chanels[21], url_chanels[21]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ntn_chanel, title_chanels[22], url_chanels[22]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pervyi_chanel, title_chanels[23], url_chanels[23]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pyatnica_chanel, title_chanels[24], url_chanels[24]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pyatyi_chanel, title_chanels[25], url_chanels[25]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.rentv_chanel, title_chanels[26], url_chanels[26]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.rossiya1_chanel, title_chanels[27], url_chanels[27]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.rossiya24_chanel, title_chanels[28], url_chanels[28]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.kultura_chanel, title_chanels[29], url_chanels[29]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.planetartr_chanel, title_chanels[30], url_chanels[30]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.stb_chanel, title_chanels[31], url_chanels[31]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.sts_chanel, title_chanels[32], url_chanels[32]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ctclove_chanel, title_chanels[33], url_chanels[33]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tv3_chanel, title_chanels[34], url_chanels[34]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tvc_chanel, title_chanels[35], url_chanels[35]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tnt_chanel, title_chanels[36], url_chanels[36]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ukraine_chanel, title_chanels[37], url_chanels[37]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.che_chanel, title_chanels[38], url_chanels[38]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tnt4_chanel, title_chanels[39], url_chanels[39]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pyatyiua_chanel, title_chanels[40], url_chanels[40]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.sport1_chanel, title_chanels[41], url_chanels[41]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tonis_chanel, title_chanels[42], url_chanels[42]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pixel_chanel, title_chanels[43], url_chanels[43]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.muztv_chanel, title_chanels[44], url_chanels[44]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.paramount_comedy_chanel, title_chanels[45], url_chanels[45]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.mtv_chanel, title_chanels[46], url_chanels[46]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.dvaodinodin_chanel, title_chanels[47], url_chanels[47]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.comedy_chanel, title_chanels[48], url_chanels[48]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.domkino_chanel, title_chanels[49], url_chanels[49]);
        chanelList.add(chanel);


        //  mAdapter.notifyDataSetChanged();
    }

    private void prepareChanelDataUA() {

        String[] title_chanel_ua = getResources().getStringArray(title_chanels);
        String[] url_chanel_ua = getResources().getStringArray(url_chanels);

        Chanel chanel = new Chanel(R.drawable.enter_film_chanel, title_chanel_ua[3], url_chanel_ua[3]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ictv_chanel, title_chanel_ua[5], url_chanel_ua[5]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.inter_chanel, title_chanel_ua[11], url_chanel_ua[11]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.inter_chanel, title_chanel_ua[12], url_chanel_ua[12]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.k1_chanel, title_chanel_ua[13], url_chanel_ua[13]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.k2_chanel, title_chanel_ua[14], url_chanel_ua[14]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.chanel24_chanel, title_chanel_ua[15], url_chanel_ua[15]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.mega_chanel, title_chanel_ua[18], url_chanel_ua[18]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.telekanal_novy, title_chanel_ua[20], url_chanel_ua[20]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ntn_chanel, title_chanel_ua[22], url_chanel_ua[22]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.stb_chanel, title_chanel_ua[31], url_chanel_ua[31]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ukraine_chanel, title_chanel_ua[37], url_chanel_ua[37]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pyatyiua_chanel, title_chanel_ua[40], url_chanel_ua[40]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.sport1_chanel, title_chanel_ua[41], url_chanel_ua[41]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tonis_chanel, title_chanel_ua[42], url_chanel_ua[42]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pixel_chanel, title_chanel_ua[43], url_chanel_ua[43]);
        chanelList.add(chanel);
    }

    private void prepareChanelDataRU() {

        String[] title_chanels_ru = getResources().getStringArray(R.array.title_chanels);
        String[] url_chanels_ru = getResources().getStringArray(R.array.url_chanels);

        Chanel chanel = new Chanel(R.drawable.dvaxdva_chanel, title_chanels_ru[0], url_chanels_ru[0]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.cartoonnetwork_chanel, title_chanels_ru[1], url_chanels_ru[1]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.disney_chanel, title_chanels_ru[2], url_chanels_ru[2]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.hct_chanel, title_chanels_ru[4], url_chanels_ru[4]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.lifenews_chanel, title_chanels_ru[6], url_chanels_ru[6]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.rutv_chanel, title_chanels_ru[7], url_chanels_ru[7]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.boec_chanel, title_chanels_ru[8], url_chanels_ru[8]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.domashnii_chanel, title_chanels_ru[9], url_chanels_ru[9]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.zvezda_chanel, title_chanels_ru[10], url_chanels_ru[10]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.karusel_chanel, title_chanels_ru[16], url_chanels_ru[16]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.matchtv_chanel, title_chanels_ru[17], url_chanels_ru[17]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.moyaplaneta_chanel, title_chanels_ru[19], url_chanels_ru[19]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ntv_chanel, title_chanels_ru[21], url_chanels_ru[21]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pervyi_chanel, title_chanels_ru[23], url_chanels_ru[23]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pyatnica_chanel, title_chanels_ru[24], url_chanels_ru[24]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.pyatyi_chanel, title_chanels_ru[25], url_chanels_ru[25]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.rentv_chanel, title_chanels_ru[26], url_chanels_ru[26]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.rossiya1_chanel, title_chanels_ru[27], url_chanels_ru[27]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.rossiya24_chanel, title_chanels_ru[28], url_chanels_ru[28]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.kultura_chanel, title_chanels_ru[29], url_chanels_ru[29]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.planetartr_chanel, title_chanels_ru[30], url_chanels_ru[30]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.sts_chanel, title_chanels_ru[32], url_chanels_ru[32]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.ctclove_chanel, title_chanels_ru[33], url_chanels_ru[33]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tv3_chanel, title_chanels_ru[34], url_chanels_ru[34]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tvc_chanel, title_chanels_ru[35], url_chanels_ru[35]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tnt_chanel, title_chanels_ru[36], url_chanels_ru[36]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.che_chanel, title_chanels_ru[38], url_chanels_ru[38]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.tnt4_chanel, title_chanels_ru[39], url_chanels_ru[39]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.muztv_chanel, title_chanels_ru[44], url_chanels_ru[44]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.paramount_comedy_chanel, title_chanels_ru[45], url_chanels_ru[45]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.mtv_chanel, title_chanels_ru[46], url_chanels_ru[46]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.dvaodinodin_chanel, title_chanels_ru[47], url_chanels_ru[47]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.comedy_chanel, title_chanels_ru[48], url_chanels_ru[48]);
        chanelList.add(chanel);
        chanel = new Chanel(R.drawable.domkino_chanel, title_chanels_ru[49], url_chanels_ru[49]);
        chanelList.add(chanel);

        //  mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        assert player == null;
    }

   /*private LinearLayout.LayoutParams paramsNotFullscreen; //if you're using RelativeLatout

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
   @Override
   public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);


        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) //To fullscreen
        {
            //Remove notification bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            paramsNotFullscreen=(LinearLayout.LayoutParams)player.getLayoutParams();
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(paramsNotFullscreen);
            params.setMargins(0, 0, 0, 0);
            params.height= ViewGroup.LayoutParams.MATCH_PARENT;
            params.width=ViewGroup.LayoutParams.MATCH_PARENT;
            player.setLayoutParams(params);

        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            player.setLayoutParams(paramsNotFullscreen);
        }
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    public void regEx(String url){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        //System.out.println(response.substring(0,100));
                        Log.d("WWWResponse",response);

                        String buff = null;

                        int lengtresponse = response.length();
                        Log.d("WWWlength", String.valueOf(response.length()));

                        if(lengtresponse>9075 & lengtresponse<16000) {

                            Pattern pattern = Pattern.compile("http:[/]{2}[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+[:][0-9]{4}[/][\\w]+[-]{0,1}[/]{0,1}[\\w]+[/][\\w]+[.][\\w]+[/][\\w]+[.][\\w]+[?][\\w]+[=][\\w]+[=]{0,}");

                            Matcher matcher = pattern.matcher(response);

                            while (matcher.find()) {
                                //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                            }
                        }

                        else
                        if (lengtresponse == 19521) { //tonis

                            Pattern pattern = Pattern.compile("http:[/]{2}[0-9]+[.]+[0-9]+[.][0-9]+[0-9][.][0-9]+[/][\\w]+[/][\\w]+[:][\\w]+[.][\\w]+[/][\\w]+[.][\\w]+[?][\\w]");

                            Matcher matcher = pattern.matcher(response);

                            while (matcher.find()) {
                                //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                            }
                        }

                        else
                        if (lengtresponse == 19665) { //pixel

                            Pattern pattern = Pattern.compile("http:[/]{2}[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+[:][\\w]+[/][\\w]+[/][\\w]+[.][\\w]+");

                            Matcher matcher = pattern.matcher(response);

                            while (matcher.find()) {
                                //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                            }
                        }

                        else
                        if (lengtresponse == 20716) { //otv

                            Pattern pattern = Pattern.compile("rtmp:[/]{2}[0-9]+[.]+[0-9]+[.][0-9]+[0-9][.]+[0-9]+[/][\\w]+[\\w]+[/][\\w]+");

                            Matcher matcher = pattern.matcher(response);

                            while (matcher.find()) {
                                //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                            }
                        }

                        else
                        if (lengtresponse>=16000) {

                            Pattern pattern = Pattern.compile("http:[/]{2}[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+[:][0-9]{4}[/][\\w]+[-]{0,1}[/]{0,1}[\\w]+[/][\\w]+[.][\\w]+[/][\\w]+[.][\\w]+[?][\\w]+[=][\\w]+[=]{0,}");

                            Matcher matcher = pattern.matcher(response);

                            while (matcher.find()) {
                                //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                            }
                        }

                        if (lengtresponse>=16000 & lengtresponse<60000) {

                            Pattern pattern = Pattern.compile("http:[/]{2}[\\w]+[.][\\w]+[.][\\w]+[/][\\w]+[.][m][\\w]+");

                            Matcher matcher = pattern.matcher(response);

                            while (matcher.find()) {
                                //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                            }
                        }

                        else
                            if (lengtresponse == 8174) {

                                Pattern pattern = Pattern.compile("http:[/]{2}[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+[/][\\w]+[/][\\w]+[.][\\w]+");

                                Matcher matcher = pattern.matcher(response);

                                while (matcher.find()) {
                                    //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                    buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                                }
                            }

                            else
                            if (lengtresponse == 8348) {

                                Pattern pattern = Pattern.compile("http:[/]{2}[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+[/][\\w]+[/][\\w]+[.][\\w]+");

                                Matcher matcher = pattern.matcher(response);

                                while (matcher.find()) {
                                    //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                    buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                                }
                            }

                            else
                            if (lengtresponse == 8226) { //24 chanel

                                Pattern pattern = Pattern.compile("http:[/]{2}[\\w]+[.][\\w]+[.][\\w]+[/][\\w]+[/][\\w]+[:][\\w]+[.][\\w]+[.][\\w]+[/][\\w]+[.][\\w]+");

                                Matcher matcher = pattern.matcher(response);

                                while (matcher.find()) {
                                    //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                    buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                                }
                            }

                            else
                            if (lengtresponse == 9073) { //inter

                                Pattern pattern = Pattern.compile("http:[/]{2}[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+[/][\\w]+[/][\\w]+[.][\\w]+");

                                Matcher matcher = pattern.matcher(response);

                                while (matcher.find()) {
                                    //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                    buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                                }
                            }

                        else {

                            Pattern pattern = Pattern.compile("http:[/]{2}[0-9]+[.][0-9]+[.][0-9]+[.][0-9]+[:][0-9]{4}[/][\\w]+[-]{0,1}[/]{0,1}[\\w]+[/][\\w]+[.][\\w]{4}[?][\\w]{11}[=][\\w]{114}[=]{0,}");
                            Matcher matcher = pattern.matcher(response);

                            while (matcher.find()) {
                                //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                                buff = response.substring(matcher.start() + 0, matcher.end() - 0);
                            }
                        }
                        //playVideo(buff);

                         //player.setSource(Uri.parse(buff));
                         Log.d("BUFplayer",buff);
                         player.play(buff);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        });

// Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("CLICK!","CLICK");
        //Toast.makeText(getApplicationContext(), "LOL"+i, Toast.LENGTH_SHORT).show();

        String positionurl = chanelList.get(i).getUrl();
        Log.d("ididid", positionurl);
        //mProgressDialog.show();
        regEx(positionurl);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.main, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    boxAdapter.filter("");
                    lvMain.clearTextFilter();
                } else {
                    boxAdapter.filter(newText);
                }
                return true;
            }
        });

        return true;
    }
}