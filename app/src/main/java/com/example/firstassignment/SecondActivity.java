package com.example.firstassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private TextView userInfoTextView;
    private ImageView userAvatarImageView;
    private Button backButton;
    private Button changeAvatarButton;
    private Button startLearningButton;
    private Button chatButton;
    private CustomTitleBar customTitleBar;
    private String username;
    private int currentAvatarResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        userInfoTextView = findViewById(R.id.userInfoTextView);
        userAvatarImageView = findViewById(R.id.userAvatarImageView);
        backButton = findViewById(R.id.backButton);
        changeAvatarButton = findViewById(R.id.changeAvatarButton);
        startLearningButton = findViewById(R.id.startLearningButton);
        chatButton = findViewById(R.id.chatButton);
        customTitleBar = findViewById(R.id.customTitleBar);

        // 获取从MainActivity传递过来的用户名和头像信息
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentAvatarResource = intent.getIntExtra("avatarResource", R.drawable.avatar1);

        // 设置欢迎信息和头像
        welcomeTextView.setText("欢迎，" + username + "！");
        userAvatarImageView.setImageResource(currentAvatarResource);
        userInfoTextView.setText("欢迎来到英语单词学习应用！点击下方按钮开始今天的单词学习之旅。");

        // 设置自定义标题栏的标题
        customTitleBar.setTitle("用户主页");

        // 返回登录界面按钮点击事件（退出登录）
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出登录，清除当前活动栈并返回登录页面
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // 结束当前活动
            }
        });

        // 开始学习按钮点击事件
        startLearningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, WordListActivity.class);
                startActivity(intent);
            }
        });

        // 更换头像按钮点击事件
        changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换头像示例
                if (currentAvatarResource == R.drawable.avatar1) {
                    currentAvatarResource = R.drawable.avatar2;
                } else if (currentAvatarResource == R.drawable.avatar2) {
                    currentAvatarResource = R.drawable.avatar3;
                } else {
                    currentAvatarResource = R.drawable.avatar1;
                }
                userAvatarImageView.setImageResource(currentAvatarResource);
                Toast.makeText(SecondActivity.this, "头像已更换", Toast.LENGTH_SHORT).show();
            }
        });

        // 点击头像也可以切换头像
        userAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换头像示例
                if (currentAvatarResource == R.drawable.avatar1) {
                    currentAvatarResource = R.drawable.avatar2;
                } else if (currentAvatarResource == R.drawable.avatar2) {
                    currentAvatarResource = R.drawable.avatar3;
                } else {
                    currentAvatarResource = R.drawable.avatar1;
                }
                userAvatarImageView.setImageResource(currentAvatarResource);
                Toast.makeText(SecondActivity.this, "头像已更换", Toast.LENGTH_SHORT).show();
            }
        });

        // AI聊天按钮点击事件
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示点击反馈
                Toast.makeText(SecondActivity.this, "正在打开AI聊天界面...", Toast.LENGTH_SHORT).show();

                // 创建Intent并启动ChatActivity
                Intent intent = new Intent(SecondActivity.this, ChatActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("avatarResource", currentAvatarResource);
                startActivity(intent);
            }
        });
    }
}