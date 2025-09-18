package com.example.firstassignment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库帮助类，用于创建和管理SQLite数据库
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // 数据库名称
    private static final String DATABASE_NAME = "EnglishLearningApp.db";
    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    // 用户表
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AVATAR = "avatar_resource";

    // 单词表
    public static final String TABLE_WORDS = "words";
    public static final String COLUMN_WORD_ID = "_id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_EXAMPLE = "example";

    // 聊天消息表
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_SENDER_TYPE = "sender_type";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_USER_ID_FK = "user_id";

    // 学习记录表
    public static final String TABLE_LEARNING_RECORDS = "learning_records";
    public static final String COLUMN_RECORD_ID = "_id";
    public static final String COLUMN_RECORD_USER_ID = "user_id";
    public static final String COLUMN_RECORD_WORD_ID = "word_id";
    public static final String COLUMN_RECORD_SCORE = "score";
    public static final String COLUMN_RECORD_STATUS = "status";
    public static final String COLUMN_RECORD_REVIEW_COUNT = "review_count";

    // 创建用户表的SQL语句
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_AVATAR + " INTEGER NOT NULL" +
            ");";

    // 创建单词表的SQL语句
    private static final String CREATE_TABLE_WORDS = "CREATE TABLE " + TABLE_WORDS + "(" +
            COLUMN_WORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_WORD + " TEXT NOT NULL, " +
            COLUMN_TRANSLATION + " TEXT NOT NULL, " +
            COLUMN_CATEGORY + " TEXT NOT NULL, " +
            COLUMN_EXAMPLE + " TEXT NOT NULL" +
            ");";

    // 创建聊天消息表的SQL语句
    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "(" +
            COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CONTENT + " TEXT NOT NULL, " +
            COLUMN_SENDER_TYPE + " INTEGER NOT NULL, " +
            COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
            COLUMN_USER_ID_FK + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
            ");";

    // 创建学习记录表的SQL语句
    private static final String CREATE_TABLE_LEARNING_RECORDS = "CREATE TABLE " + TABLE_LEARNING_RECORDS + "(" +
            COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECORD_USER_ID + " INTEGER NOT NULL, " +
            COLUMN_RECORD_WORD_ID + " INTEGER NOT NULL, " +
            COLUMN_RECORD_SCORE + " INTEGER NOT NULL, " +
            COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
            COLUMN_RECORD_STATUS + " TEXT NOT NULL, " +
            COLUMN_RECORD_REVIEW_COUNT + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + COLUMN_RECORD_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
            "FOREIGN KEY(" + COLUMN_RECORD_WORD_ID + ") REFERENCES " + TABLE_WORDS + "(" + COLUMN_WORD_ID + ")" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建所有表
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_WORDS);
        db.execSQL(CREATE_TABLE_MESSAGES);
        db.execSQL(CREATE_TABLE_LEARNING_RECORDS);

        // 初始化单词数据
        initializeWordData(db);

        Log.d(TAG, "Database and tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时，删除旧表并创建新表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEARNING_RECORDS);
        onCreate(db);
    }

    /**
     * 初始化单词数据
     */
    private void initializeWordData(SQLiteDatabase db) {
        // 插入示例单词数据
        String[] words = {
                "apple", "banana", "book", "computer", "friend",
                "happy", "learn", "music", "phone", "study",
                "cat", "dog", "house", "water", "food",
                "time", "weather", "family", "school", "work"
        };

        String[] translations = {
                "苹果", "香蕉", "书", "电脑", "朋友",
                "快乐的", "学习", "音乐", "手机", "学习",
                "猫", "狗", "房子", "水", "食物",
                "时间", "天气", "家庭", "学校", "工作"
        };

        String[] categories = {
                "水果", "水果", "学习用品", "电子产品", "人际关系",
                "情绪", "行为", "艺术", "电子产品", "行为",
                "动物", "动物", "建筑", "自然", "食物",
                "时间", "自然现象", "人际关系", "教育", "职业"
        };

        String[] examples = {
                "An apple a day keeps the doctor away.",
                "I like eating bananas for breakfast.",
                "Reading a good book can expand your knowledge.",
                "I use a computer to work every day.",
                "A true friend is hard to find.",
                "I feel very happy today.",
                "It''s never too late to learn.", // 注意：这里是两个单引号
                "I enjoy listening to music in my free time.",
                "My phone is an essential part of my daily life.",
                "We need to study hard to achieve our goals.",
                "The cat is sleeping on the sofa.",
                "The dog is running in the park.",
                "We live in a beautiful house.",
                "Drinking enough water is good for health.",
                "I'm hungry and need some food.",
                "Time flies when you're having fun.",
                "The weather is nice today, let's go outside.",
                "Family is the most important thing in life.",
                "I go to school every weekday.",
                "I love my job and work hard every day."
        };

        // 插入数据
        for (int i = 0; i < words.length; i++) {
            String insertQuery = "INSERT INTO " + TABLE_WORDS + "(" +
                    COLUMN_WORD + ", " +
                    COLUMN_TRANSLATION + ", " +
                    COLUMN_CATEGORY + ", " +
                    COLUMN_EXAMPLE +
                    ") VALUES ('" +
                    words[i] + "', '" +
                    translations[i] + "', '" +
                    categories[i] + "', '" +
                    examples[i] + "');";
            db.execSQL(insertQuery);
        }

        Log.d(TAG, "Word data initialized successfully");
    }

    /**
     * 检查数据库是否存在
     */
    public boolean isDatabaseEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 检查用户表是否为空
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count == 0;
            }
            return true;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}