package giphy.videosearch.dto;

public class VideoDTO {

    String id;
    String title;
    String mp4;
    public VideoDTO(String id, String title, String mp4) {
        this.id = id;
        this.title = title;
        this.mp4 = mp4;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMp4() {
        return mp4;
    }

    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }







}
