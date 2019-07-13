package login.softices.com.splash.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class Utility {
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getPhoto(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Uri getPhotosUri(final Context context, String ImageFilePath) {
        Bitmap photoBitmap;
        int rotationAngle = 0;
        if (ImageFilePath != null && ImageFilePath.length() > 0) {

            try {
                int mobile_width = 480;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(ImageFilePath, options);
                int outWidth = options.outWidth;
                int ratio = (int) ((((float) outWidth) / mobile_width) + 0.5f);

                if (ratio == 0) {
                    ratio = 1;
                }
                ExifInterface exif = new ExifInterface(ImageFilePath);

                String orientString = exif
                        .getAttribute(ExifInterface.TAG_ORIENTATION);
                int orientation = orientString != null ? Integer
                        .parseInt(orientString)
                        : ExifInterface.ORIENTATION_NORMAL;
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotationAngle = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotationAngle = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotationAngle = 270;
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        rotationAngle = 0;
                        break;
                    default:
                        // do with default
                        break;
                }

                options.inJustDecodeBounds = false;
                options.inSampleSize = ratio;

                photoBitmap = BitmapFactory.decodeFile(ImageFilePath, options);
                if (photoBitmap != null) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotationAngle,
                            (float) photoBitmap.getWidth() / 2,
                            (float) photoBitmap.getHeight() / 2);
                    photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0,
                            photoBitmap.getWidth(),
                            photoBitmap.getHeight(), matrix, true);

                    String path = MediaStore.Images.Media.insertImage(
                            context.getContentResolver(), photoBitmap,
                            Calendar.getInstance().getTimeInMillis()
                                    + ".jpg", null);

                    return Uri.parse(path);
                }
            } catch (OutOfMemoryError e) {
            } catch (IOException e) {
            }
        } else {
            Toast.makeText(
                    context, "Error In Image Capture", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public static byte[] getBytes(InputStream iStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = iStream.read(buffer)) != -1) {
            outStream.write(buffer,0,len);
        }
        return outStream.toByteArray();
    }
}
