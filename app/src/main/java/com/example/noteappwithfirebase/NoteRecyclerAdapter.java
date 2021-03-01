package com.example.noteappwithfirebase;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteRecyclerAdapter extends FirestoreRecyclerAdapter<Note,NoteRecyclerAdapter.NoteViewHolder> {

    public NoteRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull Note note) {
        //to set note in recyclerView
        noteViewHolder.noteTextView.setText(note.getText());
        noteViewHolder.checkBox.setChecked(note.getCompleted());

        //set note added date in recyclerView
        CharSequence dateCharSeq = DateFormat.format("EEEE, MMM d, yyyy h:mm:ss a",note.getCreated().toDate());
        noteViewHolder.dateTextView.setText(dateCharSeq);

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note_row,parent,false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTextView,dateTextView;
        CheckBox checkBox;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTextView = itemView.findViewById(R.id.noteTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            checkBox = itemView.findViewById(R.id.checkBox);


        }
        public void deleteItem() {

        }
    }
}
