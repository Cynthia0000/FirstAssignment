package com.example.firstassignment;

/**
 * 单词类，用于表示单词学习功能中的单词对象
 */
public class Word {
    private int id;             // 单词ID，用于数据库存储
    private String word;        // 英文单词
    private String translation; // 中文翻译
    private String category;    // 单词类别
    private String example;     // 例句

    /**
     * 构造函数 - 用于新建单词
     * @param word 英文单词
     * @param translation 中文翻译
     * @param category 单词类别
     * @param example 例句
     */
    public Word(String word, String translation, String category, String example) {
        this.id = -1; // 未设置ID，将由数据库生成
        this.word = word;
        this.translation = translation;
        this.category = category;
        this.example = example;
    }

    /**
     * 构造函数 - 用于从数据库加载单词
     * @param id 单词ID
     * @param word 英文单词
     * @param translation 中文翻译
     * @param category 单词类别
     * @param example 例句
     */
    public Word(int id, String word, String translation, String category, String example) {
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.category = category;
        this.example = example;
    }

    /**
     * 获取单词ID
     * @return 单词ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置单词ID
     * @param id 单词ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取英文单词
     * @return 英文单词
     */
    public String getWord() {
        return word;
    }

    /**
     * 设置英文单词
     * @param word 英文单词
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * 获取中文翻译
     * @return 中文翻译
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * 设置中文翻译
     * @param translation 中文翻译
     */
    public void setTranslation(String translation) {
        this.translation = translation;
    }

    /**
     * 获取单词类别
     * @return 单词类别
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置单词类别
     * @param category 单词类别
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取例句
     * @return 例句
     */
    public String getExample() {
        return example;
    }

    /**
     * 设置例句
     * @param example 例句
     */
    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", translation='" + translation + '\'' +
                ", category='" + category + '\'' +
                ", example='" + example + '\'' +
                '}';
    }
}