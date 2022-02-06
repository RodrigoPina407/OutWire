package com.outwire.objects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.outwire.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyEventsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<Event> events;

    public MyEventsRecyclerAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.small_event_cardview, parent, false);
        return new ViewHolderSmall(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        TextView title = holder.itemView.findViewById(R.id.small_card_title);
        TextView participants = holder.itemView.findViewById(R.id.small_card_num_participants);
        TextView date = holder.itemView.findViewById(R.id.small_card_event_date);

        title.setText(events.get(position).getEventName());
        //participants.setText(events.get(position).getNumParticipants());

        String fDate = SimpleDateFormat.getDateInstance().format(events.get(position).getDate());

        date.setText(fDate);

    }

    @Override
    public int getItemCount() {

        return events.size();
    }


    protected static class ViewHolderSmall extends RecyclerView.ViewHolder{


        public ViewHolderSmall(@NonNull View itemView) {
            super(itemView);
        }
    }

}
