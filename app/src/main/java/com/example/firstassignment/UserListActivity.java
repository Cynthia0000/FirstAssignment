package com.example.firstassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText searchUsernameEditText;
    private Button searchButton;
    private DataManager dataManager;
    private List<User> userList;
    private List<User> originalUserList; // 保存原始用户列表
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListView = findViewById(R.id.user_list_view);
        backButton = findViewById(R.id.back_button);
        searchUsernameEditText = findViewById(R.id.search_username_edittext);
        searchButton = findViewById(R.id.search_button);

        dataManager = new DataManager(this);
        loadUsers();

        // 设置返回按钮点击事件
        backButton.setOnClickListener(v -> finish());

        // 设置搜索按钮点击事件
        searchButton.setOnClickListener(v -> searchUsers());

        // 支持回车键搜索
        searchUsernameEditText.setOnEditorActionListener((v, actionId, event) -> {
            searchUsers();
            return true;
        });

        // 设置列表项点击事件（查看用户详情）
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            showUserDetailDialog(selectedUser);
        });
    }

    /**
     * 搜索用户
     */
    private void searchUsers() {
        String searchText = searchUsernameEditText.getText().toString().trim().toLowerCase();

        if (searchText.isEmpty()) {
            // 如果搜索框为空，显示所有用户
            userList = new ArrayList<>(originalUserList);
        } else {
            // 否则根据用户名搜索
            List<User> searchResults = new ArrayList<>();
            for (User user : originalUserList) {
                if (user.getUsername().toLowerCase().contains(searchText)) {
                    searchResults.add(user);
                }
            }
            userList = searchResults;
        }

        // 更新列表显示
        userAdapter = new UserAdapter(userList);
        userListView.setAdapter(userAdapter);

        // 显示空列表提示
        TextView emptyTextView = findViewById(R.id.empty_text_view);
        if (userList.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            userListView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 检查用户名是否已存在
     * @param username 要检查的用户名
     * @param excludeUserId 排除的用户ID（用于修改自己的用户名时不检查自己）
     * @return 是否存在
     */
    private boolean isUsernameExists(String username, int excludeUserId) {
        for (User user : originalUserList) {
            if (user.getUsername().equals(username) && user.getId() != excludeUserId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从数据库加载所有用户
     */
    private void loadUsers() {
        userList = dataManager.getAllUsers();
        originalUserList = new ArrayList<>(userList); // 保存原始用户列表

        if (userList == null) {
            userList = new ArrayList<>();
            originalUserList = new ArrayList<>();
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
        EditText editUsernameEditText = dialogView.findViewById(R.id.edit_username_edittext);
        TextView passwordTextView = dialogView.findViewById(R.id.password_text);
        Button updateUsernameButton = dialogView.findViewById(R.id.update_username_button);
        Button deleteButton = dialogView.findViewById(R.id.delete_user_button);
        Button closeButton = dialogView.findViewById(R.id.close_button);

        idTextView.setText("用户ID: " + user.getId());
        usernameTextView.setText("用户名: " + user.getUsername());
        editUsernameEditText.setText(user.getUsername()); // 预填当前用户名
        passwordTextView.setText("密码: " + user.getPassword());

        AlertDialog dialog = builder.setView(dialogView).create();

        // 修改用户名按钮点击事件
        updateUsernameButton.setOnClickListener(v -> {
            String newUsername = editUsernameEditText.getText().toString().trim();
            if (newUsername.isEmpty()) {
                Toast.makeText(UserListActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newUsername.equals(user.getUsername())) {
                Toast.makeText(UserListActivity.this, "用户名未更改", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查新用户名是否已存在
            if (isUsernameExists(newUsername, user.getId())) {
                Toast.makeText(UserListActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建更新后的用户对象
            User updatedUser = new User(user.getId(), newUsername, user.getPassword(), user.getAvatarResource());

            // 更新用户信息
            boolean updated = dataManager.updateUser(updatedUser);
            if (updated) {
                Toast.makeText(UserListActivity.this, "用户名修改成功", Toast.LENGTH_SHORT).show();
                loadUsers(); // 重新加载用户列表
                dialog.dismiss();
            } else {
                Toast.makeText(UserListActivity.this, "用户名修改失败", Toast.LENGTH_SHORT).show();
            }
        });

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