package com.example.firstassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户列表活动，显示并管理所有用户信息
 */
public class UserListActivity extends AppCompatActivity {
    private ListView userListView;
    private Button backButton;
    private DataManager dataManager;
    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListView = findViewById(R.id.user_list_view);
        backButton = findViewById(R.id.back_button);

        dataManager = new DataManager(this);
        loadUsers();

        // 设置返回按钮点击事件
        backButton.setOnClickListener(v -> finish());

        // 设置列表项点击事件（查看用户详情）
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            showUserDetailDialog(selectedUser);
        });
    }

    /**
     * 从数据库加载所有用户
     */
    private void loadUsers() {
        userList = dataManager.getAllUsers();

        if (userList == null) {
            userList = new ArrayList<>();
        }

        userAdapter = new UserAdapter(userList);
        userListView.setAdapter(userAdapter);

        // 如果没有用户，显示提示信息
        if (userList.isEmpty()) {
            TextView emptyTextView = findViewById(R.id.empty_text_view);
            emptyTextView.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.GONE);
        } else {
            TextView emptyTextView = findViewById(R.id.empty_text_view);
            emptyTextView.setVisibility(View.GONE);
            userListView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示用户详情对话框
     */
    private void showUserDetailDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_user_detail, null);

        TextView idTextView = dialogView.findViewById(R.id.user_id_text);
        TextView usernameTextView = dialogView.findViewById(R.id.username_text);
        TextView passwordTextView = dialogView.findViewById(R.id.password_text);
        Button deleteButton = dialogView.findViewById(R.id.delete_user_button);
        Button closeButton = dialogView.findViewById(R.id.close_button);

        idTextView.setText("用户ID: " + user.getId());
        usernameTextView.setText("用户名: " + user.getUsername());
        passwordTextView.setText("密码: " + user.getPassword());

        AlertDialog dialog = builder.setView(dialogView).create();

        // 删除用户按钮点击事件
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(UserListActivity.this)
                    .setTitle("确认删除")
                    .setMessage("确定要删除用户 '" + user.getUsername() + "' 吗？")
                    .setPositiveButton("确定", (dialog1, which) -> {
                        boolean deleted = dataManager.deleteUser(user.getId());
                        if (deleted) {
                            Toast.makeText(UserListActivity.this, "用户删除成功", Toast.LENGTH_SHORT).show();
                            loadUsers(); // 重新加载用户列表
                        } else {
                            Toast.makeText(UserListActivity.this, "用户删除失败", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        // 关闭按钮点击事件
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * 自定义用户适配器，用于显示用户列表
     */
    private class UserAdapter extends ArrayAdapter<User> {
        public UserAdapter(List<User> users) {
            super(UserListActivity.this, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User user = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            }

            TextView usernameTextView = convertView.findViewById(R.id.username_text_view);
            TextView userIdTextView = convertView.findViewById(R.id.user_id_text_view);

            usernameTextView.setText(user.getUsername());
            userIdTextView.setText("ID: " + user.getId());

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataManager != null) {
            dataManager.close();
        }
    }
}