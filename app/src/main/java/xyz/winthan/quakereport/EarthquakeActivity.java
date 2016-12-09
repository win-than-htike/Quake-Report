package xyz.winthan.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>>{

    private ListView earthquakeListView;

    private EarthquakeAdapter mAdapter;

    private TextView mEmptyStateTextView;

    private static final int EARTHQUAKE_LOADER_ID = 1;

    private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        earthquakeListView = (ListView)findViewById(R.id.list);

        mAdapter = new EarthquakeAdapter(this,new ArrayList<Earthquake>());

        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Earthquake currentEarthquake = mAdapter.getItem(position);

                Uri earthquakeUri = Uri.parse(currentEarthquake.getmURL());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                startActivity(websiteIntent);

            }
        });

        mEmptyStateTextView = (TextView)findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        EarthquakeAsynTask task = new EarthquakeAsynTask();
        task.execute(USGS_REQUEST_URL);



        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID,null,this);
        }else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        return new EarthquakeLoader(this,USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_earthquake);

        mAdapter.clear();

        if (data != null && !data.isEmpty()){
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mAdapter.clear();
    }

    private class EarthquakeAsynTask extends AsyncTask<String,Void,List<Earthquake>>{

        @Override
        protected List<Earthquake> doInBackground(String... params) {

            if (params.length < 1 || params[0] == null){
                return null;
            }

            List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(params[0]);

            return earthquakes;

        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {

            mAdapter.clear();

            if (earthquakes != null && !earthquakes.isEmpty()){
                mAdapter.addAll(earthquakes);
            }

        }
    }


}
