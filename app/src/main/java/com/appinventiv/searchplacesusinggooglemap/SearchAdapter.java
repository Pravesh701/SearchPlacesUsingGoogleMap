package com.appinventiv.searchplacesusinggooglemap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appinventiv.searchplacesusinggooglemap.SearchModel.Geometry;
import com.appinventiv.searchplacesusinggooglemap.SearchModel.Location;
import com.appinventiv.searchplacesusinggooglemap.SearchModel.Result;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolderSearchAdapter> implements Filterable {

    private Context context;
    private List<Result> listSearchPlaces;
    private List<Result> mFilteredList;
    private double latitude;
    private double longitude;

    public SearchAdapter(Context context, List<Result> arrayList) {
        this.context = context;
        this.listSearchPlaces = arrayList;
        this.mFilteredList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolderSearchAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_search_places, parent, false);
        return new ViewHolderSearchAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSearchAdapter holder, int position) {
        Result candidate = listSearchPlaces.get(position);
        String name = candidate.getName();
        String address = candidate.getFormattedAddress();
        Geometry geometry = candidate.getGeometry();
        Location location = geometry.getLocation();
        latitude = location.getLat();
        longitude = location.getLng();
        if (name != null){
            holder.placeName.setText(name);
        }
        else {
           holder.placeName.setText("Place Name");
        }
        if (address != null){
            holder.placeAddress.setText(address);
        }
        else {
            holder.placeAddress.setText("Place address");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("val","value");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = listSearchPlaces;
                } else {
                    List<Result> filteredList = new ArrayList<>(listSearchPlaces);
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (List<Result>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolderSearchAdapter extends RecyclerView.ViewHolder {
        private TextView placeName, placeAddress;
        ViewHolderSearchAdapter(@NonNull View itemView) {
            super(itemView);
            placeName = (TextView) itemView.findViewById(R.id.tv_search_place_name);
            placeAddress = (TextView) itemView.findViewById(R.id.tv_search_place_address);
        }
    }

}
