package com.outwire.objects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.outwire.R;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> x = new ArrayList<String>();

    public RecyclerAdapter(List<String> x) {

        this.x = x;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch(viewType){

            case 0:
                v = inflater.inflate(R.layout.event_cardview, parent, false);
                return new ViewHolderBig(v);
            case 1:
                v = inflater.inflate(R.layout.recycler_fragment, parent, false);
                RecyclerView recyclerView = v.findViewById(R.id.recycler_fragment_view);
                recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                        View v2 = inflater.inflate(R.layout.small_event_cardview, parent, false);
                        return new ViewHolderInner(v2);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

                    }

                    @Override
                    public int getItemCount() {
                        return x.size();
                    }

                    class ViewHolderInner extends RecyclerView.ViewHolder {
                        public ViewHolderInner(@NonNull View itemView) {
                            super(itemView);
                        }
                    }
                });
                recyclerView.setLayoutManager(new GridLayoutManager(v.getContext(), 2, GridLayoutManager.HORIZONTAL, false));
                return new ViewHolderSmall(v);

            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }

    }

    @Override
    public int getItemViewType(int position) {

        return position %2;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return x.size();
    }

    public static class ViewHolderBig extends RecyclerView.ViewHolder{


        public ViewHolderBig(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolderSmall extends RecyclerView.ViewHolder{


        public ViewHolderSmall(@NonNull View itemView) {
            super(itemView);
        }
    }
}
