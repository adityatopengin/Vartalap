package com.gyanamala.vartalap;

import android.os.Bundle;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText editMessage;
    private FloatingActionButton btnSend;
    private MaterialToolbar toolbarChat;

    private MessageAdapter adapter;
    private List<Message> messageList;
    private DatabaseReference messagesRef;

    private String islandId;
    private String islandName;
    private String myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        islandId = getIntent().getStringExtra("ISLAND_ID");
        islandName = getIntent().getStringExtra("ISLAND_NAME");
        myUsername = getIntent().getStringExtra("MY_USERNAME");

        messagesRef = FirebaseDatabase.getInstance().getReference("Islands")
                .child(islandId).child("messages");

        toolbarChat = findViewById(R.id.toolbar_chat);
        recyclerChat = findViewById(R.id.recycler_chat);
        editMessage = findViewById(R.id.edit_message);
        btnSend = findViewById(R.id.btn_send);

        toolbarChat.setTitle(islandName);
        toolbarChat.setNavigationIcon(android.R.drawable.ic_media_previous);
        toolbarChat.setNavigationOnClickListener(v -> finish());

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(this, messageList, myUsername);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); 
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());

        loadMessages();
    }

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            Message newMessage = new Message(text, myUsername, System.currentTimeMillis());
            messagesRef.push().setValue(newMessage);
            editMessage.setText(""); 
        }
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);
                    if (msg != null) {
                        messageList.add(msg);
                    }
                }
                adapter.notifyDataSetChanged();
                
                if (!messageList.isEmpty()) {
                    recyclerChat.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
