package com.veronikalebedyuk.dialogforbetter.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.veronikalebedyuk.dialogforbetter.MainActivity;
import com.veronikalebedyuk.dialogforbetter.R;
import com.veronikalebedyuk.dialogforbetter.classes.Message;

import java.lang.reflect.Array;
import java.util.List;

public class HintAdapter extends RecyclerView.Adapter {
    private String[] hints;
    private Context context;
    private RecyclerVIewClickInterface recyclerVIewClickInterface;


    public HintAdapter(Context context,  String[] hints, RecyclerVIewClickInterface recyclerVIewClickInterface) {
        this.hints = hints;
        this.context = context;
        this.recyclerVIewClickInterface =recyclerVIewClickInterface;
    }

    private class HintHolder extends RecyclerView.ViewHolder {
        Button btn;

        HintHolder(View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.btnHint);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerVIewClickInterface.onItemClick(getAdapterPosition());
                }
            });
        }

        void bind(String str) {
            btn.setText(str);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.keyboard_hint, parent, false);
        return new HintAdapter.HintHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((HintAdapter.HintHolder) holder).bind(hints[position]);
    }

    @Override
    public int getItemCount() {
         return hints.length;
    }
}
