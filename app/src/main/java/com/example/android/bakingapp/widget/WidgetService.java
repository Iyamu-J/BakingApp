package com.example.android.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.android.bakingapp.R;

public class WidgetService extends IntentService {

    public static final String ACTION_APPWIDGET_UPDATE = "com.example.android.bakingapp.action.APPWIDGET_UPDATE";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WidgetService() {
        super("WidgetService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
                handleActionUpdateWidget();
            }
        }
    }

    public static void startActionUpdateWidget(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_APPWIDGET_UPDATE);
        context.startService(intent);
    }

    private void handleActionUpdateWidget() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String recipeName = sharedPreferences.getString(getString(R.string.key_recipe_name), getString(R.string.default_value));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingAppWidgetProvider.class));

        BakingAppWidgetProvider.updateAllAppWidgets(this, appWidgetManager,
                appWidgetIds, recipeName);
    }
}
