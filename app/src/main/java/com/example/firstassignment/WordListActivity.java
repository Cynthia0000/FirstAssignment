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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        wordListView = findViewById(R.id.wordListView);
        customTitleBar = findViewById(R.id.customTitleBar);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        startLearningButton = findViewById(R.id.startLearningButton);

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
        wordList.add(new Word("apple", "苹果", "水果", "An apple a day keeps the doctor away."));
        wordList.add(new Word("banana", "香蕉", "水果", "I like eating bananas for breakfast."));
        wordList.add(new Word("book", "书", "学习用品", "Reading a good book can expand your knowledge."));
        wordList.add(new Word("computer", "电脑", "电子产品", "I use a computer to work every day."));
        wordList.add(new Word("friend", "朋友", "人际关系", "A true friend is hard to find."));
        wordList.add(new Word("happy", "快乐的", "情绪", "I feel very happy today."));
        wordList.add(new Word("learn", "学习", "行为", "It's never too late to learn."));
        wordList.add(new Word("music", "音乐", "艺术", "I enjoy listening to music in my free time."));
        wordList.add(new Word("phone", "手机", "电子产品", "My phone is an essential part of my daily life."));
        wordList.add(new Word("study", "学习", "行为", "We need to study hard to achieve our goals."));
    }

    // 单词实体类
    private class Word {
        private String word;        // 英文单词
        private String translation; // 中文翻译
        private String category;    // 单词类别
        private String example;     // 例句

        public Word(String word, String translation, String category, String example) {
            this.word = word;
            this.translation = translation;
            this.category = category;
            this.example = example;
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
}