package com.tyolar.inc.musica.library.media;

public interface MediaListener {

    void onMediaLoading();

    void onMediaStarted(int totalDuration, int currentDuration);

    void onMediaStopped();

}
