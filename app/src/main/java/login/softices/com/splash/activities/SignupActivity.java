package login.softices.com.splash.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import login.softices.com.splash.R;
import login.softices.com.splash.database.DatabaseHelper;
import login.softices.com.splash.dialogsAndValidations.InputValidation;

public class SignupActivity extends AppCompatActivity {

    private TextView txtLogin;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConformPassword;
    private EditText edtFirstName, edtLastName;
    private RadioGroup radioButton;
    private DatabaseHelper databasehelper;
    private Bitmap photoBitmap;
    private String TAG = "SignupActivity";
    private final int PICK_IMAGE_CAMERA = 0, PICK_IMAGE_GALLERY = 1;
    private Uri selectedImage;
    private ImageView ivPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_signup );
        edtFirstName = findViewById( R.id.edt_firstname );
        radioButton = findViewById( R.id.rdbt_gender );
        edtLastName = findViewById( R.id.edt_lastname );
        edtEmail = findViewById( R.id.edt_email );
        edtPassword = findViewById( R.id.edt_password );
        ivPhoto = findViewById( R.id.iv_Photo );
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
                photoBitmap = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
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
                } else if (databasehelper.checkUser( email )) {
                    Toast.makeText( SignupActivity.this, "Email already exist", Toast.LENGTH_SHORT ).show();
                } else {
                    User user = new User();
                    user.setEmail( email );
                    user.setPassword( password );
                    user.setFirst_name( firstname );
                    user.setLast_name( lastname );
                    user.setGender( " " );
                    user.setPhoto( photoBitmap );
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

    public void onClickSelectPhoto(View view) {
        if (Build.VERSION.SDK_INT < 23) {
            selectImage();
        } else {
            if (ContextCompat.checkSelfPermission( this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission( this, Manifest.permission
                    .CAMERA ) == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                ActivityCompat.requestPermissions( SignupActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (grantResults.length > 0) {
            switch (requestCode) {
                case 1:
                    goWithCameraPermission( grantResults );
                    break;
                default:
                    break;
            }
        }
    }

    private void goWithCameraPermission(int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions( SignupActivity.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
        }
    }

    /**
     * Opens PickImageDialog to choose,
     * Camera or Gallery
     */
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( SignupActivity.this );
        builder.setTitle( "Select Option" );
        builder.setItems( options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals( "Take Photo" )) {
                    dialog.dismiss();
                    Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                    selectedImage = Uri.fromFile( new File( Environment.getExternalStorageDirectory(),
                            "image_" + String.valueOf( System.currentTimeMillis() ) + ".jpg" ) );
                    intent.putExtra( android.provider.MediaStore.EXTRA_OUTPUT, selectedImage );
                    startActivityForResult( intent, PICK_IMAGE_CAMERA );
                } else if (options[item].equals( "Choose From Gallery" )) {
                    dialog.dismiss();
                    Intent pickPhoto = new Intent( Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                    pickPhoto.setAction( Intent.ACTION_GET_CONTENT );
                    startActivityForResult( Intent.createChooser( pickPhoto, "Compelete action using" ),
                            PICK_IMAGE_GALLERY );
                } else if (options[item].equals( "Cancel" )) {
                    dialog.dismiss();
                }
            }
        } );
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PICK_IMAGE_CAMERA:
                try {
                    if (selectedImage != null) {
                        Bitmap photoBitmap = BitmapFactory.decodeFile(selectedImage.getPath());
                        setImageData(photoBitmap);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "PICK_FROM_CAMERA" + e);
                }
                break;

            case PICK_IMAGE_GALLERY:
                try {
                    if (resultCode == RESULT_OK) {
                        Uri uri = imageReturnedIntent.getData();
                        if (uri != null) {
                            Bitmap bitmap = null;
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            setImageData(bitmap);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "PICK_FROM_GALLERY" + e);
                }
                break;
        }
    }

    private void setImageData(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress( Bitmap.CompressFormat.JPEG, 100, out );
                Bitmap decoded = BitmapFactory.decodeStream( new ByteArrayInputStream( out.toByteArray() ) );
                ivPhoto.setImageBitmap(bitmap);
            } else {
                InputValidation.t( this, "Unable to select image" );
            }
        } catch (Exception e) {
            Log.e( TAG, "setImageData" + e );
        }
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







