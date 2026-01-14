package com.wycherley.trackmybus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wycherley.trackmybus.R;
import com.wycherley.trackmybus.models.TutorialItem;
import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {

    private List<TutorialItem> tutorialItems;

    public TutorialAdapter(List<TutorialItem> tutorialItems) {
        this.tutorialItems = tutorialItems;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutorial_slide, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        TutorialItem item = tutorialItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return tutorialItems.size();
    }

    static class TutorialViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTutorial;
        TextView tvTitle, tvDescription;

        TutorialViewHolder(View itemView) {
            super(itemView);
            ivTutorial = itemView.findViewById(R.id.ivTutorial);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        void bind(TutorialItem item) {
            ivTutorial.setImageResource(item.getImageResource());
            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
        }
    }
}