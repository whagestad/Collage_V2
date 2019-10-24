package com.wtiii.collage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MAIN_ACTIVITY";
    private ImageButton mImageButton1;
    private String mCurrentImagePath;

    private final static String BUNDLE_KEY_MOST_RECENT_FILE_PATH = "bundle key most recent file path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageButton1 = findViewById(R.id.imageButton1);
        mImageButton1.setOnClickListener(this);

        if (savedInstanceState != null) {
            mCurrentImagePath = savedInstanceState.getString(BUNDLE_KEY_MOST_RECENT_FILE_PATH);
        }

        if (mCurrentImagePath == null) {
            mCurrentImagePath = "";
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle) {
        super.onSaveInstanceState(outBundle);
        outBundle.putString(BUNDLE_KEY_MOST_RECENT_FILE_PATH, mCurrentImagePath);
    }

    @Override
    public void onClick(View view) {
        // Completed - handle ImageButton click
        int requestCodeButtonIndex = 0; // todo revise this for many ImageButtons
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) { // If there's a camera
            try {
                File imageFile = createImageFile();
                if (imageFile != null) {
                    Uri imageURI = FileProvider.getUriForFile(this, "com.wtiii.collage.fileprovider", imageFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                    startActivityForResult(takePictureIntent, requestCodeButtonIndex);
                } else {
                    Log.e(TAG, "Image file is null");
                }
            } catch (IOException e) {
                Log.e(TAG, "Error creating image file " + e);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create unique filename with timestamp
        String imageFilename = "COLLAGE_" + new Date().getTime();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFilename,
                ".jpg",
                storageDir
        );

        // Save the file path globally, when the take picture Intent returns,
        // this location will be where the image is saved
        mCurrentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult for request code " + requestCode +
                    " and current path " + mCurrentImagePath);
        }

        else if (resultCode == RESULT_CANCELED) {
            mCurrentImagePath = "";
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // The view hasn't loaded in onActivityResult if the device
        // is rotated when the picture is taken.
        // onWindowFocusedChanged is called after onActivityResult
        // and after the view has loaded, so override this
        // method to display the image in the ImageView.

        Log.d(TAG, "focus changed " + hasFocus);
        if (hasFocus) {
            loadImage();
        }
    }

    private void loadImage() {
        // Completed - complete this
        if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
            Picasso.get()
                    .load(new File(mCurrentImagePath))
                    .error(android.R.drawable.stat_notify_error) // built-in error icon
                    .fit()
                    .centerCrop()
                    .into(mImageButton1, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Success");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d(TAG, "Error", e);
                        }
                        //@Override
                    });
        }
    }
}
