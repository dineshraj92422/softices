package login.softices.com.splash.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import login.softices.com.splash.R;
import login.softices.com.splash.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private final AppCompatActivity activity = LoginActivity.this;

    private EditText edtEmail;
    private EditText edtPassword;
    private TextView txtForgot, txtCreate;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        edtEmail = findViewById( R.id.edt_email );
        edtPassword = findViewById( R.id.edt_password );
        databaseHelper = new DatabaseHelper( this );
        edtEmail.setText( "dineshraj9724@gmail.com" );
        edtPassword.setText( "dinesh1008" );
        findViewById( R.id.btn_login ).setOnClickListener( new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (!isValidEmail( email )) {
                    Toast.makeText( LoginActivity.this, "Please enter Valid email", Toast.LENGTH_SHORT ).show();
                } else if (!isValidPassword( password )) {
                    Toast.makeText( LoginActivity.this, "Please enter Valid password", Toast.LENGTH_SHORT ).show();
                } else {
                    if (databaseHelper.checkUser( email, password )) {
                        isUserLoggedIn( email, true );
                        Intent intent = new Intent( v.getContext(), DashboardActivity.class );
                        startActivity( intent );
                        Toast.makeText( LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT ).show();
                    } else {
                        Toast.makeText( LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT ).show();
                    }

                }

            }

        } );

        txtCreate = findViewById( R.id.txt_create );
        txtCreate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this, SignupActivity.class );
                startActivity( intent );
                finish();
            }
        } );

        txtForgot = findViewById( R.id.txt_forgot );
        txtForgot.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this, ForgetActivity.class );
                startActivity( intent );
                finish();
            }
        } );
    }

    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile( EMAIL_PATTERN );
        Matcher matcher = pattern.matcher( email );
        return matcher.matches();
    }

    private void isUserLoggedIn(String email, boolean b) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( "email", email );
        editor.putBoolean( "b_login_value", b );
        editor.apply();
    }

    private void emptyInputEditText() {
        edtEmail.setText( null );
        edtPassword.setText( null );
    }
}