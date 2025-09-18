package com.example.firstassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WordDetailActivity extends AppCompatActivity {

    private TextView wordTextView;
    private TextView translationTextView;
    private TextView categoryTextView;
    private TextView exampleTextView;
    private TextView pronunciationTextView;
    private Button nextButton;
    private Button previousButton;
    private Button backButton;
    private Button recordLearningButton;
    private RatingBar learningRatingBar;
    private CustomTitleBar customTitleBar;
    private DataManager dataManager;
    private List<Word> wordList;
    private int currentIndex = 0;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        wordTextView = findViewById(R.id.wordTextView);
        translationTextView = findViewById(R.id.translationTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        exampleTextView = findViewById(R.id.exampleTextView);
        pronunciationTextView = findViewById(R.id.pronunciationTextView);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        backButton = findViewById(R.id.backButton);
        recordLearningButton = findViewById(R.id.recordLearningButton);
        learningRatingBar = findViewById(R.id.learningRatingBar);
        customTitleBar = findViewById(R.id.customTitleBar);

        // 初始化DataManager
        dataManager = new DataManager(this);

        // 从数据库加载单词列表
        wordList = dataManager.getAllWords();

        // 设置自定义标题栏的标题
        customTitleBar.setTitle("单词学习");

        // 获取当前登录用户的ID（这里假设默认是第一个用户）
        List<User> users = dataManager.getAllUsers();
        if (users != null && !users.isEmpty()) {
            currentUserId = users.get(0).getId();
        } else {
            // 如果没有用户，创建一个默认用户
            User defaultUser = new User("user", "password", R.drawable.ic_user);
            dataManager.addUser(defaultUser);
            users = dataManager.getAllUsers();
            if (users != null && !users.isEmpty()) {
                currentUserId = users.get(0).getId();
            }
        }

        // 获取从WordListActivity传递过来的单词索引
        Intent intent = getIntent();
        if (intent.hasExtra("wordIndex")) {
            currentIndex = intent.getIntExtra("wordIndex", 0);
        }

        // 显示当前单词信息
        updateWordDisplay();

        // 下一个按钮点击事件
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordList != null && !wordList.isEmpty() && currentIndex < wordList.size() - 1) {
                    currentIndex++;
                    updateWordDisplay();
                } else {
                    Toast.makeText(WordDetailActivity.this, "已经是最后一个单词了", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 上一个按钮点击事件
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordList != null && !wordList.isEmpty() && currentIndex > 0) {
                    currentIndex--;
                    updateWordDisplay();
                } else {
                    Toast.makeText(WordDetailActivity.this, "已经是第一个单词了", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 点击单词显示发音
        pronunciationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordList != null && !wordList.isEmpty()) {
                    String word = wordList.get(currentIndex).getWord();
                    Toast.makeText(WordDetailActivity.this, "正在播放发音: " + word, Toast.LENGTH_SHORT).show();
                    // 这里可以添加发音播放的逻辑
                }
            }
        });

        // 记录学习情况按钮点击事件
        recordLearningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordList != null && !wordList.isEmpty() && currentIndex < wordList.size()) {
                    // 获取用户的评分（0-5星转换为0-100分）
                    float rating = learningRatingBar.getRating();
                    int score = (int) (rating * 20); // 5星=100分，1星=20分

                    // 根据评分确定学习状态
                    String status;
                    if (score >= 80) {
                        status = "已掌握";
                    } else if (score >= 40) {
                        status = "学习中";
                    } else {
                        status = "待复习";
                    }

                    // 获取当前单词
                    Word word = wordList.get(currentIndex);

                    // 检查是否已经有该单词的学习记录
                    List<LearningRecord> existingRecords = dataManager.getUserWordLearningRecords(currentUserId, word.getId());

                    boolean success = false;
                    if (existingRecords != null && !existingRecords.isEmpty()) {
                        // 更新现有记录
                        LearningRecord existingRecord = existingRecords.get(existingRecords.size() - 1); // 取最新的记录
                        existingRecord.setScore(score);
                        existingRecord.setStatus(status);
                        existingRecord.setTimestamp(System.currentTimeMillis());
                        existingRecord.setReviewCount(existingRecord.getReviewCount() + 1);
                        success = dataManager.updateLearningRecord(existingRecord);
                    } else {
                        // 创建新记录
                        LearningRecord newRecord = new LearningRecord(currentUserId, word.getId(), score, status);
                        success = dataManager.addLearningRecord(newRecord);
                    }

                    if (success) {
                        Toast.makeText(WordDetailActivity.this, "学习情况已记录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WordDetailActivity.this, "记录学习情况失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // 更新单词显示
    private void updateWordDisplay() {
        if (wordList != null && !wordList.isEmpty() && currentIndex < wordList.size()) {
            Word word = wordList.get(currentIndex);
            wordTextView.setText(word.getWord());
            translationTextView.setText(word.getTranslation());
            categoryTextView.setText(word.getCategory());
            exampleTextView.setText(word.getExample());
            pronunciationTextView.setText("点击播放发音");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭数据库连接，避免内存泄漏
        if (dataManager != null) {
            dataManager.close();
        }
    }
}