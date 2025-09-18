package com.example.firstassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WordListActivity extends AppCompatActivity {

    private ListView wordListView;
    private CustomTitleBar customTitleBar;
    private Button backToHomeButton;
    private List<Word> wordList = new ArrayList<>();
    private Button startLearningButton;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        wordListView = findViewById(R.id.wordListView);
        customTitleBar = findViewById(R.id.customTitleBar);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        startLearningButton = findViewById(R.id.startLearningButton);

        // 初始化DataManager
        dataManager = new DataManager(this);

        // 设置自定义标题栏的标题
        customTitleBar.setTitle("单词学习");

        // 准备单词数据
        prepareWordData();

        // 创建自定义适配器
        WordListAdapter adapter = new WordListAdapter();
        wordListView.setAdapter(adapter);

        // 返回主页按钮点击事件
        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        // 学习统计按钮点击事件
        findViewById(R.id.statisticsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WordListActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });

        // 开始学习按钮点击事件
        startLearningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始学习，默认从第一个单词开始
                Intent intent = new Intent(WordListActivity.this, WordDetailActivity.class);
                intent.putExtra("wordIndex", 0);
                startActivity(intent);
            }
        });
    }

    // 准备单词数据
    private void prepareWordData() {
        wordList.clear();

        // 从数据库加载单词数据
        if (dataManager != null) {
            List<com.example.firstassignment.Word> dbWords = dataManager.getAllWords();
            if (dbWords != null && !dbWords.isEmpty()) {
                for (com.example.firstassignment.Word dbWord : dbWords) {
                    wordList.add(new Word(dbWord.getId(), dbWord.getWord(), dbWord.getTranslation(),
                            dbWord.getCategory(), dbWord.getExample()));
                }
            } else {
                // 如果数据库为空，添加一些初始单词数据
                addInitialWords();
            }
        }
    }

    // 添加初始单词数据
    private void addInitialWords() {
        // 添加初始单词到数据库
        dataManager.addWord("apple", "苹果", "水果", "An apple a day keeps the doctor away.");
        dataManager.addWord("banana", "香蕉", "水果", "I like eating bananas for breakfast.");
        dataManager.addWord("book", "书", "学习用品", "Reading a good book can expand your knowledge.");
        dataManager.addWord("computer", "电脑", "电子产品", "I use a computer to work every day.");
        dataManager.addWord("friend", "朋友", "人际关系", "A true friend is hard to find.");
        dataManager.addWord("happy", "快乐的", "情绪", "I feel very happy today.");
        dataManager.addWord("learn", "学习", "行为", "It's never too late to learn.");
        dataManager.addWord("music", "音乐", "艺术", "I enjoy listening to music in my free time.");
        dataManager.addWord("phone", "手机", "电子产品", "My phone is an essential part of my daily life.");
        dataManager.addWord("study", "学习", "行为", "We need to study hard to achieve our goals.");

        // 重新从数据库加载单词
        prepareWordData();
    }

    // 单词实体类
    private class Word {
        private int id;             // 单词ID，用于数据库存储
        private String word;        // 英文单词
        private String translation; // 中文翻译
        private String category;    // 单词类别
        private String example;     // 例句

        public Word(String word, String translation, String category, String example) {
            this.id = -1; // 未设置ID，将由数据库生成
            this.word = word;
            this.translation = translation;
            this.category = category;
            this.example = example;
        }

        public Word(int id, String word, String translation, String category, String example) {
            this.id = id;
            this.word = word;
            this.translation = translation;
            this.category = category;
            this.example = example;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getWord() {
            return word;
        }

        public String getTranslation() {
            return translation;
        }

        public String getCategory() {
            return category;
        }

        public String getExample() {
            return example;
        }
    }

    // 自定义适配器
    private class WordListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return wordList.size();
        }

        @Override
        public Object getItem(int position) {
            return wordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.word_list_item, null);
                holder = new ViewHolder();
                holder.wordTextView = convertView.findViewById(R.id.wordTextView);
                holder.translationTextView = convertView.findViewById(R.id.translationTextView);
                holder.categoryTextView = convertView.findViewById(R.id.categoryTextView);
                holder.exampleTextView = convertView.findViewById(R.id.exampleTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 设置数据
            Word word = wordList.get(position);
            holder.wordTextView.setText(word.getWord());
            holder.translationTextView.setText(word.getTranslation());
            holder.categoryTextView.setText(word.getCategory());
            holder.exampleTextView.setText(word.getExample());

            // 设置点击事件
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到单词详情页
                    Intent intent = new Intent(WordListActivity.this, WordDetailActivity.class);
                    intent.putExtra("wordIndex", position);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        // ViewHolder模式
        private class ViewHolder {
            TextView wordTextView;
            TextView translationTextView;
            TextView categoryTextView;
            TextView exampleTextView;
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