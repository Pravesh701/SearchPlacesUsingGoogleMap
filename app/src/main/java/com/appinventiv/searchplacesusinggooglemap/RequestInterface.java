package com.appinventiv.searchplacesusinggooglemap;

import com.appinventiv.searchplacesusinggooglemap.NearbySearch.NearbySearch;
import com.appinventiv.searchplacesusinggooglemap.SearchModel.SearchModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestInterface {

    @GET("maps/api/place/textsearch/json")
    Call<SearchModel> getSearchResult(@Query("query") String search,@Query("key") String key);

    @GET("maps/api/place/nearbysearch/json")
    Call<NearbySearch> getNearByPlaces(@Query("location") String location,
                                       @Query("radius") int radius,
                                       @Query("type") String type,
                                       @Query("key") String key);

}
