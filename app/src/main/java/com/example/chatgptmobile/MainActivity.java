package com.example.chatgptmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText txt_message;
    ImageButton btn_send;
    List<Message> mMessageList;
    MessageAdapter mMessageAdapter;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#222738"));
        actionBar.setBackgroundDrawable(colorDrawable);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));

        recyclerView = findViewById(R.id.recyclerView);
        txt_message = findViewById(R.id.txt_message);
        btn_send = findViewById(R.id.btn_send);

        mMessageList = new ArrayList<>();

        // Setup the Recycler View.
        mMessageAdapter = new MessageAdapter(mMessageList);
        recyclerView.setAdapter(mMessageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = txt_message.getText().toString().trim();
                addToChat(query, Message.SENT_BY_USER);
                txt_message.setText("");
                callAPI(query);
            }
        });
    }

    void addToChat(String message_text, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageList.add(new Message(message_text, sentBy));
                mMessageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(mMessageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String message) {
        mMessageList.remove(mMessageList.size()-1);
        addToChat(message, Message.SENT_BY_BOT);
    }
    void callAPI(String query) {
        mMessageList.add(new Message("Generating Response... ", Message.SENT_BY_BOT));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo-instruct");
            jsonObject.put("prompt", query);
            jsonObject.put("max_tokens", 4000);
            jsonObject.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer Your api key")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response deu to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    JSONObject jsonObject1;
                    try {
                        jsonObject1 = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject1.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    //addResponse("Failed to load response deu to "+response.body().toString());
                    int responseCode = response.code();
                    String responseBody = response.body() != null ? response.body().string() : "Empty Response Body";
                    String errorMessage = "Failed to load response. Response Code: " + responseCode + ", Body: " + responseBody;
                    addResponse(errorMessage);
                }
            }
        });
    }
}
