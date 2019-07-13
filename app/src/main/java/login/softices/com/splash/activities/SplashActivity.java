package login.softices.com.splash.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import login.softices.com.splash.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splashscreen );

        Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                if (isUserLoggedIn()) {
                    Intent intent = new Intent( SplashActivity.this, DashboardActivity.class );
                    startActivity( intent );
                } else {
                    Intent intent = new Intent( SplashActivity.this, LoginActivity.class );
                    startActivity( intent );
                    finish();
                }
            }
        }, 2500 );
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        return sharedPreferences.getBoolean( "b_login_value", false );
    }
}
