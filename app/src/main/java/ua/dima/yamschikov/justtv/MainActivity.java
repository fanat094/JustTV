package ua.dima.yamschikov.justtv;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, EasyVideoCallback,
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

        Log.d("QQ","QQ");

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
        loadSharedPrefs("MY_PREFS_JUSTTV");
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


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
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
            Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show();
            emailProfile.setText(response.get("email").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Glide.with(this).load(profile_pic_url.getString("url")).skipMemoryCache(true).into(loginBtn);

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
                   Glide.with(this).load(profile_pic_url.getString("url")).skipMemoryCache(true).into(loginBtn);

                    } catch (Exception e){
                e.printStackTrace();
        }
    }

    public  void  setUserProfileGoogle(){
        try {
            nameProfile.setText(gname);
            emailProfile.setText(gemail);
            Toast.makeText(this, "setUserProfileGoogle", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(gpic).skipMemoryCache(true).into(loginBtn);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        if (player != null && player.onBackPressed()) {
            return;
        }
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
          // chanelList.clear();
            chanelList.clear();
            prepareChanelData();
            lvMain.setAdapter(boxAdapter);
        } else if (id == R.id.nav_gallery) {

            Toast.makeText(this,"nav_gallery",Toast.LENGTH_LONG).show();
            chanelList.clear();
            prepareChanelData2();
            lvMain.setAdapter(boxAdapter);

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

    private void prepareChanelData() {

        Chanel chanel = new Chanel(R.mipmap.ic_launcher, "1+1", "http://vsitv.org/uastb.html");
        chanelList.add(chanel);

        chanel = new Chanel(R.mipmap.ic_launcher, "Новий канал", newchanel);
        chanelList.add(chanel);

        chanel = new Chanel(R.mipmap.ic_launcher, "СТБ", stb);
        chanelList.add(chanel);

        chanel = new Chanel(R.mipmap.ic_launcher, "Мега", "http://vsitv.org/mega.html");
        chanelList.add(chanel);
        chanel = new Chanel(R.mipmap.ic_launcher, "Мега", "");
        chanelList.add(chanel);
        chanel = new Chanel(R.mipmap.ic_launcher, "Мега", "");
        chanelList.add(chanel);
        chanel = new Chanel(R.mipmap.ic_launcher, "Мега", "");
        chanelList.add(chanel);

      //  mAdapter.notifyDataSetChanged();

    }
    private void prepareChanelData2() {
        Chanel chanel = new Chanel(R.mipmap.ic_launcher, "ТНТ", tnt);
        chanelList.add(chanel);

        //mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        player.pause();
    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

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

        //String url = "http://vsitv.org/uastb.html";

// Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response.substring(0,100));
                        Log.d("WWW",response);


                        String buff = null;

                        Pattern pattern = Pattern.compile("http:[/]{2}[0-9]{3}[.][0-9]{3}[.][0-9]{2}[.][0-9]{3}[:][0-9]{4}[/][\\w]+[/][\\w]{5}[.][\\w]{4}[?][\\w]{11}[=][\\w]{114}[=]{2}");

                        Matcher matcher = pattern.matcher(response);

                        while (matcher.find()){
                            //Log.i("StreamLink",response.substring(matcher.start()+9, matcher.end()-1));
                            buff = response.substring(matcher.start()+0, matcher.end()-1);
                        }

                        //playVideo(buff);

                        //player.setSource(Uri.parse(buff));
                        Log.d("BUF",buff);
                        player.play(buff);
                        mProgressDialog.dismiss();

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
        Toast.makeText(getApplicationContext(), "LOL"+i, Toast.LENGTH_SHORT).show();

        String positionurl = chanelList.get(i).getUrl();
        Log.d("ididid", positionurl);
        //mProgressDialog.show();
        regEx(positionurl);
    }

    /*@Override
    public void onVideoProgressUpdate(int position, int duration) {
        player.setProgressCallback(this);
        player.setCallback(this);
    }*/
}