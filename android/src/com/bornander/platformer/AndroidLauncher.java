package com.bornander.platformer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.bornander.libgdx.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Iterator;

public class AndroidLauncher extends AndroidApplication implements OnlineConnectivity {

    private final static int RC_UNUSED = 1000;
    private final static int RC_SIGN_IN = 1001;

    private OnlineConnectivityCallback currentCallback = null;
    private GoogleSignInClient signInClient;
    private GoogleSignInAccount currentAccount = null;
    private LeaderboardsClient leaderboardsClient = null;
    private AchievementsClient achievementsClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        PlatformerGame game = new PlatformerGame(this);
        setCallback(game);
        initialize(game, config);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);
    }



    @Override
    public void signInSilently() {
        Log.info("signInSilently()");

        signInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            try {
                                Log.info("signInSilently(): success");
                                currentAccount = task.getResult(ApiException.class);
                                Log.info("account" + currentAccount);
                                leaderboardsClient = Games.getLeaderboardsClient(AndroidLauncher.this, currentAccount);
                                achievementsClient = Games.getAchievementsClient(AndroidLauncher.this, currentAccount);

                                currentCallback.onlineSignIn();
                            }
                            catch(ApiException e) {
                                Log.info("signInSilently; failed " + e.getMessage());
                            }
                        } else {
                            Log.info("signInSilently(): failure", task.getException());
                        }
                    }
                });
    }

    @Override
    public void setCallback(OnlineConnectivityCallback callback)
    {
        currentCallback = callback;
    }

    @Override
    public boolean isOnlineCapable() {
        return true;
    }


    @Override
    public boolean isSignedIn() {
        Log.info("isSignedIn");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.info("isSignedIn returns %b, %s", (account != null), account);
        return account != null;
    }

    @Override
    public void signIn() {
        Log.info("signIn");
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void signOut() {
        Log.info("signOut");

        Task<Void> signOutTask = signInClient.signOut();
        signOutTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.info("signOutTask onCompleted %b", task.isSuccessful());
                currentCallback.onlineSignOut();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.info("onActivityResult(%d, %d, %s)", requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.info("Activity sing-in completed, getting signed in account data");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.info("handleSignInResult");
        try {
            Log.info("handleSignInResult " + completedTask.isSuccessful());
            currentAccount = completedTask.getResult(ApiException.class);
            Log.info("account" + currentAccount);
            leaderboardsClient = Games.getLeaderboardsClient(this, currentAccount);
            achievementsClient = Games.getAchievementsClient(this, currentAccount);
            if (currentCallback != null)
                currentCallback.onlineSignIn();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.info("signInResult:failed code=" + e.getStatusCode());
            currentCallback.onlineError();
        }
    }

    public void submitScore(int scoreIndex, long time) {
        Log.info("submitting score %d", scoreIndex);
        if (isSignedIn() && leaderboardsClient != null) {
            String leaderboard = getLeaderboard(scoreIndex);
            Log.info("submitting score to board %s", leaderboard);
            leaderboardsClient.submitScore(leaderboard, time);
        }
    }

    public void unlockAchievement(int achievemnetIndex) {
        if (isSignedIn() && achievementsClient != null) {
            String achievement = getAchievement(achievemnetIndex);
            Log.info("Unlocking achievement %d, %s", achievemnetIndex, achievement);
            achievementsClient.unlock(achievement);
        }
    }

    public void requestTopScores() {
        for (int i = 1; i < 9; ++i)
            requestTopScore(i);
    }

    @Override
    public void requestAchievements() {
        Log.info("requestAchievements");
        if (isSignedIn() && achievementsClient != null) {
            Log.info("requestAchievements; is signed in and has client");
            Task<AnnotatedData<AchievementBuffer>> task = achievementsClient.load(false);
            task.addOnCompleteListener(this, new OnCompleteListener<AnnotatedData<AchievementBuffer>>() {
                @Override
                public void onComplete(@NonNull Task<AnnotatedData<AchievementBuffer>> task) {
                    Log.info("requestAchievements; task complete");
                    if (task.isSuccessful()) {
                        AnnotatedData<AchievementBuffer> result = task.getResult();
                        AchievementBuffer buffer = result.get();
                        for (Achievement achievement : buffer) {
                            Log.info("Reading achievement %s", achievement);
                            if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                                Log.info("Achievement is unlocked");
                                int index = getIndexFromAchievementId(achievement.getAchievementId());
                                currentCallback.onlineAchivementUnlocked(index);
                            } else {
                                Log.info("Achievement is NOT unlocked");
                            }
                        }
                    }
                    else
                        Log.info("requestAchievements ; task failed");

                }
            });
        }
    }

    public void requestTopScore(final int scoreIndex) {
        if (isSignedIn() && leaderboardsClient != null) {
            final String leaderboard = getLeaderboard(scoreIndex);
            Task<AnnotatedData<LeaderboardsClient.LeaderboardScores>> task = leaderboardsClient.loadTopScores(leaderboard, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC, 1);
            task.addOnCompleteListener(this, new OnCompleteListener<AnnotatedData<LeaderboardsClient.LeaderboardScores>>() {
                @Override
                public void onComplete(@NonNull Task<AnnotatedData<LeaderboardsClient.LeaderboardScores>> task) {
                    Log.info("requestTopScore::onComplete %d, %s", scoreIndex, leaderboard);
                    Log.info("requestTopScore::onComplete task.isSuccessful()=" + task.isSuccessful());
                    try {
                        if (task.isSuccessful()) {
                            AnnotatedData<LeaderboardsClient.LeaderboardScores> result = task.getResult();
                            LeaderboardsClient.LeaderboardScores scores = result.get();
                            LeaderboardScoreBuffer buffer = scores.getScores();
                            for (LeaderboardScore aBuffer : buffer) {
                                Log.info("iterating over item");
                                currentCallback.onlineTopScore(scoreIndex, aBuffer.getRawScore());
                                break;
                            }
                            buffer.release();
                            scores.release();
                        }
                        else
                            Log.info("requestTopScore; task failed");
                    } catch (Exception e) {
                        com.bornander.libgdx.Log.info("getTopScore::onComplete: " + e.getMessage());
                    }
                }
            });
        }
    }



    @Override
    public void viewLeaderBoards() {
        Log.info("viewLeaderBoards");
        if (isSignedIn() && leaderboardsClient != null) {
            Log.info("viewLeaderBoards; isSignedIn and has leaderboardsClient");
            leaderboardsClient.getAllLeaderboardsIntent()
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            Log.info("viewLeaderBoards; starting activity for result");
                            startActivityForResult(intent, RC_UNUSED);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handleException(e, "leaderboard problem");
                        }
                    });
        }
    }

    @Override
    public void viewAchievements() {
        Log.info("viewAchievements");
        if (isSignedIn() && achievementsClient != null) {
            Log.info("viewAchievements; isSignedIn and has achievementsClient");
            achievementsClient.getAchievementsIntent()
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            Log.info("viewAchievements; starting activity for result");
                            startActivityForResult(intent, RC_UNUSED);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handleException(e, "achievements problem");
                        }
                    });
        }
    }

    private void handleException(Exception e, String details) {
        new AlertDialog.Builder(AndroidLauncher.this)
                .setMessage("An error occured; " + e.getMessage() + ", " + details)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    private String getLeaderboard(int index) {
        switch (index) {
            case 1:
                return getString(R.string.leaderboard_1);
            case 2:
                return getString(R.string.leaderboard_2);
            case 3:
                return getString(R.string.leaderboard_3);
            case 4:
                return getString(R.string.leaderboard_4);
            case 5:
                return getString(R.string.leaderboard_5);
            case 6:
                return getString(R.string.leaderboard_6);
            case 7:
                return getString(R.string.leaderboard_7);
            case 8:
                return getString(R.string.leaderboard_8);
            case 9:
                return getString(R.string.leaderboard_9);
        }
        return null;
    }

    private String getAchievement(int index) {
        switch (index) {
            case 1:
                return getString(R.string.achievement_1);
            case 2:
                return getString(R.string.achievement_2);
            case 3:
                return getString(R.string.achievement_3);
            case 4:
                return getString(R.string.achievement_4);
            case 5:
                return getString(R.string.achievement_5);
            case 6:
                return getString(R.string.achievement_6);
            case 7:
                return getString(R.string.achievement_7);
            case 8:
                return getString(R.string.achievement_8);
            case 9:
                return getString(R.string.achievement_9);
        }
        return null;
    }

    private int getIndexFromAchievementId(String id) {
        if (id.equals(getString(R.string.achievement_1))) return 1;
        if (id.equals(getString(R.string.achievement_2))) return 2;
        if (id.equals(getString(R.string.achievement_3))) return 3;
        if (id.equals(getString(R.string.achievement_4))) return 4;
        if (id.equals(getString(R.string.achievement_5))) return 5;
        if (id.equals(getString(R.string.achievement_6))) return 6;
        if (id.equals(getString(R.string.achievement_7))) return 7;
        if (id.equals(getString(R.string.achievement_8))) return 8;
        if (id.equals(getString(R.string.achievement_9))) return 9;
        return 0;
    }

}
