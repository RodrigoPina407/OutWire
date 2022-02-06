package com.outwire.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.outwire.R;
import com.outwire.objects.Event;
import com.outwire.objects.MyEventsRecyclerAdapter;
import com.outwire.util.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class RecyclerFragment extends Fragment {

    private final List<Event> events = new ArrayList<>();
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private ListenerRegistration listenerRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getEvents();

        View v = inflater.inflate(R.layout.recycler_fragment, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recycler_fragment_view);

        adapter = new MyEventsRecyclerAdapter(events);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }



    private void getEvents(){

        EventListener<QuerySnapshot> snapshotEventListener = (value, error) -> {

            if (error != null) {
                Log.w("Firestore error:", "Listen failed.", error);
                return;
            }

            for (DocumentChange dc : value.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        events.add(dc.getDocument().toObject(Event.class));
                        adapter.notifyItemInserted(adapter.getItemCount());
                        break;
                    case MODIFIED:
                        events.set(dc.getOldIndex(), dc.getDocument().toObject(Event.class));
                        adapter.notifyItemChanged(dc.getOldIndex());
                        break;
                    case REMOVED:
                        events.remove(dc.getOldIndex());
                        adapter.notifyItemRemoved(dc.getOldIndex());
                        break;
                }
            }
        };

        listenerRegistration = FirebaseHelper.getUserEventsCollection().addSnapshotListener(snapshotEventListener);

    }

}
