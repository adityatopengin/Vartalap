package com.gyanamala.vartalap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private final Context context;
    private final List<Message> messageList;
    private final String myUsername;

    public MessageAdapter(Context context, List<Message> messageList, String myUsername) {
        this.context = context;
        this.messageList = messageList;
        this.myUsername = myUsername;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSender().equals(myUsername)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageHolder) holder).textBody.setText(message.getText());
        } else {
            ((ReceivedMessageHolder) holder).textSender.setText(message.getSender());
            ((ReceivedMessageHolder) holder).textBody.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView textBody;
        SentMessageHolder(View itemView) {
            super(itemView);
            textBody = itemView.findViewById(R.id.text_message_body);
        }
    }

    static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView textSender, textBody;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            textSender = itemView.findViewById(R.id.text_message_sender);
            textBody = itemView.findViewById(R.id.text_message_body);
        }
    }
}

