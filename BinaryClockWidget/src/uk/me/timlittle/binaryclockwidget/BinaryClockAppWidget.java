package uk.me.timlittle.binaryclockwidget;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;


public class BinaryClockAppWidget extends AppWidgetProvider {
	private static final int WIDGET_CATEGORY_HOME_SCREEN = 1;
	private static final int WIDGET_CATEGORY_KEYGUARD = 2;
	private static final String TAG = "BinaryClockAppkWidget";
	private static final int REQUEST_CODE = 1;
    private Context context = null;
    private static final Intent update = new Intent( BinaryClockService.ACTION_UPDATE );

    
    @Override
    public void onUpdate( Context context, 
        AppWidgetManager appWidgetManager, 
        int[] appWidgetIds )
    {
    	Log.d( TAG, "onUpdate" );
        this.context = context;
        this.context.startService( update );
        updateTime(context, appWidgetManager, appWidgetIds);
        scheduleTimer();
    }
    
    private void scheduleTimer()
    {
        Calendar date = Calendar.getInstance();
        date.set( Calendar.SECOND, 0 );
        date.set( Calendar.MILLISECOND, 0 );
        date.add( Calendar.MINUTE, 1 );
        AlarmManager am =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE );
        PendingIntent pi = PendingIntent.getService( context,
                REQUEST_CODE,
                update,
                PendingIntent.FLAG_NO_CREATE );
        if ( pi == null )
        {
            pi = PendingIntent.getService( context,
                    REQUEST_CODE,
                    update,
                    PendingIntent.FLAG_CANCEL_CURRENT );
            am.setRepeating( AlarmManager.RTC,
                    date.getTimeInMillis(),
                    60 * 1000,
                    //1000,
                    pi );
            Log.d( TAG, "Alarm created" );
        }
    }
    

    @Override
    public void onDeleted( Context context,
                           int[] appWidgetIds )
    {
        Log.d( TAG, "onDeleted" );
        AppWidgetManager mgr = AppWidgetManager.getInstance( context );
        int[] remainingIds = mgr.getAppWidgetIds(
                new ComponentName( context, this.getClass() ) );
        if ( remainingIds == null || remainingIds.length <= 0 )
        {
            PendingIntent pi = PendingIntent.getService( context,
                    REQUEST_CODE,
                    update,
                    PendingIntent.FLAG_NO_CREATE );
            if ( pi != null )
            {
                AlarmManager am =
                        (AlarmManager) context.getSystemService(
                                Context.ALARM_SERVICE );
                am.cancel( pi );
                pi.cancel();
                Log.d( TAG, "Alarm cancelled" );
            }
        }
    }
    
    @SuppressLint("NewApi")
	private void updateTime(Context context, 
	        AppWidgetManager appWidgetManager, 
	        int[] appWidgetIds )
    {
    	Calendar date = Calendar.getInstance();
        //Log.d( TAG, "Update: " + dateFormat.format( date.getTime() ));
        String[] words = TimeToWords.timeToWords( date );
        for ( int id : appWidgetIds )
        {
            Bundle options = appWidgetManager.getAppWidgetOptions( id );
            int layoutId = R.layout.appwidget;
            if(options != null)
            {
                int type = options.getInt( "appWidgetCategory", 1 );
                if(type == WIDGET_CATEGORY_KEYGUARD)
                {
                    layoutId = R.layout.keyguard;
                }
            }
            RemoteViews v = new RemoteViews( context.getPackageName(), 
                layoutId);
            updateTime( words, v );
            appWidgetManager.updateAppWidget( id, v );
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
