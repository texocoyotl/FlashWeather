package com.nahualsoft.flashweather;

import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;

import java.util.List;

public interface OnGeocoderResponseListener {
    public abstract void onGeocoderResponse(@NonNull Location location, @NonNull List<Address> results);
}
