package com.example.android.bakingapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import timber.log.Timber;


public class StepsFragment extends Fragment implements Player.EventListener {

    private static final String EXTRA_SHORT_DESCRIPTION = "EXTRA_SHORT_DESCRIPTION";
    private static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    private static final String EXTRA_VIDEO_URL = "EXTRA_VIDEO_URL";

    private Context mContext;

    private PlayerView playerView;
    private TextView shortDescTextView;
    private TextView descTextView;
    private ProgressBar loading;
    private Guideline horizontalHalf;

    private SimpleExoPlayer mPlayer;
    private MediaSource mMediaSource;

    private String videoUrl;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    public StepsFragment() {
        // Required empty public constructor
    }

    public static StepsFragment newInstance(String shortDec, String desc, String videoUrl) {
        Bundle args = new Bundle();
        args.putString(EXTRA_SHORT_DESCRIPTION, shortDec);
        args.putString(EXTRA_DESCRIPTION, desc);
        args.putString(EXTRA_VIDEO_URL, videoUrl);
        StepsFragment stepsFragment = new StepsFragment();
        stepsFragment.setArguments(args);
        return stepsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        playerView = view.findViewById(R.id.playerView);
        shortDescTextView = view.findViewById(R.id.tv_short_desc);
        descTextView = view.findViewById(R.id.tv_desc);
        loading = view.findViewById(R.id.progressBar);
        horizontalHalf = view.findViewById(R.id.horizontalHalf);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();
        if (getArguments() != null) {
            String shortDesc = getArguments().getString(EXTRA_SHORT_DESCRIPTION);
            shortDescTextView.setText(shortDesc);

            String desc = getArguments().getString(EXTRA_DESCRIPTION);
            descTextView.setText(desc);

            videoUrl = getArguments().getString(EXTRA_VIDEO_URL);
            if (videoUrl != null && !videoUrl.isEmpty()) {
                playerView.setVisibility(View.VISIBLE);
                horizontalHalf.setVisibility(View.VISIBLE);
                loading.setVisibility(View.VISIBLE);
                initializePlayer();
            } else {
                playerView.setVisibility(View.GONE);
                horizontalHalf.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (videoUrl != null && !videoUrl.isEmpty()) {
                playerView.setVisibility(View.VISIBLE);
                horizontalHalf.setVisibility(View.VISIBLE);
                loading.setVisibility(View.VISIBLE);
                initializePlayer();
            } else {
                playerView.setVisibility(View.GONE);
                horizontalHalf.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            playerView.setVisibility(View.VISIBLE);
            horizontalHalf.setVisibility(View.VISIBLE);
            loading.setVisibility(View.VISIBLE);
            initializePlayer();
        } else {
            playerView.setVisibility(View.GONE);
            horizontalHalf.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    void initializePlayer() {

        Uri uri = Uri.parse(videoUrl);

        if (mPlayer == null) {

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);

            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(mContext),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory),
                    new DefaultLoadControl());

            playerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(playWhenReady);
            mPlayer.seekTo(currentWindow, playbackPosition);
        }

        String userAgent = Util.getUserAgent(mContext, mContext.getPackageName());

        mMediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri);


        mPlayer.prepare(mMediaSource, true, false);
        mPlayer.addListener(this);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        String stateString;
        switch (playbackState) {
            case Player.STATE_IDLE:
                stateString = "ExoPlayer.STATE_IDLE      -";
                loading.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_BUFFERING:
                stateString = "ExoPlayer.STATE_BUFFERING -";
                loading.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_READY:
                stateString = "ExoPlayer.STATE_READY     -";
                loading.setVisibility(View.GONE);
                break;
            case Player.STATE_ENDED:
                stateString = "ExoPlayer.STATE_ENDED     -";
                break;
            default:
                stateString = "UNKNOWN_STATE             -";
                break;
        }
        Timber.d("changed state to %s playWhenReady: %s", stateString, playWhenReady);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Timber.d(error);
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
        mPlayer.seekTo(currentWindow, playbackPosition);
        mPlayer.prepare(mMediaSource, true, false);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            playbackPosition = mPlayer.getCurrentPosition();
            playWhenReady = mPlayer.getPlayWhenReady();
            currentWindow = mPlayer.getCurrentWindowIndex();
            mPlayer.release();
            mPlayer.removeListener(this);
            mPlayer = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
