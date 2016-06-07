package com.ciandt.thegarage.navapp.data.remote;

import com.ciandt.thegarage.navapp.model.api.BeaconApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by thales on 5/2/16.
 */
public interface BeaconsApi {

//    @GET("findByMacAddress/macAddress={mac}")
//    Call<BeaconApi> getBeacon(@Path("mac") String macAddress);

    @GET("listAll")
    Call<BeaconApi> listAllBeacons();
}
