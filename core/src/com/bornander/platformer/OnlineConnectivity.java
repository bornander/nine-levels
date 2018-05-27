package com.bornander.platformer;

public interface OnlineConnectivity {

	void setCallback(OnlineConnectivityCallback callback);
	boolean isOnlineCapable();
	boolean isSignedIn();
	void signIn();
	void signInSilently();
	void signOut();
	void requestTopScores();
    void requestAchievements();

	void submitScore(int scoreIndex, long score);
	void unlockAchievement(int achievementIndex);
	void viewLeaderBoards();
	void viewAchievements();


}
