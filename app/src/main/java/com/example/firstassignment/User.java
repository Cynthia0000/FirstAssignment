package com.example.firstassignment;

/**
 * 用户类，表示应用中的用户实体
 */
public class User {
    private int id;              // 用户ID
    private String username;     // 用户名
    private String password;     // 密码
    private int avatarResource;  // 头像资源ID

    /**
     * 构造函数
     * @param username 用户名
     * @param password 密码
     * @param avatarResource 头像资源ID
     */
    public User(String username, String password, int avatarResource) {
        this.username = username;
        this.password = password;
        this.avatarResource = avatarResource;
    }

    /**
     * 构造函数（包含ID）
     * @param id 用户ID
     * @param username 用户名
     * @param password 密码
     * @param avatarResource 头像资源ID
     */
    public User(int id, String username, String password, int avatarResource) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.avatarResource = avatarResource;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置用户ID
     * @param id 用户ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取头像资源ID
     * @return 头像资源ID
     */
    public int getAvatarResource() {
        return avatarResource;
    }

    /**
     * 设置头像资源ID
     * @param avatarResource 头像资源ID
     */
    public void setAvatarResource(int avatarResource) {
        this.avatarResource = avatarResource;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", avatarResource=" + avatarResource +
                '}';
    }
}