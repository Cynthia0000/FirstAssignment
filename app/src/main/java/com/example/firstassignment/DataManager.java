package com.example.firstassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
                        String word = cursor.getString(wordIndex);
                        String translation = cursor.getString(translationIndex);
                        String category = cursor.getString(categoryIndex);
                        String example = cursor.getString(exampleIndex);

                        wordList.add(new Word(word, translation, category, example));
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
}