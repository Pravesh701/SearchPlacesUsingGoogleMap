package com.appinventiv.searchplacesusinggooglemap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.appinventiv.searchplacesusinggooglemap.NearbySearch.Photo;
import com.appinventiv.searchplacesusinggooglemap.NearbySearch.Result;
import java.util.List;

public class NearbySearchAdapter extends RecyclerView.Adapter<NearbySearchAdapter.ViewHolderNearBySearch> {

    private Context context;
    private List<Result> listNearByPlace;
    private String photoUrl;
    NearbySearchAdapter(Context context, List<Result> arrayList) {
        this.context = context;
        this.listNearByPlace = arrayList;
    }
    @NonNull
    @Override
    public ViewHolderNearBySearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_nearby_search_places, parent, false);
        return new ViewHolderNearBySearch(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolderNearBySearch holder, int position) {

        Result result = listNearByPlace.get(position);
        final String name = result.getName();
        List<String> type = result.getTypes();
        String typ = "";
        final String address = result.getVicinity();
        List<Photo> listPhoto = result.getPhotos();
        Photo photo;
        if (listPhoto != null){
            photo = listPhoto.get(0);
            photoUrl = photo.getPhotoReference();
        }
        for (String t : type){
            typ = typ.concat(t+" ");
        }
        if (name != null){
            holder.placeName.setText(name);
        }
        else {
            holder.placeName.setText(context.getResources().getString(R.string.tv_place_name));
        }
        if (!typ.equals("")){
            typ = context.getResources().getString(R.string.tv_place_type).concat("\n" + typ);
            holder.placeTypes.setText(typ);
        }
        else {
            holder.placeTypes.setText(context.getResources().getString(R.string.tv_place_type));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra("NAME", name);
                intent.putExtra("Address", address);
                intent.putExtra("PhotoURL", photoUrl);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return listNearByPlace.size();
    }

    static class ViewHolderNearBySearch extends RecyclerView.ViewHolder {
        private TextView placeName, placeTypes;
        ViewHolderNearBySearch(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.tv_nearby_search_place_name);
            placeTypes = itemView.findViewById(R.id.tv_nearby_search_place_type);
        }
    }

}
