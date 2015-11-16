package com.example.sam.moodalert;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sam.moodalert.Model.Updater;
import com.parse.ParseUser;


public class SplashActivity extends Activity {
    protected ProgressBar mProgressView;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Get progress view
        mProgressView = (ProgressBar)findViewById(R.id.login_progress);
        //Set up update broadcast receiver
        registerReceiver(SplashObserver, new IntentFilter("SPLASH_UPDATE"));

        showProgress(true);

        activity = this;
    }

    @Override
    public void onResume(){
        super.onResume();

        //Auto login
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Updater.getInstance(this);
        } else {
            // register user
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.getIndeterminateDrawable().setColorFilter(0xFFE0E0E0,
                    android.graphics.PorterDuff.Mode.MULTIPLY);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    //Notified by loader to move to login because entries have been loaded
    private final BroadcastReceiver SplashObserver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Check if online or offline mode
            boolean offlineMode = intent.getBooleanExtra("OFFLINE_MODE", false);

            //No parse access
            if(offlineMode){
                Toast.makeText(activity, "Error retrieving data", Toast.LENGTH_SHORT).show();

                //Wait 1 second
                final Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        //remove spinner
                        showProgress(false);

                        // go to app
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                };
                handler.postDelayed(r, 1500); //wait 1.5 seconds before continuing

            //Have access to Parse
            } else {
                //Wait 1 second
                final Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        //remove spinner
                        showProgress(false);

                        // go to app
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                };
                handler.postDelayed(r, 1500); //wait 1.5 seconds before continuing
            }
        }
    };

    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(SplashObserver);
    }
}
