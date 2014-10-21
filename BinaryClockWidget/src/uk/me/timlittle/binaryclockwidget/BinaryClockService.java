package uk.me.timlittle.binaryclockwidget;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class BinaryClockService extends IntentService
{
	private static final int WIDGET_CATEGORY_HOME_SCREEN = 1;
	private static final int WIDGET_CATEGORY_KEYGUARD = 2;
    private static final SimpleDateFormat dateFormat = 
        new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
    private static final String TAG = "BinaryClockService";
 
    public static final String ACTION_UPDATE = 
        "uk.me.timlittle.binaryclockappwidget.ACTION_UPDATE";
 
    public BinaryClockService()
    {
        super( TAG );
    }
 
    @Override
    protected void onHandleIntent( Intent intent )
    {
        if(intent.getAction().equals( ACTION_UPDATE ))
        {
            Calendar now = Calendar.getInstance();
            updateTime( now );
        }
    }
     
    
    @SuppressLint("NewApi")
	private void updateTime( Calendar date)
    {
        Log.d( TAG, "Update: " + dateFormat.format( date.getTime() ));
        AppWidgetManager manager = AppWidgetManager.getInstance( this );
        ComponentName name = new ComponentName( this, BinaryClockAppWidget.class );
        int[] appIds = manager.getAppWidgetIds( name );
        String[] words = TimeToWords.timeToWords( date );
        for ( int id : appIds )
        {
            Bundle options = manager.getAppWidgetOptions( id );
            int layoutId = R.layout.appwidget;
            if(options != null)
            {
                int type = options.getInt( "appWidgetCategory", 1 );
                if(type == WIDGET_CATEGORY_KEYGUARD)
                {
                    layoutId = R.layout.keyguard;
                }
            }
            RemoteViews v = new RemoteViews( getPackageName(), 
                layoutId);
            updateTime( words, v );
            manager.updateAppWidget( id, v );
        }
     
    }

    @TargetApi( Build.VERSION_CODES.JELLY_BEAN )
    private int getAppWidgetCategory(AppWidgetManager manager, int id)
    {
        int category = WIDGET_CATEGORY_HOME_SCREEN;
        Bundle options = manager.getAppWidgetOptions( id );
        if ( options != null )
        {
            category = options.getInt( "appWidgetCategory", 1 );
        }
        return category;
    }    
    private void updateTime( String[] words, RemoteViews views )
    {
        views.setTextViewText( R.id.hours, words[0] );
        views.setTextViewText( R.id.minutes, words[1] );
    }
}
