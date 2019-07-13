package login.softices.com.splash.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import login.softices.com.splash.R;
import login.softices.com.splash.database.DatabaseHelper;
import login.softices.com.splash.dialogsAndValidations.InputValidation;

public class ProfileActivity extends AppCompatActivity {

    EditText edtEmail;
    EditText edtFirstName, edtLastName;
    private DatabaseHelper databaseHelper;
    private ImageView ivphoto;
    private Uri selectedImage;
    private Bitmap photoBitmap;
    private User user;
    private String TAG = "ProfileActivity";
    private final int PICK_IMAGE_CAMERA = 0, PICK_IMAGE_GALLERY = 1, REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        edtFirstName = findViewById(R.id.firstname);
        edtLastName = findViewById(R.id.lastname);
        edtEmail = findViewById(R.id.email);
        databaseHelper = new DatabaseHelper(this);
        ivphoto = findViewById(R.id.iv_photo);

        setDefaultData();

        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String firstname = edtFirstName.getText().toString();
                String lastname = edtLastName.getText().toString();
                photoBitmap = ((BitmapDrawable) ivphoto.getDrawable()).getBitmap();
                if (!isValidFirstName(firstname)) {
                    Toast.makeText(ProfileActivity.this, "Please Enter correct name", Toast.LENGTH_SHORT).show();
                } else if (!isValidLastName(lastname)) {
                    Toast.makeText(ProfileActivity.this, "Please Enter correct surname", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(ProfileActivity.this, "Please enter Valid email", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User();
                    user.setEmail(email);
                    user.setFirst_name(firstname);
                    user.setLast_name(lastname);
                    user.setGender("");
                    user.setPhoto(photoBitmap);

                    if (databaseHelper.updateUser(user)) {
                        Toast.makeText(ProfileActivity.this, "Data update", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
                        startActivityForResult(intent, 0);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Data not upadte", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        ivphoto = findViewById(R.id.iv_photo);
        ivphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenImage();
            }
        });

    }

    private void onOpenImage() {
        if (Build.VERSION.SDK_INT < 23) {
            selectImage();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_CODE:
                    goWithCameraPermission(grantResults);
                    break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * \
     * Request for Camera permission.
     *
     * @param grantResults
     */
    private void goWithCameraPermission(int[] grantResults) {
        if (grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else if (grantResults[1] == PackageManager.PERMISSION_DENIED ||
                grantResults[0] == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest
                    .permission.WRITE_EXTERNAL_STORAGE, Manifest
                    .permission.CAMERA}, REQUEST_CODE);
        }
    }

    /**
     * Opens PickImageDialog to choose,
     * Camera or Gallery
     */
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    dialog.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    selectedImage = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "image_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, selectedImage);
                    startActivityForResult(intent, PICK_IMAGE_CAMERA);
                } else if (options[item].equals("Choose From Gallery")) {
                    dialog.dismiss();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(pickPhoto, "Compelete action using"),
                            PICK_IMAGE_GALLERY);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
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

    /**
     * saving image as bitmap in imageview.
     *
     * @param bitmap
     */
    private void setImageData(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                ivphoto.setImageBitmap(bitmap);           } else {
                InputValidation.t(this, "Unable to select image");
            }
        } catch (Exception e) {
            Log.e(TAG, "setImageData" + e);
        }
    }

    private void setDefaultData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPreferences.getString("email", "");
        List<User> user = databaseHelper.getcurrentUser(email);
        ivphoto.setImageBitmap(user.get(0).getPhoto());
        edtFirstName.setText(user.get(0).getFirst_name());
        edtLastName.setText(user.get(0).getLast_name());
        edtEmail.setText(user.get(0).getEmail());
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

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
