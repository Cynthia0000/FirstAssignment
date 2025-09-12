package com.example.firstassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private ImageView avatarImageView;
    private int selectedAvatarResource = R.drawable.avatar1; // 默认头像
    private Button registerButton;
    private Button backButton;
    private CustomTitleBar customTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.registerUsernameEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText);
        avatarImageView = findViewById(R.id.registerAvatarImageView);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        customTitleBar = findViewById(R.id.customTitleBar);

        // 设置自定义标题栏的标题
        customTitleBar.setTitle("用户注册");

        // 设置头像点击事件，切换头像
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 简单切换头像示例
                if (selectedAvatarResource == R.drawable.avatar1) {
                    selectedAvatarResource = R.drawable.avatar2;
                } else if (selectedAvatarResource == R.drawable.avatar2) {
                    selectedAvatarResource = R.drawable.avatar3;
                } else {
                    selectedAvatarResource = R.drawable.avatar1;
                }
                avatarImageView.setImageResource(selectedAvatarResource);
            }
        });

        // 注册按钮点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                // 简单验证
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 注册成功提示
                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();

                // 注册成功后返回登录界面，并传递用户名和头像信息
                Intent intent = new Intent();
                intent.putExtra("username", username);
                intent.putExtra("avatarResource", selectedAvatarResource);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // 返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}