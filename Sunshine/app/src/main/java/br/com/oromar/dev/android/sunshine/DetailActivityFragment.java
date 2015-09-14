package br.com.oromar.dev.android.sunshine;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.TextView;

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
        TextView text = (TextView) rootView.findViewById(R.id.text_detail_activity);
        if (intent != null){
            mForecastDetail = intent.getDataString();
        }
        text.setText(mForecastDetail);
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
        mForecastDetail = Utility.formatDate(date) +
                " - " + data.getString(ForecastAdapter.COL_WEATHER_DESC) +
                " - " + Utility.formatTemperature(getActivity(), max, isMetric) +
                " / " + Utility.formatTemperature(getActivity(), min, isMetric);
        TextView txt = (TextView) getView().findViewById(R.id.text_detail_activity);
        txt.setText(mForecastDetail);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
