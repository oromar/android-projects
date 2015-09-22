package br.com.oromar.dev.android.sunshine;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import br.com.oromar.dev.android.sunshine.data.WeatherContract;


public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String HASHTAG_FORECAST_SHARE = " #SunshineApp";
    private String mForecastDetail;
    private static final int DETAIL_LOADER = 0;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_share, menu);
        MenuItem menuItem = menu.findItem(R.id.android_share_link);
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createIntentShare());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mForecastDetail = intent.getDataString();
        }
        ForecastAdapter.DetailViewHolder holder = new ForecastAdapter.DetailViewHolder(rootView);
        rootView.setTag(holder);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private Intent createIntentShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastDetail + HASHTAG_FORECAST_SHARE);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent i = getActivity().getIntent();
        if ( i == null) {
            return null;
        }
        CursorLoader loader = new CursorLoader(getActivity(),
                i.getData(),
                ForecastAdapter.FORECAST_COLUMNS,
                null,
                null,
                WeatherContract.WeatherEntry.COLUMN_DATE + " ASC ");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean isMetric = Utility.isMetric(getActivity());
        if (!data.moveToFirst()){
            return;
        }
        long date = data.getLong(ForecastAdapter.COL_WEATHER_DATE);
        double max = data.getDouble(ForecastAdapter.COL_WEATHER_MAX_TEMP);
        double min = data.getDouble(ForecastAdapter.COL_WEATHER_MIN_TEMP);
        String description = data.getString(ForecastAdapter.COL_WEATHER_DESC);
        String humidity = data.getString(ForecastAdapter.COL_HUMIDITY);
        String pressure = data.getString(ForecastAdapter.COL_PRESSURE);
        String windSpeed = data.getString(ForecastAdapter.COL_WIND_SPEED);

        ForecastAdapter.DetailViewHolder holder = (ForecastAdapter.DetailViewHolder) getView().getTag();
        holder.date.setText(Utility.getFriendlyDayString(getActivity(), date));
        holder.description.setText(description);
        holder.highTemperature.setText(Utility.formatTemperature(getActivity(), max, isMetric));
        holder.lowTemperature.setText(Utility.formatTemperature(getActivity(), min, isMetric));
        holder.imageIcon.setImageResource(Utility.getImageResource(description));
        holder.humidity.setText(humidity);
        holder.pressure.setText(pressure);

        holder.wind.setText(windSpeed);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }



}
