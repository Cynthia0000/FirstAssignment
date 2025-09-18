package com.example.firstassignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 聊天消息适配器，用于RecyclerView显示聊天消息
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Message> messageList;
    private int currentAvatarResource;
    private SimpleDateFormat timeFormat;

    /**
     * 构造函数
     * @param context 上下文
     * @param avatarResource 用户头像资源ID
     */
    public ChatAdapter(Context context, int avatarResource) {
        this.context = context;
        this.messageList = new ArrayList<>();
        this.currentAvatarResource = avatarResource;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    /**
     * 添加消息到列表
     * @param message 消息对象
     */
    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    /**
     * 设置用户头像
     * @param avatarResource 头像资源ID
     */
    public void setAvatarResource(int avatarResource) {
        this.currentAvatarResource = avatarResource;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);
        String timeString = timeFormat.format(new Date(message.getTimestamp()));

        if (message.getSenderType() == Message.SENDER_USER) {
            // 显示用户消息
            holder.userMessageLayout.setVisibility(View.VISIBLE);
            holder.aiMessageLayout.setVisibility(View.GONE);

            holder.userMessageText.setText(message.getContent());
            holder.userMessageTime.setText(timeString);
            holder.userAvatar.setImageResource(currentAvatarResource);
        } else {
            // 显示AI消息
            holder.userMessageLayout.setVisibility(View.GONE);
            holder.aiMessageLayout.setVisibility(View.VISIBLE);

            holder.aiMessageText.setText(message.getContent());
            holder.aiMessageTime.setText(timeString);
            holder.aiAvatar.setImageResource(R.drawable.ai_avatar);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /**
     * 聊天ViewHolder，用于缓存视图组件
     */
    static class ChatViewHolder extends RecyclerView.ViewHolder {

        // 用户消息相关组件
        LinearLayout userMessageLayout;
        TextView userMessageText;
        TextView userMessageTime;
        ImageView userAvatar;

        // AI消息相关组件
        LinearLayout aiMessageLayout;
        TextView aiMessageText;
        TextView aiMessageTime;
        ImageView aiAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            // 初始化用户消息组件
            userMessageLayout = itemView.findViewById(R.id.userMessageLayout);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            userMessageTime = itemView.findViewById(R.id.userMessageTime);
            userAvatar = itemView.findViewById(R.id.userAvatar);

            // 初始化AI消息组件
            aiMessageLayout = itemView.findViewById(R.id.aiMessageLayout);
            aiMessageText = itemView.findViewById(R.id.aiMessageText);
            aiMessageTime = itemView.findViewById(R.id.aiMessageTime);
            aiAvatar = itemView.findViewById(R.id.aiAvatar);
        }
    }
}