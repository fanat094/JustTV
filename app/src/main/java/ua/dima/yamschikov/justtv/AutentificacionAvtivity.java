package ua.dima.yamschikov.justtv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

public class AutentificacionAvtivity extends AppCompatActivity {
    CallbackManager callbackManager;
    SignInButton mGoogleSignInButton;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    final String SAVED_TEXT_GN = "saved_text_googleName";
    final String SAVED_TEXT_GE = "saved_text_googleEmail";
    final String SAVED_TEXT_GU = "saved_text_googleUrl";
    final String SAVED_TEXT_FK = "saved_text_facebook";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.autentificacion_main);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile email");
        getLoginDetailsFacebook(loginButton);

        mGoogleSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        Button btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    protected void getLoginDetailsFacebook(LoginButton login_button) {
        // Callback registration<
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                getUserInfo(login_result);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    protected void getUserInfo(final LoginResult login_result) {
        GraphRequest data_request = GraphRequest.newMeRequest(login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json_object, GraphResponse response) {
                        Intent intent = new Intent(AutentificacionAvtivity.this, MainActivity.class);
                        intent.putExtra( "jsondata",json_object.toString());
                        intent.putExtra("CHECK", 0);
                        Log.d("SRAKA", json_object.toString());

                        editor = getSharedPreferences("MY_PREFS_JUSTTV", MODE_PRIVATE).edit();
                        editor.putString(SAVED_TEXT_FK, json_object.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString( "fields", "id, name, email, picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    private void signInWithGoogle() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            Log.e("TAG", "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

            Intent intent = new Intent(AutentificacionAvtivity.this, MainActivity.class);
            intent.putExtra( "gname",personName.toString());
            intent.putExtra( "gemail",email.toString());
            intent.putExtra( "gpic",personPhotoUrl.toString());
            intent.putExtra("CHECK", 1);

            editor = getSharedPreferences("MY_PREFS_JUSTTV", MODE_PRIVATE).edit();
            editor.putString(SAVED_TEXT_GN, personName.toString());
            editor.putString(SAVED_TEXT_GE, email.toString());
            //editor.putString(SAVED_TEXT_GU,personPhotoUrl.toString());
            Toast.makeText(this, "Text saved_GOOGLE", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, intent);
            finish();

        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.<br />
        // getMenuInflater().inflate(R.menu.menu_main, menu);<br />
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement<br />
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.<br />
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.<br />
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.commit();//вилітає якщо нічого не робити на актівіті авторизації а просто нажати кнопку "Назад"
    }
}