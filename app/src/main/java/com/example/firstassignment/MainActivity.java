package com.example.firstassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REGISTER_REQUEST_CODE = 1;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ImageView avatarImageView;
    private int selectedAvatarResource = R.drawable.avatar1; // 默认使用头像1
    private Button loginButton;
    private Button registerButton;
    private CustomTitleBar customTitleBar;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        avatarImageView = findViewById(R.id.avatarImageView);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        customTitleBar = findViewById(R.id.customTitleBar);

        // 初始化DataManager
        dataManager = new DataManager(this);

        // 检查数据库是否为空，如果为空则创建测试用户
        checkAndCreateTestUser();

        // 设置自定义标题栏的标题
        customTitleBar.setTitle("用户登录");

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

        // 登录按钮点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 简单验证
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 显示登录进度条
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("登录中");
                progressDialog.setMessage("正在验证您的信息...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // 使用线程进行数据库验证
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 使用DataManager验证用户登录
                            final User user = dataManager.verifyUserLogin(username, password);

                            // 在主线程中更新UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();

                                    if (user != null) {
                                        // 登录成功，跳转到第二个活动，并传递用户信息
                                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                                        intent.putExtra("userId", user.getId());
                                        intent.putExtra("username", user.getUsername());
                                        intent.putExtra("avatarResource", user.getAvatarResource());
                                        startActivity(intent);
                                    } else {
                                        // 登录失败
                                        Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "登录失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        // 注册按钮点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REGISTER_REQUEST_CODE);
            }
        });
    }

    /**
     * 检查数据库是否为空，如果为空则创建测试用户
     */
    private void checkAndCreateTestUser() {
        // 使用dataManager检查数据库是否为空
        boolean isEmpty = dataManager.isDatabaseEmpty();

        if (isEmpty) {
            // 创建测试用户
            User testUser = new User("user", "password", R.drawable.avatar1);
            dataManager.addUser(testUser);
            Toast.makeText(this, "已创建测试用户: 用户名=user, 密码=password", Toast.LENGTH_SHORT).show();
        }
    }

    // 处理从注册页面返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // 获取从注册页面返回的用户名和头像信息
            String username = data.getStringExtra("username");
            int avatarResource = data.getIntExtra("avatarResource", R.drawable.avatar1);

            // 设置到登录页面
            usernameEditText.setText(username);
            selectedAvatarResource = avatarResource;
            avatarImageView.setImageResource(avatarResource);
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