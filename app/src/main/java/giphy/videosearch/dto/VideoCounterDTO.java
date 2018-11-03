package giphy.videosearch.dto;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class VideoCounterDTO {

    @Id
    public long id;
    String video_id;
    long video_upvote;
    long video_downvote;

    public VideoCounterDTO(long id, String video_id, long video_upvote, long video_downvote) {
        this.id = id;
        this.video_id = video_id;
        this.video_upvote = video_upvote;
        this.video_downvote = video_downvote;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public long getVideo_upvote() {
        return video_upvote;
    }

    public void setVideo_upvote(long video_upvote) {
        this.video_upvote = video_upvote;
    }

    public long getVideo_downvote() {
        return video_downvote;
    }

    public void setVideo_downvote(long video_downvote) {
        this.video_downvote = video_downvote;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}




