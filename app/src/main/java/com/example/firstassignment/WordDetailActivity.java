package com.example.firstassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private CustomTitleBar customTitleBar;
    private DataManager dataManager;
    private List<Word> wordList;
    private int currentIndex = 0;

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
        customTitleBar = findViewById(R.id.customTitleBar);

        // 初始化DataManager
        dataManager = new DataManager(this);

        // 从数据库加载单词列表
        wordList = dataManager.getAllWords();

        // 设置自定义标题栏的标题
        customTitleBar.setTitle("单词学习");

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