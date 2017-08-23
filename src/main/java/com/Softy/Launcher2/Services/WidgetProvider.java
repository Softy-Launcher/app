package com.Softy.Launcher2.Services;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.Softy.Launcher2.MiniSettings;
import com.Softy.Launcher2.R;
import com.Softy.Launcher2.Settings.Drawer;
import com.Softy.Launcher2.Settings.General;
import com.Softy.Launcher2.Settings.Workspace;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetProviderConfigureActivity WidgetProviderConfigureActivity}
 */
public class WidgetProvider extends AppWidgetProvider {
    private static String packageName;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        packageName = context.getPackageName();
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetProviderConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        CharSequence widgetText = WidgetProviderConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget);

        Intent general = new Intent(context,General.class);
        Intent workspace = new Intent(context,Workspace.class);
        Intent drawer = new Intent(context, Drawer.class);
        Intent settings = new Intent(context, MiniSettings.class);

        PendingIntent gPI = PendingIntent.getActivity(context, 0, general, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent wPI = PendingIntent.getActivity(context, 0 , workspace, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent dPI = PendingIntent.getActivity(context, 0, drawer, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent sPI = PendingIntent.getActivity(context, 0, settings, PendingIntent.FLAG_UPDATE_CURRENT);

        v.setOnClickPendingIntent(R.id.general_op, gPI);
        v.setOnClickPendingIntent(R.id.workspace_op, wPI);
        v.setOnClickPendingIntent(R.id.drawer_op, dPI);
        v.setOnClickPendingIntent(R.id.settings_op, sPI);

        //update the widget
        appWidgetManager.updateAppWidget(appWidgetId, v);
    }

    public static String getPackageName(){
        return packageName;
    }
}

