package br.com.oromar.dev.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.oromar.dev.android.sunshine.data.WeatherContract;

public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    public static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.LocationEntry.COORD_LAT,
            WeatherContract.LocationEntry.COORD_LONG,
            WeatherContract.LocationEntry.CITY_NAME
    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_HUMIDITY = 7;
    static final int COL_PRESSURE = 8;
    static final int COL_WIND_SPEED = 9;
    static final int COL_COORD_LAT = 10;
    static final int COL_COORD_LONG = 11;
    static final int COL_CITY_NAME = 12;


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());

        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY) {

            layoutId = R.layout.list_item_forecast_today;

        } else {

            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int position = cursor.getPosition();
        int viewType = getItemViewType(position);

        long date = cursor.getLong(ForecastAdapter.COL_WEATHER_DATE);
        double min = cursor.getDouble(COL_WEATHER_MIN_TEMP);
        double max = cursor.getDouble(COL_WEATHER_MAX_TEMP);
        String desc = cursor.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(context);

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (viewType == VIEW_TYPE_TODAY) {
            viewHolder.imageIcon.setImageResource(Utility.getImageResourceToday(desc));
        } else {
            viewHolder.imageIcon.setImageResource(Utility.getImageResource(desc));
        }
        viewHolder.date.setText(Utility.getFriendlyDayString(context, date));
        viewHolder.lowTemperature.setText(Utility.formatTemperature(mContext, min, isMetric));
        viewHolder.highTemperature.setText(Utility.formatTemperature(mContext, max, isMetric));
        viewHolder.description.setText(desc);
    }

    public static class ViewHolder {

        public final TextView date;
        public final TextView highTemperature;
        public final TextView lowTemperature;
        public final TextView description;
        public final ImageView imageIcon;

        public ViewHolder(View view) {
            date = (TextView) view.findViewById(R.id.list_item_date_textview);
            highTemperature = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTemperature = (TextView) view.findViewById(R.id.list_item_low_textview);
            description = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            imageIcon = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }

    public static class DetailViewHolder extends ViewHolder {

        public final TextView humidity;
        public final TextView wind;
        public final TextView pressure;
        public final TextView location;

        public DetailViewHolder(View view) {
            super(view);
            humidity = (TextView) view.findViewById(R.id.list_item_humidity_text_view);
            wind = (TextView) view.findViewById(R.id.list_item_wind_text_view);
            pressure = (TextView) view.findViewById(R.id.list_item_pressure_text_view);
            location = (TextView) view.findViewById(R.id.list_item_location_textview);
        }
    }
}