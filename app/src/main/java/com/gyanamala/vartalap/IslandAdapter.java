package com.gyanamala.vartalap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import java.util.List;

public class IslandAdapter extends RecyclerView.Adapter<IslandAdapter.IslandViewHolder> {

    private final Context context;
    private final List<Island> islandList;
    private final String myUsername;

    public IslandAdapter(Context context, List<Island> islandList, String myUsername) {
        this.context = context;
        this.islandList = islandList;
        this.myUsername = myUsername;
    }

    @NonNull
    @Override
    public IslandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_island, parent, false);
        return new IslandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IslandViewHolder holder, int position) {
        Island island = islandList.get(position);

        // 1. Set the Text
        holder.textIslandName.setText(island.getName());

        // 2. The Bento Grid Logic (Make GLOBAL take the full row)
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams staggerParams = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            if (island.getType().equals("GLOBAL")) {
                staggerParams.setFullSpan(true); // Stretches across all columns
                holder.textIslandStatus.setText("The Main Hub");
            } else {
                staggerParams.setFullSpan(false); // Fits into one column
                holder.textIslandStatus.setText("Public Group");
            }
        }

        // 3. Dynamic Colors & Icons based on Island Type
        if (island.getType().equals("GLOBAL")) {
            holder.islandBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.island_global));
            holder.iconIsland.setImageResource(android.R.drawable.ic_dialog_info);
        } else if (island.getType().equals("LOCKED")) {
            holder.islandBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.island_locked));
            holder.iconIsland.setImageResource(android.R.drawable.ic_secure);
            holder.textIslandStatus.setText("PIN Required");
        } else {
            // Default Public Group
            holder.islandBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.island_user));
            holder.iconIsland.setImageResource(android.R.drawable.ic_menu_myplaces);
        }

        // 4. Handle Clicks (Navigate to ChatActivity)
        holder.itemView.setOnClickListener(v -> {
            if (island.getType().equals("LOCKED")) {
                // Future update: Trigger PIN Dialog here
            } else {
                // Go straight to the chat room
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("ISLAND_ID", island.getIslandId());
                intent.putExtra("ISLAND_NAME", island.getName());
                intent.putExtra("MY_USERNAME", myUsername);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return islandList.size();
    }

    // ViewHolder class to map the XML IDs
    public static class IslandViewHolder extends RecyclerView.ViewHolder {
        View islandBackground;
        ImageView iconIsland;
        TextView textIslandName, textIslandStatus;

        public IslandViewHolder(@NonNull View itemView) {
            super(itemView);
            islandBackground = itemView.findViewById(R.id.island_background);
            iconIsland = itemView.findViewById(R.id.icon_island);
            textIslandName = itemView.findViewById(R.id.text_island_name);
            textIslandStatus = itemView.findViewById(R.id.text_island_status);
        }
    }
}

