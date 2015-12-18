package com.ermnesoft.uploadphototwitter;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by sasha on 12/11/15.
 *
 * Need for extend the functionality twitter api client
 */
public class MyTwitterApiClient extends TwitterApiClient{

    /**
     * base constructor
     * @param session twitter session
     */
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    /**
     * get custom service instance
     * @return custom service object
     */
    public CustomService getCustomService(){
        return getService(CustomService.class);
    }
}

/**
 * interface for post request with parameters and callback
 * <p>
 *     used retrofit api
 *     @see <a href="http://square.github.io/retrofit/">http://retrofit</a>
 * </p>
 */
interface CustomService {
    @POST("/1.1/account/update_profile_image.json")
    void sendPhoto(@Query("user_id") long id, @Query("image") String image, Callback<Response> cb);
}