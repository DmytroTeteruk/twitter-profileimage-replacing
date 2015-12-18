package com.ermnesoft.uploadphototwitter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import retrofit.client.Response;

/**
 * Created by sasha on 12/18/15.
 */
public class FragmentLogin extends Fragment implements View.OnClickListener {

    /**
     * TAG for Log
     */
    private static final String TAG =  FragmentLogin.class.getSimpleName();

    /**
     * context for fragment
     */
    private LoginActivity activityLogin;

    /**
     * twitter login button
     */
    private TwitterLoginButton loginButton;

    /**
     * buttons for change twitter photo
     */
    private Button buttonChangeTwitter;

    /**
     * image view for avatar
     */
    private ImageView imageViewAvatar;

    /**
     * session object for twitter
     */
    private TwitterSession session;

    /**
     * user id for twitter
     */
    long userId;

    /**
     * Override method need for add context to fragment
     * @param activity context activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        activityLogin = (LoginActivity) activity;
    }

    /**
     * Override method need for init objects when started fragment
     * @param inflater object for get information from xml
     * @param container Optional view to be the parent of the generated hierarchy
     * @param savedInstanceState bundle object
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //find buttons and add listeners
        imageViewAvatar = (ImageView) view.findViewById(R.id.image_view_avatar);
        imageViewAvatar.setOnClickListener(this);

        buttonChangeTwitter = (Button) view.findViewById(R.id.button_login_twitter);
        buttonChangeTwitter.setOnClickListener(this);

        loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                userId = session.getUserId();

                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(activityLogin.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

        return view;
    }

    /**
     * change twitter photo
     */
    public void changeTwitterPhoto(){
        if(session != null){
            try {
                MyTwitterApiClient apiClients = new MyTwitterApiClient(session);

                    apiClients.getCustomService().sendPhoto(userId, activityLogin.createJsonWithImage(activityLogin.originalFileGallery), new Callback<Response>() {
                        @Override
                        public void success(Result<Response> result) {
                            if (result.data.getStatus() == 200) {
                                activityLogin.tempUri = null;
                                Toast.makeText(activityLogin, R.string.changed_twitter_photo, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void failure(TwitterException e) {
                            activityLogin.tempUri = null;
                            Log.v(TAG, "TwitterException response -->" + e);
                        }
                    });


            } catch (NullPointerException e) {
                Toast.makeText(activityLogin, R.string.failed_chang_photo, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activityLogin, R.string.failed_chang_photo, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            // button login twitter
            case R.id.button_login_twitter:
                changeTwitterPhoto();
                break;
            // image view load dialog avatar
            case R.id.image_view_avatar:
                activityLogin.importPhotoAlbum();
                break;
        }
    }
}
