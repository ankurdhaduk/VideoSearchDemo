package giphy.videosearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import giphy.videosearch.dto.VideoDTO;


public class GiphyVideoAdapter extends BaseAdapter {

    ArrayList<VideoDTO> videolist = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater=null;

    public GiphyVideoAdapter(Context context, ArrayList<VideoDTO> get_videolist){

        this.context=context;
        this.videolist=get_videolist;
    }


    @Override
    public int getCount() {
        return videolist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view=convertView;

        if(view==null)

            view=inflater.inflate(R.layout.giphy_video_grid_row,null);

        ImageView video_thumb=view.findViewById(R.id.video_thumb);
        video_thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
        video_thumb.setPadding(8, 8, 8, 8);
        TextView tv_title= view.findViewById(R.id.tv_title);
        tv_title.setText(videolist.get(position).getTitle());
        return  view;

    }


}
