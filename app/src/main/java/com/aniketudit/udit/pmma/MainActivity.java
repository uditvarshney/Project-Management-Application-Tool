package com.aniketudit.udit.pmma;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public String receiver="eKMFD2BNBQ0:APA91bFDGnpY5IwMeOhksvomQ4fQMGMvvxz9a7HS8SToHzAMz5ul708_ChUchP8toHr4nY1drv5zw1o50daWtkpu-SUeNa4qMogrDz8i1pmAtrFQ78ACQH3B3u0cho6YyELIu-OByaxE";
    OkHttpClient mClient;
    private String SERVER_KEY="AAAAyxjhfF0:APA91bHh1KXXBXnVQWQbl9LpzVnHrkCwlUOQul9yIWXK2j6R10K2VkFZSTCVoSf8ERTCZvKJLswRuaF3_ZUCWbaNufZ3F-3GYTPwmAGJ1u-me_oXf5nNfu3wI5PTakLol4GiXHn9MUIx";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=(Button) findViewById(R.id.sendNotification);
        mClient = new OkHttpClient();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray regArray= null;
                        regArray = new JSONArray();
                regArray.put(receiver);
                //regArray.put(receiver);
                sendMessage(regArray,"First Message Firebase","helo udit varshney","R.mipmap.ic_launcher","xdsdxdffg","Kumar","29udit@gamil.com");
            }
        });
    }
    public void sendMessage(final JSONArray recipients, final String title, final String body, final String icon, final String userId, final String userName, final String emailId) {

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
                    data.put("emailId", emailId);
                    data.put("title",title);
                    data.put("text",body);
                    //root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);

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
                    Toast.makeText(getApplicationContext(), "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
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
