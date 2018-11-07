package com.example.android.bakingapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipes;
import com.example.android.bakingapp.model.Steps;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class StepsFragment extends Fragment implements Player.EventListener {

    private static final String EXTRA_RECIPE = "EXTRA_RECIPE";
    private static final String PLAYBACK_POSITION = "PLAYBACK_POSITION";
    private static final String PLAYER_STATE = "PLAYER_STATE";

    @BindView(R.id.playerView)
    PlayerView playerView;
    @BindView(R.id.tv_short_desc)
    TextView shortDescTextView;
    @BindView(R.id.tv_desc)
    TextView descTextView;
    @BindView(R.id.progressBar)
    ProgressBar loading;
    @BindView(R.id.btn_prev)
    FloatingActionButton prevButton;
    @BindView(R.id.btn_next)
    FloatingActionButton nextButton;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.constraint_layout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.video_constraint_layout)
    ConstraintLayout videoConstraintLayout;

    private Context mContext;
    private Unbinder unbinder;
    private SimpleExoPlayer mPlayer;
    private Recipes recipes;

    private String videoUrl;
    private boolean videoUrlNotEmpty;
    private boolean mTwoPane;
    private boolean playWhenReady = true;
    private long playbackPosition;
    private int stepId;
    private int stepsListSize;

    public StepsFragment() {
        // Required empty public constructor
    }

    public static StepsFragment newInstance(Recipes recipes) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_RECIPE, recipes);
        StepsFragment stepsFragment = new StepsFragment();
        stepsFragment.setArguments(args);
        return stepsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        stepId = sharedPreferences.getInt(getString(R.string.key_step_id), 0);
        mTwoPane = sharedPreferences.getBoolean(getString(R.string.key_two_pane), false);

        if (getArguments() != null) {
            recipes = getArguments().getParcelable(EXTRA_RECIPE);
            if (recipes != null) {
                stepsListSize = recipes.getSteps().size();
                Steps steps = recipes.getSteps().get(stepId);


                nestedScrollView.setVisibility(VISIBLE);
                constraintLayout.setVisibility(VISIBLE);
                shortDescTextView.setVisibility(VISIBLE);
                descTextView.setVisibility(VISIBLE);

                String shortDesc = steps.getShortDescription();
                shortDescTextView.setText(shortDesc);

                String desc = steps.getDescription();
                descTextView.setText(desc);

                videoUrl = steps.getVideoURL();
                videoUrlNotEmpty = videoUrl != null && !videoUrl.isEmpty();
                showVideo(videoUrlNotEmpty);
            }
        }
        makeButtonVisible();
        handleButtonClicks();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPlayer != null) {
            playbackPosition = mPlayer.getCurrentPosition();
            playWhenReady = mPlayer.getPlayWhenReady();
            outState.putBoolean(PLAYER_STATE, playWhenReady);
            outState.putLong(PLAYBACK_POSITION, playbackPosition);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
            playWhenReady = savedInstanceState.getBoolean(PLAYER_STATE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showVideo(videoUrlNotEmpty);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initializePlayer() {

        Uri uri = Uri.parse(videoUrl);

        if (mPlayer == null) {

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);

            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(mContext),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory),
                    new DefaultLoadControl());
        }

        playerView.setPlayer(mPlayer);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        mPlayer.setPlayWhenReady(playWhenReady);
        mPlayer.seekTo(playbackPosition);

        String userAgent = Util.getUserAgent(mContext, mContext.getPackageName());

        MediaSource mMediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri);


        mPlayer.prepare(mMediaSource, true, false);
        mPlayer.addListener(this);
    }

    private void showVideo(boolean isVideoUrlNotEmpty) {
        if (isVideoUrlNotEmpty) {
            videoConstraintLayout.setVisibility(VISIBLE);
            playerView.setVisibility(VISIBLE);
            loading.setVisibility(VISIBLE);
            initializePlayer();
            hideSystemUi();
        } else {
            videoConstraintLayout.setVisibility(GONE);
            playerView.setVisibility(GONE);
            loading.setVisibility(GONE);
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            playbackPosition = mPlayer.getCurrentPosition();
            playWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.release();
            mPlayer.removeListener(this);
            mPlayer = null;
        }
    }

    private void hideSystemUi() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && !mTwoPane) {

            shortDescTextView.setVisibility(GONE);
            descTextView.setVisibility(GONE);
            prevButton.setVisibility(GONE);
            nextButton.setVisibility(GONE);
            nestedScrollView.setVisibility(GONE);
            constraintLayout.setVisibility(GONE);
        }
    }

    private void handleButtonClicks() {
        makeButtonVisible();
        if (getFragmentManager() != null) {
            // handle clicks on the previous button
            prevButton.setOnClickListener(v -> {
                if (stepId > 0) {
                    stepId--;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    sharedPreferences.edit().putInt(getString(R.string.key_step_id), stepId).apply();
                    StepsFragment fragment = StepsFragment.newInstance(recipes);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_steps_container, fragment)
                            .commit();
                }
                makeButtonVisible();
            });

            // handle clicks on the next button
            nextButton.setOnClickListener(v -> {
                if (stepId < stepsListSize - 1) {
                    stepId++;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    sharedPreferences.edit().putInt(getString(R.string.key_step_id), stepId).apply();
                    StepsFragment fragment = StepsFragment.newInstance(recipes);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_steps_container, fragment)
                            .commit();
                }
                makeButtonVisible();
            });
        }
    }

    /**
     * Shows the either of the Buttons depending on the stepId
     */
    private void makeButtonVisible() {
        if (stepId == 0) {
            prevButton.setVisibility(View.INVISIBLE);
        } else {
            prevButton.setVisibility(View.VISIBLE);
        }
        if (stepId == stepsListSize - 1) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }
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
        if (Player.STATE_IDLE == playbackState) {
            loading.setVisibility(GONE);
        } else if (Player.STATE_BUFFERING == playbackState && playWhenReady) {
            loading.setVisibility(VISIBLE);
        } else if (Player.STATE_READY == playbackState && playWhenReady) {
            loading.setVisibility(GONE);
        } else if (Player.STATE_READY == playbackState) {
            loading.setVisibility(GONE);
        } else if (Player.STATE_ENDED == playbackState) {
            loading.setVisibility(GONE);
        } else if (!playWhenReady) {
            loading.setVisibility(GONE);
        }
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
    }
}
