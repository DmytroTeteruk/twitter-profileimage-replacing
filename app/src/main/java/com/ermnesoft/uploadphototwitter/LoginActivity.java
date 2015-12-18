package com.ermnesoft.uploadphototwitter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends FragmentActivity {

    /**
     * manager for fragments
     */
    private FragmentTransaction fragmentTransaction;

    /**
     * custom fragment for login
     */
    private FragmentLogin fragmentLogin;

    /**
     * image path for original file for camera and album
     */
    public String imageFilePath = "";

    /**
     * uri path to image album
     */
    public Uri tempUri = null;

    /**
     * object for login twitter
     */
    private TwitterLoginButton loginButton;

    /**
     * file with image from gallery
     */
    public File originalFileGallery = null;

    /**
     * if true image added
     */
    public boolean isAddedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_login);

        // create fragment login
        fragmentLogin = new FragmentLogin();

        // create object for transaction fragments
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // add and commit fragment from xml
        fragmentTransaction.add(R.id.login, fragmentLogin);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // find id twitter button object
        loginButton = (TwitterLoginButton)fragmentLogin.getView().findViewById(R.id.twitter_login_button);
        //callback result twitter
        loginButton.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // gallery request code
            case Constants.SELECT_PICTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageFilePath = cursor.getString(columnIndex);
                        originalFileGallery = new File(imageFilePath);
                        Uri imageFileUri = Uri.fromFile(originalFileGallery);
                        tempUri = null;
                        tempUri = imageFileUri;
                        isAddedImage = true;
                        ((ImageView)fragmentLogin.getView().findViewById(R.id.image_view_avatar)).setImageURI(imageFileUri);
                    }
                    cursor.close();
                }
                break;
        }
    }

    /**
     * start activity with photo album
     */
    public void importPhotoAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.SELECT_PICTURE_REQUEST_CODE);
    }

    /**
     * create json object with image and base 64
     * @param bitmapFile file with bitmap
     * @return string base 64
     */
    public String createJsonWithImage(File bitmapFile){

        int size = (int) bitmapFile.length();
        byte[] bytes = new byte[size];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(bitmapFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap ba = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Bitmap bitmap = Bitmap.createScaledBitmap(ba, Constants.SCALE_SIZE_IMAGE, Constants.SCALE_SIZE_IMAGE, false);

        Matrix matrix = new Matrix();
        matrix.postRotate(Utilities.getImageOrientation(imageFilePath));

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imagePostJSON = Base64Custom.encode(b);
        return imagePostJSON;
    }
}
