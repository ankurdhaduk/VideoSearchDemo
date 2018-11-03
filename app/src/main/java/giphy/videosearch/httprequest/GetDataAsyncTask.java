package giphy.videosearch.httprequest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;



public class GetDataAsyncTask extends AsyncTask<String, Integer, String> implements Utils {
    private String methodName;
    private Context mContext;
    private String broadcastID;
    private String[] params;

    public GetDataAsyncTask(String methodName, String broadcastID) {
        this.methodName = methodName;
        this.broadcastID = broadcastID;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        mContext = MyApplication.getAppContext();
    }

    protected String doInBackground(String... params) {
        this.params = params;
        String response = "";
        switch (methodName) {
            case _API_URL_VIDEOLIST:
               response =  getVideoList();
                break;
            default:
                break;
        }

        return response;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
                Intent intent;
                intent = new Intent(broadcastID);
                intent.putExtra(_BROADCAST_TYPE, _ASYNC_TASK);
                ShowLogToast.ShowLog("_ASYNC_TASK---" + _ASYNC_TASK);
                intent.putExtra(_RESPONSE_ASYNC, result);
                intent.putExtra(_TYPE_ASYNC, methodName);
                mContext.sendBroadcast(intent);
        } catch (Exception e) {
            ShowLogToast.ShowLog("Async Task exception:\n" + e.getMessage());
        }
    }


    private String getVideoList() {
        String search_text = params[0];
        return WebServiceCall.getVideoListCall(search_text);
    }





}