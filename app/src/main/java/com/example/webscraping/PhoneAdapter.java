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
        holder.txtViewName.setText(phoneModel.getName());
        holder.txtViewSize.setText(phoneModel.getScreenSize());
        holder.txtViewResolution.setText(phoneModel.getResolution());
        holder.txtViewWidth.setText(phoneModel.getPhoneWidth());
        holder.txtViewType.setText(phoneModel.getScreenType());
        Picasso.get().load(phoneModel.getImageUrl()).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return phoneModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView;
        TextView txtViewName;
        TextView txtViewSize;
        TextView txtViewResolution;
        TextView txtViewWidth;
        TextView txtViewType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.phoneImg);
            txtViewName = itemView.findViewById(R.id.phoneName);
            txtViewSize = itemView.findViewById(R.id.screenSize);
            txtViewResolution = itemView.findViewById(R.id.screenResolution);
            txtViewWidth = itemView.findViewById(R.id.phoneWidth);
            txtViewType = itemView.findViewById(R.id.phoneType);
        }
    }
}
