package login.softices.com.splash.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import login.softices.com.splash.R;

public class BatteryLevelActivity extends AppCompatActivity {
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent i) {
            int level = i.getIntExtra( "level", 0 );
            ProgressBar pb = (ProgressBar) BatteryLevelActivity.this.findViewById( R.id.progressbar );
            pb.setProgress( level );
            TextView tv = (TextView) findViewById( R.id.textfield );
            tv.setText( "Battery Level: " + Integer.toString( level ) + "%" );

        }


    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_battery_level );
        registerReceiver( mBatInfoReceiver, new IntentFilter(
        Intent.ACTION_BATTERY_CHANGED ) );

        }
}
