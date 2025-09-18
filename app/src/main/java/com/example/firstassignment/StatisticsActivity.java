package com.example.firstassignment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatisticsActivity extends AppCompatActivity {

    private LineChart memoryCurveChart;
    private BarChart wordStatusChart;
    private TextView totalLearnedWordsTextView;
    private TextView avgScoreTextView;
    private DataManager dataManager;
    private long currentUserId;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // 初始化UI组件
        memoryCurveChart = findViewById(R.id.memoryCurveChart);
        wordStatusChart = findViewById(R.id.wordStatusChart);
        totalLearnedWordsTextView = findViewById(R.id.totalLearnedWordsTextView);
        avgScoreTextView = findViewById(R.id.avgScoreTextView);

        // 初始化DataManager
        dataManager = new DataManager(this);

        // 初始化线程池和主线程Handler
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        // 在后台线程获取用户ID和加载数据
        loadDataInBackground();

        // 返回按钮功能
        findViewById(R.id.backToWordListButton).setOnClickListener(v -> {
            finish();
        });

        // 查看学习记录功能
        findViewById(R.id.viewLearningRecordsButton).setOnClickListener(v -> {
            Toast.makeText(StatisticsActivity.this, "查看学习记录功能开发中", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDataInBackground() {
        executorService.execute(() -> {
            try {
                // 获取当前登录用户ID
                currentUserId = getCurrentUserId();

                // 加载统计数据
                Map<String, Integer> dailyStats = dataManager.getUserDailyLearningStats((int) currentUserId);
                Map<String, Integer> statusStats = dataManager.getUserWordStatusStats((int) currentUserId);
                int totalLearnedWords = dataManager.getUserLearnedWordCount((int) currentUserId);
                double avgScore = dataManager.getUserAverageLearningScore((int) currentUserId);

                // 如果没有真实数据，使用模拟数据
                if ((dailyStats == null || dailyStats.isEmpty()) ||
                        (statusStats == null || statusStats.isEmpty()) ||
                        totalLearnedWords == 0) {
                    dailyStats = generateMockDailyStats();
                    statusStats = generateMockStatusStats();
                    totalLearnedWords = 30;
                    avgScore = 85.5;
                }

                final Map<String, Integer> finalDailyStats = dailyStats;
                final Map<String, Integer> finalStatusStats = statusStats;
                final int finalTotalLearnedWords = totalLearnedWords;
                final double finalAvgScore = avgScore;

                // 在主线程更新UI
                mainHandler.post(() -> {
                    showMemoryCurveChart(finalDailyStats);
                    showWordStatusChart(finalStatusStats);
                    showOverallStatistics(finalTotalLearnedWords, finalAvgScore);
                });
            } catch (Exception e) {
                // 错误处理
                mainHandler.post(() -> {
                    Toast.makeText(StatisticsActivity.this, "加载统计数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // 生成模拟的每日学习数据
    private Map<String, Integer> generateMockDailyStats() {
        Map<String, Integer> mockStats = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Date currentDate = new Date();

        // 生成过去7天的数据
        for (int i = 6; i >= 0; i--) {
            Date date = new Date(currentDate.getTime() - i * 24 * 60 * 60 * 1000);
            String dateStr = sdf.format(date);
            // 随机生成70-100之间的分数
            int score = 70 + (int)(Math.random() * 31);
            mockStats.put(dateStr, score);
        }

        return mockStats;
    }

    // 生成模拟的单词状态数据
    private Map<String, Integer> generateMockStatusStats() {
        Map<String, Integer> mockStats = new HashMap<>();
        mockStats.put("已掌握", 10);
        mockStats.put("学习中", 6);
        mockStats.put("需复习", 4);
        return mockStats;
    }

    private long getCurrentUserId() {
        try {
            // 获取当前登录用户ID（这里简化处理，假设使用第一个用户）
            List<User> users = dataManager.getAllUsers();
            if (users != null && !users.isEmpty()) {
                return users.get(0).getId();
            } else {
                // 如果没有用户，创建默认用户
                User defaultUser = new User("user", "password", R.drawable.ic_launcher_foreground);
                boolean success = dataManager.addUser(defaultUser);
                if (success) {
                    users = dataManager.getAllUsers();
                    if (users != null && !users.isEmpty()) {
                        return users.get(0).getId();
                    }
                }
            }
        } catch (Exception e) {
            // 处理异常，避免应用崩溃
            mainHandler.post(() -> {
                Toast.makeText(StatisticsActivity.this, "获取用户ID失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        return 1; // 默认ID
    }

    private void showMemoryCurveChart(Map<String, Integer> dailyStats) {
        if (dailyStats == null || dailyStats.isEmpty()) {
            // 如果没有数据，显示提示信息
            Toast.makeText(this, "暂无学习记录数据", Toast.LENGTH_SHORT).show();
            return;
        }

        // 准备图表数据
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;

        // 按日期排序并添加数据点
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        for (String date : dailyStats.keySet()) {
            entries.add(new Entry(index, dailyStats.get(date)));
            labels.add(date);
            index++;
        }

        // 设置数据集
        LineDataSet dataSet = new LineDataSet(entries, "学习分数");
        dataSet.setColor(getResources().getColor(R.color.tiffanyGreen));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(getResources().getColor(R.color.tiffanyGreenDark));
        dataSet.setCircleRadius(4f);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(getResources().getColor(R.color.tiffanyGreenLight));
        dataSet.setDrawFilled(true);

        // 设置图表数据
        LineData lineData = new LineData(dataSet);
        memoryCurveChart.setData(lineData);

        // 配置图表
        Description description = new Description();
        description.setText("记忆曲线 - 每日平均学习分数");
        memoryCurveChart.setDescription(description);

        XAxis xAxis = memoryCurveChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new CustomXAxisValueFormatter(labels));

        // 刷新图表
        memoryCurveChart.invalidate();
    }

    private void showWordStatusChart(Map<String, Integer> statusStats) {
        if (statusStats == null || statusStats.isEmpty()) {
            // 如果没有数据，显示提示信息
            Toast.makeText(this, "暂无单词学习状态数据", Toast.LENGTH_SHORT).show();
            return;
        }

        // 准备图表数据
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;

        // 添加数据点
        for (String status : statusStats.keySet()) {
            entries.add(new BarEntry(index, statusStats.get(status)));
            labels.add(status);
            index++;
        }

        // 设置数据集
        BarDataSet dataSet = new BarDataSet(entries, "单词数量");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(getResources().getColor(R.color.black));

        // 设置图表数据
        BarData barData = new BarData(dataSet);
        wordStatusChart.setData(barData);

        // 配置图表
        Description description = new Description();
        description.setText("单词学习状态分布");
        wordStatusChart.setDescription(description);

        XAxis xAxis = wordStatusChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new CustomXAxisValueFormatter(labels));

        // 刷新图表
        wordStatusChart.invalidate();
    }

    private void showOverallStatistics(int totalLearnedWords, double avgScore) {
        // 显示总学习单词数
        totalLearnedWordsTextView.setText("总学习单词数: " + totalLearnedWords);

        // 显示平均学习分数
        avgScoreTextView.setText("平均学习分数: " + String.format("%.1f", avgScore));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataManager != null) {
            dataManager.close();
        }
        // 关闭线程池
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // 自定义X轴标签格式化器（修正版本）
    private static class CustomXAxisValueFormatter extends ValueFormatter {
        private final ArrayList<String> labels;

        public CustomXAxisValueFormatter(ArrayList<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < labels.size()) {
                return labels.get(index);
            }
            return "";
        }
    }
}