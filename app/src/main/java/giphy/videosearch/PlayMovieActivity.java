package giphy.videosearch;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import giphy.videosearch.R;
import giphy.videosearch.dto.VideoCounterDTO;
import giphy.videosearch.dto.VideoCounterDTO_;
import giphy.videosearch.httprequest.MyApplication;
import io.objectbox.Box;
import io.objectbox.BoxStore;


public class PlayMovieActivity extends Activity implements AdaptiveMediaSourceEventListener{

    @BindView(R.id.sv_player) SurfaceView svPlayer;
    @BindView(R.id.prev) ImageButton prev;
    @BindView(R.id.rew) ImageButton rew;
    @BindView(R.id.btnPlay) ImageButton btnPlay;
    @BindView(R.id.ffwd) ImageButton ffwd;
    @BindView(R.id.next) ImageButton next;
    @BindView(R.id.time_current) TextView timeCurrent;
    @BindView(R.id.mediacontroller_progress) SeekBar mediacontrollerProgress;
    @BindView(R.id.player_end_time) TextView playerEndTime;
    @BindView(R.id.fullscreen) ImageButton fullscreen;
    @BindView(R.id.lin_media_controller) LinearLayout linMediaController;
    @BindView(R.id.player_frame_layout) FrameLayout playerFrameLayout;
    @BindView(R.id.bt_upvote) Button bt_upvote;
    @BindView(R.id.bt_downvote) Button bt_downvote;
    @BindView(R.id.tx_upvote) TextView tx_upvote;
    @BindView(R.id.tx_downvote) TextView tx_downvote;
    private SimpleExoPlayer exoPlayer;
    private boolean bAutoplay = true;
    private boolean bIsPlaying = false;
    private boolean bControlsActive = true;

    private Handler handler;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private DataSource.Factory dataSourceFactory;

    BoxStore boxStore;
    Box<VideoCounterDTO> videoBox;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_movie);
        final String video_id = getIntent().getExtras().getString("id");
        String video_url = getIntent().getExtras().getString("mp4");


        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler=new Handler();

        initDataSource();
        initMp4Player(video_url);

        // Initialization of objectbox database
        boxStore = MyApplication.getApp().getBoxStore();
        videoBox = boxStore.boxFor(VideoCounterDTO.class);

        // Fire query to get all videolist
        List<VideoCounterDTO> videolist = videoBox.query().equal(VideoCounterDTO_.video_id, video_id).build().find();
        // If videolist is empty
        if(videolist.isEmpty()) {
            VideoCounterDTO video_row = new VideoCounterDTO(0, video_id, 0, 0);
            videoBox.put(video_row);
            bt_upvote.setText("0  UPVOTE");
            bt_downvote.setText("0 DOWNVOTE");
        }
        else {
            long video_upvote_count =  videolist.get(0).getVideo_upvote();
            long video_downvote_count =  videolist.get(0).getVideo_downvote();
            bt_upvote.setText(video_upvote_count+" UPVOTE");
            bt_downvote.setText(video_downvote_count+" DOWNVOTE");
        }

        if (bAutoplay) {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(true);
                bIsPlaying = true;
                setProgress();
            }
        }

        // upvote button click event
        bt_upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<VideoCounterDTO> video = videoBox.query().equal(VideoCounterDTO_.video_id, video_id).build().find();
                if(!video.isEmpty()) {
                   long video_upvote_data =  video.get(0).getVideo_upvote()+1;
                    VideoCounterDTO videocount = videoBox.get(video.get(0).getId());
                    videocount.setVideo_upvote(video_upvote_data);
                    videoBox.put(videocount);
                    bt_upvote.setText(video_upvote_data+" UPVOTE");
                }


            }
        });

        // Downvote button click event
        bt_downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<VideoCounterDTO> video = videoBox.query().equal(VideoCounterDTO_.video_id, video_id).build().find();
                if(!video.isEmpty()) {
                    long video_downvote_data =  video.get(0).getVideo_downvote()+1;
                    VideoCounterDTO videocount = videoBox.get(video.get(0).getId());
                    videocount.setVideo_downvote(video_downvote_data);
                    videoBox.put(videocount);
                    bt_downvote.setText(video_downvote_data+" DOWNVOTE");
                }
            }
        });


    }

    private void initDataSource() {
        dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"),
                        new DefaultBandwidthMeter());
    }

    private void initMediaControls() {
        initSurfaceView();
        initPlayButton();
        initSeekBar();
        initFwd();
        initPrev();
        initRew();
        initNext();
    }

    private void initNext() {
        next.requestFocus();
        next.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                exoPlayer.seekTo(exoPlayer.getDuration());
            }
        });
    }

    private void initRew() {
        rew.requestFocus();
        rew.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
            }
        });
    }

    private void initPrev() {
        prev.requestFocus();
        prev.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                exoPlayer.seekTo(0);
            }
        });
    }

    private void initFwd() {
        ffwd.requestFocus();
        ffwd.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
            }
        });
    }



    private void initSurfaceView() {
        svPlayer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                toggleMediaControls();
            }
        });
    }

    private String stringForTime(int timeMs) {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
        mediacontrollerProgress.setProgress(0);
        mediacontrollerProgress.setMax(0);
        mediacontrollerProgress.setMax((int) exoPlayer.getDuration() / 1000);

        handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {

            @Override public void run() {
                if (exoPlayer != null && bIsPlaying) {
                    mediacontrollerProgress.setMax(0);
                    mediacontrollerProgress.setMax((int) exoPlayer.getDuration() / 1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    mediacontrollerProgress.setProgress(mCurrentPosition);
                    timeCurrent.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
                    playerEndTime.setText(stringForTime((int) exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initSeekBar() {
        mediacontrollerProgress.requestFocus();

        mediacontrollerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                exoPlayer.seekTo(progress * 1000);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediacontrollerProgress.setMax(0);
        mediacontrollerProgress.setMax((int) exoPlayer.getDuration() / 1000);
    }

    private void toggleMediaControls() {

        if (bControlsActive) {
            hideMediaController();
            bControlsActive = false;
        } else {
            showController();
            bControlsActive = true;
            setProgress();
        }
    }

    private void showController() {
        linMediaController.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideMediaController() {
        linMediaController.setVisibility(View.GONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initPlayButton() {
        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (bIsPlaying) {
                    exoPlayer.setPlayWhenReady(false);
                    bIsPlaying = false;
                } else {
                    exoPlayer.setPlayWhenReady(true);
                    bIsPlaying = true;
                    setProgress();
                }
            }
        });
    }

    private void initMp4Player(String mp4URL) {

        MediaSource sampleSource =
                new ExtractorMediaSource(Uri.parse(mp4URL), dataSourceFactory, new DefaultExtractorsFactory(),
                        handler, new ExtractorMediaSource.EventListener() {
                    @Override public void onLoadError(IOException error) {

                    }
                });


        initExoPlayer(sampleSource);
    }

    private void initExoPlayer(MediaSource sampleSource) {
        if (exoPlayer == null) {
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        }

        exoPlayer.prepare(sampleSource);

        exoPlayer.setVideoSurfaceView(svPlayer);

        exoPlayer.setPlayWhenReady(true);

        initMediaControls();
    }

    @Override
    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                              int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                              long mediaEndTimeMs, long elapsedRealtimeMs) {

    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                                int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                                long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                               int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                               long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                            int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                            long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded,
                            IOException error, boolean wasCanceled) {

    }

    @Override
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason,
                                          Object trackSelectionData, long mediaTimeMs) {

    }
}
