package com.example.webscraping;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.ViewHolder> {
    private ArrayList<PhoneModel> phoneModels;
    private Context context;

    public PhoneAdapter(ArrayList<PhoneModel> phoneModels, Context context) {
        this.phoneModels = phoneModels;
        this.context = context;
    }

    @NonNull
    @Override
    public PhoneAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneAdapter.ViewHolder holder, int position) {
        PhoneModel phoneModel = phoneModels.get(position);
        holder.txtView.setText(phoneModel.getName());
        Picasso.get().load(phoneModel.getImageUrl()).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return phoneModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView;
        TextView txtView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.phoneImg);
            txtView = itemView.findViewById(R.id.phoneName);
        }
    }
}
