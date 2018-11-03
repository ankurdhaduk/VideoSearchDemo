package giphy.videosearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import giphy.videosearch.dto.VideoDTO;
import giphy.videosearch.httprequest.ConnectionDetector;
import giphy.videosearch.httprequest.GetDataAsyncTask;
import giphy.videosearch.httprequest.Utils;


public class SearchVideoActivity extends AppCompatActivity implements Utils {

    private boolean receiverRegistered = false;
    ArrayList<VideoDTO> movielist = new ArrayList<>();
    GridView gv_movie;
    EditText ed_search;
    Button bt_submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCreateView();
        gridOnClick();
        buttonOnClick();

        if (!receiverRegistered) {
            registerReceiver(videolistReceiver, new IntentFilter(_VIDEO_LIST_BROADCAST));
            receiverRegistered = true;
        }
    }


    void setCreateView()
    {
        gv_movie = findViewById(R.id.gv_movie);
        ed_search = findViewById(R.id.ed_search);
        bt_submit = findViewById(R.id.bt_submit);
    }
    void gridOnClick()
    {
        gv_movie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent_gridview = new Intent(SearchVideoActivity.this,PlayMovieActivity.class);
                intent_gridview.putExtra("id",movielist.get(position).getId());
                intent_gridview.putExtra("mp4",movielist.get(position).getMp4());
                startActivity(intent_gridview);

            }
        });
    }

    void buttonOnClick()
    {
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movielist.clear();
                if (new ConnectionDetector().isOnline()) {
                    new GetDataAsyncTask(_API_URL_VIDEOLIST, _VIDEO_LIST_BROADCAST).execute(ed_search.getText().toString());
                }
                else
                {
                    Toast.makeText(SearchVideoActivity.this,"Please check your internet",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private BroadcastReceiver videolistReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                switch (bundle.getString(_BROADCAST_TYPE)) {
                    case _ASYNC_TASK:
                        getAsyncResult(bundle);
                        break;
                }
            }
        }
    };

    void getAsyncResult(Bundle bundle) {
        String response = bundle.getString(_RESPONSE_ASYNC);
        String asyncType = bundle.getString(_TYPE_ASYNC);

        switch (asyncType) {
            case _API_URL_VIDEOLIST:
                        getVideoListResponse(response);
                        break;
                    default:
                        break;
                }
    }

    void getVideoListResponse(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            // Getting JSON Array node
            JSONArray data = jsonObj.getJSONArray("data");
            // looping through All VideoList
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                // Get video id from JsonObject
                String id = c.getString("id");
                // Get video title from JsonObject
                String title = c.getString("title");
                // Image node is JSON Object
                JSONObject images = c.getJSONObject("images");
                // hd node in Images JSON Object
                JSONObject hd = images.getJSONObject("hd");
                // Get mp4 key from hd node
                String mp4 = hd.getString("mp4");
                VideoDTO moviedto = new VideoDTO(id,title,mp4);
                movielist.add(moviedto);
                Log.d("dangerlist",movielist.get(i).getId());
                Log.d("dangerlist",movielist.get(i).getMp4());
            }
             setGridAdapter();

        }
     catch (final JSONException e) {
         Log.e("movielist", "Json parsing error: " + e.getMessage());
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 Toast.makeText(getApplicationContext(),
                         "Json parsing error: " + e.getMessage(),
                         Toast.LENGTH_LONG)
                         .show();
             }
         });

     }
    }

    void setGridAdapter() {
        GiphyVideoAdapter christmasImagesAdapter = new GiphyVideoAdapter(SearchVideoActivity.this, movielist);
        gv_movie.setAdapter(christmasImagesAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!receiverRegistered) {
            registerReceiver(videolistReceiver, new IntentFilter(_VIDEO_LIST_BROADCAST));
            receiverRegistered = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiverRegistered) {
            unregisterReceiver(videolistReceiver);
            receiverRegistered = false;
        }
    }

}
