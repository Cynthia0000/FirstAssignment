package com.example.firstassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private String[] words = {"apple", "banana", "book", "computer", "friend", "happy", "learn", "music", "phone", "study"};
    private String[] translations = {"苹果", "香蕉", "书", "电脑", "朋友", "快乐的", "学习", "音乐", "手机", "学习"};
    private String[] categories = {"水果", "水果", "学习用品", "电子产品", "人际关系", "情绪", "行为", "艺术", "电子产品", "行为"};
    private String[] examples = {
            "An apple a day keeps the doctor away.",
            "I like eating bananas for breakfast.",
            "Reading a good book can expand your knowledge.",
            "I use a computer to work every day.",
            "A true friend is hard to find.",
            "I feel very happy today.",
            "It's never too late to learn.",
            "I enjoy listening to music in my free time.",
            "My phone is an essential part of my daily life.",
            "We need to study hard to achieve our goals."
    };

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
                if (currentIndex < words.length - 1) {
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
                if (currentIndex > 0) {
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
                Toast.makeText(WordDetailActivity.this, "正在播放发音: " + words[currentIndex], Toast.LENGTH_SHORT).show();
                // 这里可以添加发音播放的逻辑
            }
        });
    }

    // 更新单词显示
    private void updateWordDisplay() {
        wordTextView.setText(words[currentIndex]);
        translationTextView.setText(translations[currentIndex]);
        categoryTextView.setText(categories[currentIndex]);
        exampleTextView.setText(examples[currentIndex]);
        pronunciationTextView.setText("点击播放发音");
    }
}