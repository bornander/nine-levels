package com.bornander.platformer;

public interface OnlineConnectivityCallback {
    void onlineSignIn();
    void onlineSignOut();
    void onlineError();
    void onlineTopScore(int scoreIndex, long score);
    void onlineAchivementUnlocked(int index);
}