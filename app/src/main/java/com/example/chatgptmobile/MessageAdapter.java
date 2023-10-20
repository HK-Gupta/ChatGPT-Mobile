package com.example.chatgptmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<Message> messageList;
    public  MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view, null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_USER)) {
            holder.bot_chat_layout.setVisibility(View.GONE);
            holder.sender_chat_layout.setVisibility(View.VISIBLE);
            holder.sender_text_message.setText(message.getMessage());
        } else {
            holder.bot_chat_layout.setVisibility(View.VISIBLE);
            holder.sender_chat_layout.setVisibility(View.GONE);
            holder.bot_text_message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout sender_chat_layout, bot_chat_layout;
        TextView sender_text_message, bot_text_message;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sender_chat_layout = itemView.findViewById(R.id.sender_chat_layout);
            bot_chat_layout = itemView.findViewById(R.id.bot_chat_layout);
            sender_text_message = itemView.findViewById(R.id.sender_text_message);
            bot_text_message = itemView.findViewById(R.id.bot_text_message);

        }
    }
}
