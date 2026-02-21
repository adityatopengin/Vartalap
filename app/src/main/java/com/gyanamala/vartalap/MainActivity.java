package com.gyanamala.vartalap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerIslands;
    private ExtendedFloatingActionButton fabCreateIsland;
    private DatabaseReference islandsRef;
    private SharedPreferences prefs;
    private String myUsername;

    // These will be wired in Batch 4
    // private IslandAdapter adapter;
    private List<Island> islandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Verify Identity
        prefs = getSharedPreferences("VartalapPrefs", MODE_PRIVATE);
        myUsername = prefs.getString("my_username", "Unknown");

        // 2. Initialize Firebase
        islandsRef = FirebaseDatabase.getInstance().getReference("Islands");

        // 3. Setup UI Components
        recyclerIslands = findViewById(R.id.recycler_islands);
        fabCreateIsland = findViewById(R.id.fab_create_island);
        islandList = new ArrayList<>();

        setupBentoGrid();
        loadIslandsFromFirebase();

        // 4. FAB Click Listener for creating new Islands
        fabCreateIsland.setOnClickListener(v -> showCreateIslandDialog());
        
        // Hide FAB on scroll down for a cleaner look
        recyclerIslands.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fabCreateIsland.isExtended()) {
                    fabCreateIsland.shrink();
                } else if (dy < 0 && !fabCreateIsland.isExtended()) {
                    fabCreateIsland.extend();
                }
            }
        });
    }

    private void setupBentoGrid() {
        // Staggered Grid with 2 columns gives the "Bento" look
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerIslands.setLayoutManager(layoutManager);

        // Apply a smooth cascading animation
        int resId = android.R.anim.fade_in; // Fallback built-in animation
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerIslands.setLayoutAnimation(animation);

        // Adapter will go here in Batch 4
        // adapter = new IslandAdapter(this, islandList, myUsername);
        // recyclerIslands.setAdapter(adapter);
    }

    private void loadIslandsFromFirebase() {
        islandsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                islandList.clear();
                
                // Manually inject the GLOBAL island at the very top
                islandList.add(new Island("global_chat", "GLOBAL", "GLOBAL", null, System.currentTimeMillis()));

                // Fetch user-created islands
                for (DataSnapshot data : snapshot.getChildren()) {
                    Island island = data.getValue(Island.class);
                    if (island != null) {
                        islandList.add(island);
                    }
                }

                // Trigger the adapter and animation
                // if (adapter != null) {
                //     adapter.notifyDataSetChanged();
                //     recyclerIslands.scheduleLayoutAnimation(); 
                // }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load islands.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateIslandDialog() {
        // A sophisticated Material 3 Dialog built entirely in Java
        View dialogView = getLayoutInflater().inflate(R.layout.activity_setup, null); // Placeholder for custom view
        
        new MaterialAlertDialogBuilder(this)
                .setTitle("Discover a New Island")
                .setMessage("Name your public group. (Private PIN feature coming in updates!)")
                .setPositiveButton("Create", (dialog, which) -> {
                    // Logic to push new Island to Firebase will go here
                    String islandId = islandsRef.push().getKey();
                    Island newIsland = new Island(islandId, "New Island", "PUBLIC", null, System.currentTimeMillis());
                    islandsRef.child(islandId).setValue(newIsland);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

