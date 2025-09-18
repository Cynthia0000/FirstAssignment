package com.example.firstassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView messageRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private Button sendButton;
    private TextView chatTitle;
    private String username;
    private static final String TAG = "ChatActivity";
    private Handler handler;
    private DataManager dataManager;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_chat);
            Log.d(TAG, "Layout加载成功");

            // 初始化Handler，用于处理异步消息
            handler = new Handler();

            // 初始化UI组件
            messageRecyclerView = findViewById(R.id.messageRecyclerView);
            messageInput = findViewById(R.id.messageInput);
            sendButton = findViewById(R.id.sendButton);
            chatTitle = findViewById(R.id.chatTitle);

            // 显示一个简单的Toast提示，表明ChatActivity成功启动
            Toast.makeText(this, "ChatActivity启动成功!", Toast.LENGTH_SHORT).show();

            // 获取从用户主页传递过来的信息
            Intent intent = getIntent();
            if (intent != null) {
                username = intent.getStringExtra("username");
                userId = intent.getIntExtra("userId", -1);
                Log.d(TAG, "Received data: username=" + username + ", userId=" + userId);
            }

            // 初始化DataManager
            dataManager = new DataManager(this);

            // 初始化RecyclerView
            initRecyclerView();

            // 从数据库加载历史消息
            loadMessagesFromDatabase();

            // 如果没有历史消息，显示欢迎消息
            if (chatAdapter.getItemCount() == 0) {
                showWelcomeMessage();
            }

            // 设置发送按钮点击事件
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });

            // 设置输入框的键盘事件，按回车键发送消息
            messageInput.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                        sendMessage();
                        return true;
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "加载聊天界面失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 初始化RecyclerView，设置布局管理器和适配器
     */
    private void initRecyclerView() {
        // 创建LinearLayoutManager，设置为垂直方向
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        messageRecyclerView.setLayoutManager(layoutManager);

        // 创建聊天适配器，设置用户头像
        chatAdapter = new ChatAdapter(this, R.drawable.avatar1);
        messageRecyclerView.setAdapter(chatAdapter);

        // 自动滚动到底部
        messageRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    /**
     * 从数据库加载历史消息
     */
    private void loadMessagesFromDatabase() {
        if (dataManager != null && userId != -1) {
            List<Message> messages = dataManager.getAllMessages(userId);
            if (messages != null && !messages.isEmpty()) {
                for (Message message : messages) {
                    chatAdapter.addMessage(message);
                }
                // 自动滚动到底部
                messageRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        }
    }

    /**
     * 显示欢迎消息
     */
    private void showWelcomeMessage() {
        String welcomeMessage = "Hello! I'm your AI assistant. Nice to chat with you. How can I help you today?";
        Message message = new Message(welcomeMessage, Message.SENDER_AI);
        chatAdapter.addMessage(message);

        // 保存到数据库
        if (dataManager != null && userId != -1) {
            dataManager.addMessage(message.getContent(), message.getSenderType(), userId);
        }

        // 自动滚动到底部
        messageRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (!content.isEmpty()) {
            // 添加用户消息
            Message userMessage = new Message(content, Message.SENDER_USER);
            chatAdapter.addMessage(userMessage);

            // 保存到数据库
            if (dataManager != null && userId != -1) {
                dataManager.addMessage(userMessage.getContent(), userMessage.getSenderType(), userId);
            }

            // 清空输入框
            messageInput.setText("");

            // 自动滚动到底部
            messageRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

            // 模拟AI回复
            simulateAIResponse(content);
        }
    }

    /**
     * 模拟AI回复
     * @param userMessage 用户发送的消息
     */
    private void simulateAIResponse(final String userMessage) {
        // 使用Handler延迟发送AI回复，模拟网络延迟
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String aiResponse = generateAIResponse(userMessage);
                Message aiMessage = new Message(aiResponse, Message.SENDER_AI);
                chatAdapter.addMessage(aiMessage);

                // 保存到数据库
                if (dataManager != null && userId != -1) {
                    dataManager.addMessage(aiMessage.getContent(), aiMessage.getSenderType(), userId);
                }

                // 自动滚动到底部
                messageRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        }, 1000); // 延迟1秒发送回复
    }

    /**
     * 生成AI回复
     * @param userMessage 用户发送的消息
     * @return AI回复内容
     */
    private String generateAIResponse(String userMessage) {
        // 转换为小写以进行不区分大小写的匹配
        userMessage = userMessage.toLowerCase();

        // 这里是一个简单的回复生成器，实际应用中可以连接到AI API
        if (userMessage.contains("hello") || userMessage.contains("hi") || userMessage.contains("hey")) {
            return "Hello! I'm your AI assistant. Nice to chat with you.";
        } else if (userMessage.contains("goodbye") || userMessage.contains("bye") || userMessage.contains("see you")) {
            return "Goodbye! Feel free to chat with me anytime you need.";
        } else if (userMessage.contains("name") || userMessage.contains("call you")) {
            return "I'm an AI assistant. You can call me小智 (Xiao Zhi).";
        } else if (userMessage.contains("help") || userMessage.contains("can you") || userMessage.contains("how to")) {
            return "I can answer questions, provide information, or just chat with you. What would you like to talk about?";
        } else if (userMessage.contains("weather") || userMessage.contains("temperature")) {
            return "I don't have access to real-time weather data yet, but I can help with other topics!";
        } else if (userMessage.contains("thanks") || userMessage.contains("thank you")) {
            return "You're welcome! I'm here to help.";
        } else if (userMessage.contains("your purpose") || userMessage.contains("why are you here")) {
            return "My purpose is to assist and chat with you. I'm still learning and improving every day!";
        } else {
            return "Thanks for your message! This is a test response. In a real application, I would provide more targeted answers based on your questions.";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭数据库连接
        if (dataManager != null) {
            dataManager.close();
        }
    }
}