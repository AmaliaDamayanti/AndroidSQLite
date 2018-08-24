package com.ad.mynotesapp;

import android.view.View;

/**
 * bertugas membuat item seperti CardView bisa diklik di dalam adapter
 * Caranya lakukan penyesuaian pada kelas event OnClickListene
 * Alhasil kita bisa mengimplementasikan interface listener yang baru bernama OnItemClickCallback
 * Kelas tersebut dibuat untuk menghindari nilai final dari position yang tentunya sangat tidak direkomendasikan.
 */
public class CustomOnClickListener implements View.OnClickListener {

    private int position;
    private OnItemClickCallback onItemClickCallback;

    public CustomOnClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }
    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view, position);
    }
    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }
}
