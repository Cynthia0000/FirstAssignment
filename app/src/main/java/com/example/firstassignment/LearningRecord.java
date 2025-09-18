package com.example.firstassignment;

import java.util.Date;

/**
 * 学习记录实体类，用于存储用户的单词学习进度和记忆曲线数据
 */
public class LearningRecord {
    private int id;
    private int userId;
    private int wordId;
    private int score; // 学习得分（0-100）
    private long timestamp; // 学习时间戳
    private String status; // 学习状态：如"已学习"、"已掌握"、"待复习"
    private int reviewCount; // 复习次数

    public LearningRecord(int userId, int wordId, int score, String status) {
        this.userId = userId;
        this.wordId = wordId;
        this.score = score;
        this.timestamp = System.currentTimeMillis();
        this.status = status;
        this.reviewCount = 1;
    }

    public LearningRecord(int id, int userId, int wordId, int score, long timestamp, String status, int reviewCount) {
        this.id = id;
        this.userId = userId;
        this.wordId = wordId;
        this.score = score;
        this.timestamp = timestamp;
        this.status = status;
        this.reviewCount = reviewCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Date getDate() {
        return new Date(timestamp);
    }
}