package login.softices.com.splash.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import login.softices.com.splash.R;
import login.softices.com.splash.database.DatabaseHelper;

public class SignupActivity extends AppCompatActivity {

    private TextView txtLogin;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConformPassword;
    private EditText edtFirstName, edtLastName;
    private RadioGroup radioButton;
    private DatabaseHelper databasehelper;
    private ImageView ivphoto;
    private Bitmap photoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_signup );
        edtFirstName = findViewById( R.id.edt_firstname );
        radioButton = findViewById( R.id.rdbt_gender );
        edtLastName = findViewById( R.id.edt_lastname );
        edtEmail = findViewById( R.id.edt_email );
        edtPassword = findViewById( R.id.edt_password );
        ivphoto=findViewById(R.id.iv_Photo);
        edtConformPassword = findViewById( R.id.edt_conformpassword );
        databasehelper = new DatabaseHelper( this );
       edtFirstName.setText( "Dineshsingh" );
        edtLastName.setText( "Rajpurohit" );
        edtEmail.setText( "dineshraj9724@gmail.com" );
        edtPassword.setText( "dinesh1008" );
        edtConformPassword.setText( "dinesh1008" );
        findViewById( R.id.btn_signup ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String firstname = edtFirstName.getText().toString();
                String lastname = edtLastName.getText().toString();
                String password = edtPassword.getText().toString();
                String conformpass = edtConformPassword.getText().toString();


                if (!isValidFirstName( firstname )) {
                    Toast.makeText( SignupActivity.this, "Please Enter correct name", Toast.LENGTH_SHORT ).show();
                } else if (!isValidLastName( lastname )) {
                    Toast.makeText( SignupActivity.this, "Please Enter correct surname", Toast.LENGTH_SHORT ).show();
                } else if (!isValidEmail( email )) {
                    Toast.makeText( SignupActivity.this, "Please enter Valid email", Toast.LENGTH_SHORT ).show();
                } else if (!isValidPassword( password )) {
                    Toast.makeText( SignupActivity.this, "Please enter Valid password", Toast.LENGTH_SHORT ).show();
                } else if (!isValidConformpassword( password, conformpass )) {
                    Toast.makeText( SignupActivity.this, "Please Enter same Password", Toast.LENGTH_SHORT ).show();
                } else if (databasehelper.checkUser( email)) {
                    Toast.makeText( SignupActivity.this, "Email already exist", Toast.LENGTH_SHORT ).show();
                } else {
                    User user = new User();
                    user.setEmail( email );
                    user.setPassword( password );
                    user.setFirst_name( firstname );
                    user.setLast_name( lastname );
                    user.setGender( " " );
                    user.setPhoto(photoBitmap);
                    boolean isUserCreated = databasehelper.addUser( user );
                    if (isUserCreated) {
                        Intent intent = new Intent( SignupActivity.this, LoginActivity.class );
                        startActivity( intent );
                    } else {
                        Toast.makeText( SignupActivity.this, "Something went wrong", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        } );

        txtLogin = findViewById( R.id.txt_login );
        txtLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SignupActivity.this, LoginActivity.class );
                startActivity( intent );
                finish();
            }
        } );

    }

    private boolean isValidLastName(String lname) {
        if (lname != null && lname.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidFirstName(String fname) {
        if (fname != null && fname.length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isValidConformpassword(String password, String conformpass) {
        if (conformpass != null && password.equals( conformpass )) {
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

    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }
}