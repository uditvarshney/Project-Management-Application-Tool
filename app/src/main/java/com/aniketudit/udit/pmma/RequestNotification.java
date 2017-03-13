package com.aniketudit.udit.pmma;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Dell PC on 01-02-2017.
 */

public class RequestNotification {
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public OkHttpClient mClient;
    public Context context;
    public String SERVER_KEY=null;
    public String receiverKey;
    public JSONArray regArray= null;
    public RequestNotification(Context context, String serverKEy, String receiverToken){
        this.context=context;
        this.receiverKey=receiverToken;
        this.SERVER_KEY=serverKEy;
        mClient=new OkHttpClient();
        regArray = new JSONArray();
        regArray.put(receiverKey);
    }
    public void sendMessage(final String title, final String body, final String icon, final String userId, final String userName, final String email) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    //JSONObject notification = new JSONObject();
                    //notification.put("body", body);
                    //notification.put("title", title);
                    //notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("userId", userId);
                    data.put("userName", userName);
                    data.put("emailId", email);
                    data.put("title",title);
                    data.put("text",body);
                    //root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", regArray);

                    String result = postToFCM(root.toString());
                    Log.d("HelloUdit", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    //Toast.makeText(getApplicationContext(), "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}
