package com.techsanelab.todo.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import com.techsanelab.todo.ChartHandler;
import com.techsanelab.todo.DBHelper;
import com.techsanelab.todo.EasyFont;
import com.techsanelab.todo.R;
import com.techsanelab.todo.Utils;
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;

public class AnalysisFragment extends Fragment {

    private PieChart chart;
    private BarChart dowChart, monthChart;
    private static final String TAG = "AnalysisFragment";
    private View view;
    private EasyFont easyFont;
    private HashMap<Integer, Integer> dayAnalysis, monthAnalysis;
    private ArrayList<Integer> dowData, monthlyData, analysisData;
    private ArrayList<View> charts;
    private DBHelper dbHelper;
    private DateFormatSymbols dfs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_analysis, container, false);
        Objects.requireNonNull(getActivity()).getWindow().setStatusBarColor(getActivity().getColor(R.color.analysisColorDark));
        easyFont = new EasyFont(getActivity(), view);
        dbHelper = DBHelper.getInstance(getContext());

        dfs = new DateFormatSymbols();

        chart = view.findViewById(R.id.chart1);
        monthChart = view.findViewById(R.id.chart2);
        dowChart = view.findViewById(R.id.chart3);
        RelativeLayout introLayout = view.findViewById(R.id.intro_layout);

        if (dbHelper.todosCount() > 3) {
            introLayout.setVisibility(View.GONE);
            setupCharts();
            setupToggleButtons();
            setupCards();
        }


        return view;
    }

    public void setupToggleButtons() {
        ThemedToggleButtonGroup buttonGroup = view.findViewById(R.id.indicator);
        buttonGroup.setOnSelectListener((ThemedButton btn) -> {
            switch (btn.getId()) {
                case R.id.btn1:
                    showIndex(charts, 0);
                    chart.spin(500, 0, 45, Easing.EaseInOutQuad);
                    break;
                case R.id.btn2:
                    showIndex(charts, 1);
                    dowChart.animateY(800,Easing.EaseInOutQuad);
                    break;
                case R.id.btn3:
                    showIndex(charts, 2);
                    monthChart.animateY(800,Easing.EaseInOutQuad);
                    break;
            }

            return kotlin.Unit.INSTANCE;
        });
        buttonGroup.selectButton(R.id.btn1);
    }

    public void setupCards() {
        addCardUI(R.id.card1, String.format("%d\nEvents", analysisData.get(0)));
        addCardUI(R.id.card2, String.format("%d\nHabits", analysisData.get(4)));
        addCardUI(R.id.card3, String.format("%d\nIn progress", analysisData.get(2)));
        addCardUI(R.id.card4, String.format("\"%s\"\nBusiest day", dfs.getWeekdays()[dbHelper.getBusiestDay() + 1]));
        addCardUI(R.id.card5, String.format("\"%s\"\nProductive day", dfs.getWeekdays()[dbHelper.getMostProductiveDay() + 1]));
        addCardUI(R.id.card6, String.format("\"%s\"\nYour focus", dbHelper.getFocus()));
    }

    public void setupCharts() {
        ChartHandler chartHandler = new ChartHandler();
        chartHandler.setActivity(getActivity());
        chartHandler.setEasyFont(easyFont);

        chartHandler.buildPieChart(chart, getActivity(), "Events");
        chartHandler.buildLineChart(monthChart, getActivity(), "Month");
        chartHandler.buildLineChart(dowChart, getActivity(), "Days");


        monthChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Log.d(TAG, "getFormattedValue: " + dfs.getMonths()[(int) value] + value);
                return dfs.getMonths()[(int) value];
            }
        });

        dowChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Log.d(TAG, "getFormattedValue: " + dfs.getWeekdays()[(int) value] + value);
                return dfs.getWeekdays()[(int) value + 1];
            }
        });

        monthAnalysis = dbHelper.monthAnalysis();
        monthlyData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            if (monthAnalysis.containsKey(i))
                monthlyData.add(monthAnalysis.get(i));
            else
                monthlyData.add(0);
        }
        chartHandler.setBarChartData(monthChart, monthlyData);

        dayAnalysis = dbHelper.daysAnalysis();
        dowData = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            if (dayAnalysis.containsKey(i))
                dowData.add(dayAnalysis.get(i));
            else
                dowData.add(0);
        }

        chartHandler.setBarChartData(monthChart, monthlyData);
        chartHandler.setBarChartData(dowChart, dowData);


        analysisData = dbHelper.analysis();
        chartHandler.setPieChartData(analysisData.subList(1, 4), new String[]{getString(R.string.complete_state),
                        getString(R.string.inprogress_state),
                        "Not Started"},
                new Integer[]{getActivity().getColor(R.color.completeStartColor),
                        getActivity().getColor(R.color.inprogressEndColor),
                        getActivity().getColor(R.color.incompleteEndColor)});


        charts = new ArrayList<>();
        charts.add(chart);
        charts.add(dowChart);
        charts.add(monthChart);
    }

    public void addCardUI(int cardId, String text) {
        try {
            TextView txt1 = new TextView(getContext());
            txt1.setText(text);
            txt1.setGravity(Gravity.CENTER);
            txt1.setTypeface(easyFont.getTypefaceBold());
            if (text.length() > 20)
                txt1.setTextSize(15f);
            else
                txt1.setTextSize(18f);
            txt1.setTextColor(getActivity().getColor(R.color.white));
            txt1.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            CardView card = view.findViewById(cardId);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setBackground(Utils.getGradientDrawable("#FFA814", 0.85f));
            card.addView(linearLayout);
            card.addView(txt1);
        } catch (Exception e) {
            Log.e(TAG, "addCardUI: ", e);
        }

    }

    public void showIndex(ArrayList<View> views, int index) {
        for (int i = 0; i < views.size(); i++) {
            if (i == index)
                views.get(i).setVisibility(View.VISIBLE);
            else
                views.get(i).setVisibility(View.GONE);
        }
    }

}
