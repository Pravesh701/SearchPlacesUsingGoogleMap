package com.appinventiv.searchplacesusinggooglemap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.appinventiv.searchplacesusinggooglemap.SearchModel.Result;
import com.appinventiv.searchplacesusinggooglemap.SearchModel.SearchModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SearchActivity";
    private final String BASE_URL = "https://maps.googleapis.com/";
    private static final String API_KEY = "AIzaSyA024ETshHqlwMtT42pKM_DecNeaSSgyNo";
    private SearchView svPlaceSearch;
    private ImageButton imgBtnPlaceSearch;
    private RecyclerView rvShowSearchPlaces;
    private SearchAdapter searchAdapter;
    private List<Result> listSearchPlaces;
    private String defaultSearchData = "";
    private Retrofit retrofitSearchPlace;
    private RequestInterface request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        svPlaceSearch = (SearchView) findViewById(R.id.sv_search_place);
        imgBtnPlaceSearch = (ImageButton) findViewById(R.id.img_btn_search_place);
        rvShowSearchPlaces = (RecyclerView) findViewById(R.id.rv_show_search_places);
        rvShowSearchPlaces.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
        listSearchPlaces = new ArrayList<>();
        searchAdapter = new SearchAdapter(SearchActivity.this, listSearchPlaces);
        rvShowSearchPlaces.setAdapter(searchAdapter);
        retrofitSearchPlace = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        request = retrofitSearchPlace.create(RequestInterface.class);
        listSearchPlaces = new ArrayList<>();
        searchAdapter = new SearchAdapter(this, listSearchPlaces);
        rvShowSearchPlaces.setAdapter(searchAdapter);
        search(svPlaceSearch);
        imgBtnPlaceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSearchData(defaultSearchData);
            }
        });
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadSearchData(query);
                Log.d(TAG, "Text Is  " + query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                defaultSearchData = newText;
                return true;
            }
        });
    }

    private void loadSearchData(String searchData){
        Call<SearchModel> call = request.getSearchResult(searchData,API_KEY);
        Log.d(TAG, "loadSearchData: ");
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                Log.d(TAG, "Call is " + call.toString());
                Log.d(TAG, "Response is " + response.toString());
                if (response.isSuccessful()){
                    SearchModel searchModel = response.body();
                    listSearchPlaces = searchModel.getResults();
                    searchAdapter = new SearchAdapter(SearchActivity.this, listSearchPlaces);
                    rvShowSearchPlaces.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                }
                else {
                    Log.d(TAG, "Not Responding  "+response.toString());
                }
            }
            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

}
