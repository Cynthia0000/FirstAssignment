package com.example.firstassignment;

/**
 * 消息类，用于表示聊天界面中的消息对象
 */
public class Message {
    // 消息类型：用户发送
    public static final int SENDER_USER = 1;
    // 消息类型：AI发送
    public static final int SENDER_AI = 2;

    private String content; // 消息内容
    private int senderType; // 发送者类型
    private long timestamp; // 消息时间戳

    /**
     * 构造函数
     * @param content 消息内容
     * @param senderType 发送者类型
     */
    public Message(String content, int senderType) {
        this.content = content;
        this.senderType = senderType;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 获取消息内容
     * @return 消息内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息内容
     * @param content 消息内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取发送者类型
     * @return 发送者类型
     */
    public int getSenderType() {
        return senderType;
    }

    /**
     * 设置发送者类型
     * @param senderType 发送者类型
     */
    public void setSenderType(int senderType) {
        this.senderType = senderType;
    }

    /**
     * 获取时间戳
     * @return 时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳
     * @param timestamp 时间戳
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}