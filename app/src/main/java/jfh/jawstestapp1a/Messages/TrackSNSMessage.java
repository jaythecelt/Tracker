package jfh.jawstestapp1a.Messages;


import jfh.jawstestapp1a.dao.Track;

public class TrackSNSMessage extends BaseMessage {
    public static final String TRACK_KEY = "track";

    private Track track = null;

    public Track getTrack() {
        if (track==null)
            track = new Track();
        return track;
    }
    public void setTrack(Track track) {
        this.track = track;
    }

}
