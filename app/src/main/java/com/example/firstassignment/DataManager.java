package com.example.firstassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据管理器，封装对数据库的增删查改操作
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DataManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /**
     * 检查数据库是否为空
     */
    public boolean isDatabaseEmpty() {
        Cursor cursor = null;
        try {
            // 检查用户表是否为空
            cursor = database.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS, null);
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

    /**
     * 添加用户
     * @param user 用户对象
     * @return 添加是否成功
     */
    public boolean addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COLUMN_AVATAR, user.getAvatarResource());

        try {
            long result = database.insert(DatabaseHelper.TABLE_USERS, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding user: " + e.getMessage());
            return false;
        }
    }

    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，如果登录失败则返回null
     */
    public User verifyUserLogin(String username, String password) {
        User user = null;
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS +
                    " WHERE " + DatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                    DatabaseHelper.COLUMN_PASSWORD + " = ?";
            cursor = database.rawQuery(query, new String[]{username, password});

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
                int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
                int avatarIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_AVATAR);

                if (idIndex != -1 && usernameIndex != -1 && passwordIndex != -1 && avatarIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String dbUsername = cursor.getString(usernameIndex);
                    String dbPassword = cursor.getString(passwordIndex);
                    int avatarResource = cursor.getInt(avatarIndex);

                    user = new User(id, dbUsername, dbPassword, avatarResource);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error verifying user login: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新是否成功
     */
    public boolean updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COLUMN_AVATAR, user.getAvatarResource());

        try {
            int result = database.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getId())}
            );
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除是否成功
     */
    public boolean deleteUser(int userId) {
        try {
            int result = database.delete(
                    DatabaseHelper.TABLE_USERS,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * 添加单词
     * @param word 英文单词
     * @param translation 中文翻译
     * @param category 单词类别
     * @param example 例句
     * @return 添加是否成功
     */
    public boolean addWord(String word, String translation, String category, String example) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_WORD, word);
        values.put(DatabaseHelper.COLUMN_TRANSLATION, translation);
        values.put(DatabaseHelper.COLUMN_CATEGORY, category);
        values.put(DatabaseHelper.COLUMN_EXAMPLE, example);

        try {
            long result = database.insert(DatabaseHelper.TABLE_WORDS, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding word: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取所有单词
     * @return 单词列表
     */
    public List<Word> getAllWords() {
        List<Word> wordList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_WORDS;
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int wordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD);
                    int translationIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TRANSLATION);
                    int categoryIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY);
                    int exampleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EXAMPLE);

                    if (wordIndex != -1 && translationIndex != -1 && categoryIndex != -1 && exampleIndex != -1) {
                        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD_ID));
                        String word = cursor.getString(wordIndex);
                        String translation = cursor.getString(translationIndex);
                        String category = cursor.getString(categoryIndex);
                        String example = cursor.getString(exampleIndex);

                        wordList.add(new Word(id, word, translation, category, example));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all words: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return wordList;
    }

    /**
     * 更新单词
     * @param wordId 单词ID
     * @param word 英文单词
     * @param translation 中文翻译
     * @param category 单词类别
     * @param example 例句
     * @return 更新是否成功
     */
    public boolean updateWord(int wordId, String word, String translation, String category, String example) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_WORD, word);
        values.put(DatabaseHelper.COLUMN_TRANSLATION, translation);
        values.put(DatabaseHelper.COLUMN_CATEGORY, category);
        values.put(DatabaseHelper.COLUMN_EXAMPLE, example);

        try {
            int result = database.update(
                    DatabaseHelper.TABLE_WORDS,
                    values,
                    DatabaseHelper.COLUMN_WORD_ID + " = ?",
                    new String[]{String.valueOf(wordId)}
            );
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating word: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除单词
     * @param wordId 单词ID
     * @return 删除是否成功
     */
    public boolean deleteWord(int wordId) {
        try {
            int result = database.delete(
                    DatabaseHelper.TABLE_WORDS,
                    DatabaseHelper.COLUMN_WORD_ID + " = ?",
                    new String[]{String.valueOf(wordId)}
            );
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting word: " + e.getMessage());
            return false;
        }
    }

    /**
     * 添加聊天消息
     * @param content 消息内容
     * @param senderType 发送者类型
     * @param userId 用户ID（可为null）
     * @return 添加是否成功
     */
    public boolean addMessage(String content, int senderType, Integer userId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CONTENT, content);
        values.put(DatabaseHelper.COLUMN_SENDER_TYPE, senderType);
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());

        if (userId != null) {
            values.put(DatabaseHelper.COLUMN_USER_ID_FK, userId);
        }

        try {
            long result = database.insert(DatabaseHelper.TABLE_MESSAGES, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding message: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取指定用户的所有消息
     * @param userId 用户ID（可为null，获取所有消息）
     * @return 消息列表
     */
    public List<Message> getAllMessages(Integer userId) {
        List<Message> messageList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query;
            String[] params = null;

            if (userId != null) {
                query = "SELECT * FROM " + DatabaseHelper.TABLE_MESSAGES +
                        " WHERE " + DatabaseHelper.COLUMN_USER_ID_FK + " = ? ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;
                params = new String[]{String.valueOf(userId)};
            } else {
                query = "SELECT * FROM " + DatabaseHelper.TABLE_MESSAGES +
                        " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;
            }

            cursor = database.rawQuery(query, params);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int contentIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT);
                    int senderTypeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SENDER_TYPE);
                    int timestampIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP);

                    if (contentIndex != -1 && senderTypeIndex != -1 && timestampIndex != -1) {
                        String content = cursor.getString(contentIndex);
                        int senderType = cursor.getInt(senderTypeIndex);
                        long timestamp = cursor.getLong(timestampIndex);

                        Message message = new Message(content, senderType);
                        message.setTimestamp(timestamp);
                        messageList.add(message);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all messages: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return messageList;
    }

    /**
     * 根据ID获取用户
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    public User getUserById(int userId) {
        User user = null;
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS +
                    " WHERE " + DatabaseHelper.COLUMN_USER_ID + " = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
                int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
                int avatarIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_AVATAR);

                if (idIndex != -1 && usernameIndex != -1 && passwordIndex != -1 && avatarIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String dbUsername = cursor.getString(usernameIndex);
                    String dbPassword = cursor.getString(passwordIndex);
                    int avatarResource = cursor.getInt(avatarIndex);

                    user = new User(id, dbUsername, dbPassword, avatarResource);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by id: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS;
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
                    int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
                    int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
                    int avatarIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_AVATAR);

                    if (idIndex != -1 && usernameIndex != -1 && passwordIndex != -1 && avatarIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        String dbUsername = cursor.getString(usernameIndex);
                        String dbPassword = cursor.getString(passwordIndex);
                        int avatarResource = cursor.getInt(avatarIndex);

                        userList.add(new User(id, dbUsername, dbPassword, avatarResource));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all users: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return userList;
    }

    /**
     * 添加学习记录
     * @param learningRecord 学习记录对象
     * @return 添加是否成功
     */
    public boolean addLearningRecord(LearningRecord learningRecord) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECORD_USER_ID, learningRecord.getUserId());
        values.put(DatabaseHelper.COLUMN_RECORD_WORD_ID, learningRecord.getWordId());
        values.put(DatabaseHelper.COLUMN_RECORD_SCORE, learningRecord.getScore());
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, learningRecord.getTimestamp());
        values.put(DatabaseHelper.COLUMN_RECORD_STATUS, learningRecord.getStatus());
        values.put(DatabaseHelper.COLUMN_RECORD_REVIEW_COUNT, learningRecord.getReviewCount());

        try {
            long result = database.insert(DatabaseHelper.TABLE_LEARNING_RECORDS, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding learning record: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新学习记录
     * @param learningRecord 学习记录对象
     * @return 更新是否成功
     */
    public boolean updateLearningRecord(LearningRecord learningRecord) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECORD_SCORE, learningRecord.getScore());
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, learningRecord.getTimestamp());
        values.put(DatabaseHelper.COLUMN_RECORD_STATUS, learningRecord.getStatus());
        values.put(DatabaseHelper.COLUMN_RECORD_REVIEW_COUNT, learningRecord.getReviewCount());

        try {
            int result = database.update(
                    DatabaseHelper.TABLE_LEARNING_RECORDS,
                    values,
                    DatabaseHelper.COLUMN_RECORD_ID + " = ?",
                    new String[]{String.valueOf(learningRecord.getId())}
            );
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating learning record: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取用户的学习记录
     * @param userId 用户ID
     * @return 学习记录列表
     */
    public List<LearningRecord> getUserLearningRecords(int userId) {
        List<LearningRecord> records = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_LEARNING_RECORDS +
                    " WHERE " + DatabaseHelper.COLUMN_RECORD_USER_ID + " = ? ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP + " DESC";
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_ID);
                    int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_USER_ID);
                    int wordIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_WORD_ID);
                    int scoreIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_SCORE);
                    int timestampIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP);
                    int statusIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_STATUS);
                    int reviewCountIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_REVIEW_COUNT);

                    if (idIndex != -1 && userIdIndex != -1 && wordIdIndex != -1 && scoreIndex != -1 &&
                            timestampIndex != -1 && statusIndex != -1 && reviewCountIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        int dbUserId = cursor.getInt(userIdIndex);
                        int wordId = cursor.getInt(wordIdIndex);
                        int score = cursor.getInt(scoreIndex);
                        long timestamp = cursor.getLong(timestampIndex);
                        String status = cursor.getString(statusIndex);
                        int reviewCount = cursor.getInt(reviewCountIndex);

                        records.add(new LearningRecord(id, dbUserId, wordId, score, timestamp, status, reviewCount));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user learning records: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return records;
    }

    /**
     * 获取用户的单词学习统计（按日期分组）
     * @param userId 用户ID
     * @return 日期到平均学习分数的映射
     */
    public Map<String, Integer> getUserDailyLearningStats(int userId) {
        Map<String, Integer> stats = new HashMap<>();
        Cursor cursor = null;

        try {
            // 按日期分组，计算每天的平均学习分数
            String query = "SELECT strftime('%Y-%m-%d', datetime(" + DatabaseHelper.COLUMN_TIMESTAMP + "/1000, 'unixepoch')) as date, " +
                    "AVG(" + DatabaseHelper.COLUMN_RECORD_SCORE + ") as avg_score FROM " + DatabaseHelper.TABLE_LEARNING_RECORDS +
                    " WHERE " + DatabaseHelper.COLUMN_RECORD_USER_ID + " = ?" +
                    " GROUP BY date ORDER BY date";
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int dateIndex = cursor.getColumnIndex("date");
                    int scoreIndex = cursor.getColumnIndex("avg_score");

                    if (dateIndex != -1 && scoreIndex != -1) {
                        String date = cursor.getString(dateIndex);
                        int avgScore = Math.round(cursor.getFloat(scoreIndex));
                        stats.put(date, avgScore);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user daily learning stats: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return stats;
    }

    /**
     * 获取用户的单词记忆曲线数据
     * @param userId 用户ID
     * @param wordId 单词ID
     * @return 学习记录列表（按时间排序）
     */
    public List<LearningRecord> getUserWordLearningRecords(int userId, int wordId) {
        List<LearningRecord> records = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_LEARNING_RECORDS +
                    " WHERE " + DatabaseHelper.COLUMN_RECORD_USER_ID + " = ? AND " +
                    DatabaseHelper.COLUMN_RECORD_WORD_ID + " = ? ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(wordId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_ID);
                    int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_USER_ID);
                    int wordIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_WORD_ID);
                    int scoreIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_SCORE);
                    int timestampIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP);
                    int statusIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_STATUS);
                    int reviewCountIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_REVIEW_COUNT);

                    if (idIndex != -1 && userIdIndex != -1 && wordIdIndex != -1 && scoreIndex != -1 &&
                            timestampIndex != -1 && statusIndex != -1 && reviewCountIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        int dbUserId = cursor.getInt(userIdIndex);
                        int dbWordId = cursor.getInt(wordIdIndex);
                        int score = cursor.getInt(scoreIndex);
                        long timestamp = cursor.getLong(timestampIndex);
                        String status = cursor.getString(statusIndex);
                        int reviewCount = cursor.getInt(reviewCountIndex);

                        records.add(new LearningRecord(id, dbUserId, dbWordId, score, timestamp, status, reviewCount));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user word learning records: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return records;
    }

    /**
     * 获取用户的单词学习进度统计
     * @param userId 用户ID
     * @return 状态到单词数量的映射
     */
    public Map<String, Integer> getUserWordStatusStats(int userId) {
        Map<String, Integer> stats = new HashMap<>();
        Cursor cursor = null;

        try {
            // 按状态分组，统计每种状态的单词数量
            String query = "SELECT " + DatabaseHelper.COLUMN_RECORD_STATUS + ", COUNT(DISTINCT " + DatabaseHelper.COLUMN_RECORD_WORD_ID + ") as count " +
                    "FROM " + DatabaseHelper.TABLE_LEARNING_RECORDS +
                    " WHERE " + DatabaseHelper.COLUMN_RECORD_USER_ID + " = ?" +
                    " GROUP BY " + DatabaseHelper.COLUMN_RECORD_STATUS;
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int statusIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECORD_STATUS);
                    int countIndex = cursor.getColumnIndex("count");

                    if (statusIndex != -1 && countIndex != -1) {
                        String status = cursor.getString(statusIndex);
                        int count = cursor.getInt(countIndex);
                        stats.put(status, count);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user word status stats: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return stats;
    }

    /**
     * 获取用户学习的单词总数
     * @param userId 用户ID
     * @return 学习的单词总数
     */
    public int getUserLearnedWordCount(int userId) {
        int count = 0;
        Cursor cursor = null;

        try {
            String query = "SELECT COUNT(DISTINCT " + DatabaseHelper.COLUMN_RECORD_WORD_ID + ") FROM " +
                    DatabaseHelper.TABLE_LEARNING_RECORDS +
                    " WHERE " + DatabaseHelper.COLUMN_RECORD_USER_ID + " = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user learned word count: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }

    /**
     * 获取用户的平均学习得分
     * @param userId 用户ID
     * @return 平均得分
     */
    public double getUserAverageLearningScore(int userId) {
        double average = 0.0;
        Cursor cursor = null;

        try {
            String query = "SELECT AVG(" + DatabaseHelper.COLUMN_RECORD_SCORE + ") FROM " +
                    DatabaseHelper.TABLE_LEARNING_RECORDS +
                    " WHERE " + DatabaseHelper.COLUMN_RECORD_USER_ID + " = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                if (!cursor.isNull(0)) {
                    average = cursor.getDouble(0);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user average learning score: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return average;
    }
}