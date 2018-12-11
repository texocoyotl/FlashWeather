package com.nahualsoft.flashweather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class LocationSelectionFragment extends Fragment implements OnGeocoderResponseListener{

    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private ImageButton locationButton;

    public LocationSelectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_selection, container, false);
        locationButton = view.findViewById(R.id.getLocationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Activity activity = getActivity();
                if (activity != null && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermission();
                } else {
                    getLocation();
                }

            }
        });
        return view;

    }

    private void getLocation() {
        Toast.makeText(getContext(), "Getting location", Toast.LENGTH_SHORT).show();
        final OnGeocoderResponseListener listener = this;

        if (getContext() != null) {

            try {
                LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null) {
                    List<String> providers = locationManager.getProviders(true);
                    Location location = null;

                    for (int i = providers.size() - 1; i >= 0; i--) {
                        location = locationManager.getLastKnownLocation(providers.get(i));
                        if (location != null) {
                            getCityFromLocation(getContext(), location, listener);
                            break;
                        }
                    }
                }

            } catch (SecurityException e) {
                //No-op
            }
        }


//        LocationManager locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
//        final OnGeocoderResponseListener listener = this;
//        final Context context = getContext();
//
//        try {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    Log.d( "GEO", "New location: " + location.toString());
//                    getCityFromLocation(context, location, listener);
//                }
//
//                @Override
//                public void onStatusChanged(String s, int i, Bundle bundle) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String s) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String s) {
//
//                }
//            });
//        }
//        catch (SecurityException e){
//            // No-op
//        }
    }

    private static void getCityFromLocation(final @NonNull Context context, final @NonNull Location location, final OnGeocoderResponseListener onGeocoderResponseListener) {
        if (onGeocoderResponseListener == null) return;

        new AsyncTask<Void, Integer, List<Address>>(){
            @Override
            protected void onPostExecute(List<Address> results) {
                if (results != null){
                    onGeocoderResponseListener.onGeocoderResponse(location, results);
                }
            }

            @Override
            protected List<Address> doInBackground(Void... voids) {
                Geocoder coder = new Geocoder(context, Locale.ENGLISH);
                List<Address> results = null;
                try{
                    results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                }
                catch (IOException e){
                    //No-op
                }
                return results;
            }
        }.execute();
    }

    private void requestLocationPermission() {
        this.requestPermissions(
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSIONS_REQUEST_COARSE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
            }
        }
    }

    @Override
    public void onGeocoderResponse(@NonNull Location location, @NonNull List<Address> results) {
        Log.d("GEO", results.get(0).getLocality());
    }
}
