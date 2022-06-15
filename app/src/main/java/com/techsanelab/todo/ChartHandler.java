package com.techsanelab.todo;

import android.app.Activity;
import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartHandler {

    private EasyFont easyFont;
    private PieChart chart;
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setEasyFont(EasyFont easyFont) {
        this.easyFont = easyFont;
    }

    public PieChart buildPieChart(PieChart chart, Activity activity, String title) {
        this.chart = chart;
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(8, 6, 8, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterTextTypeface(easyFont.getTypefaceBold());
        chart.setCenterTextSize(25f);
        chart.setCenterTextColor(activity.getColor(R.color.titleTextColor));
        chart.setCenterText(title);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(40f);
        chart.setTransparentCircleRadius(45f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);

        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.animateY(1000, Easing.EaseInOutQuad);
        Legend l = chart.getLegend();
        l.setEnabled(false);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(easyFont.getTypefaceBold());
        chart.setEntryLabelTextSize(13f);
        return chart;
    }

    public BarChart buildLineChart(BarChart lineChart, Activity activity, String title) {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setExtraOffsets(8, 6, 8, 5);
        // set listeners
        lineChart.setDrawGridBackground(false);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        // force pinch zoom along both axis
        lineChart.setPinchZoom(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        return lineChart;
    }

    public void setBarChartData(BarChart barChart, List<Integer> data) {
        ArrayList<BarEntry> values = new ArrayList<>();
        BarDataSet set1;
        for (int i = 0; i < data.size(); i++) {
            int val = data.get(i);
            values.add(new BarEntry(i, val, null));
        }

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new BarDataSet(values, "DataSet 1");
        }
        set1.setDrawIcons(false);

        // text size of values
        set1.setValueTextSize(9f);

        for (int i = 0; i < data.size(); i++) {
            set1.setGradientColor(activity.getColor(R.color.analysisColorDark), activity.getColor(R.color.analysisColor));
        }

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData barData = new BarData(dataSets);
        barData.setValueTextSize(10f);
        barData.setValueTypeface(easyFont.getTypeface());
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
    }

    public void setPieChartData(List<Integer> data, String[] labels, Integer[] colors) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        PieDataSet dataSet = new PieDataSet(entries, "Events Results");
        dataSet.setColors((ArrayList<Integer>) null); // Clear colors

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) != 0) {
                entries.add(new PieEntry((float) (data.get(i)),
                        labels[i],
                        null));
                dataSet.addColor(colors[i]);
            }
        }

        dataSet.setSliceSpace(15f);

        /*
         * You can add icons to each part.
         * */
//        dataSet.setDrawIcons(true);
//        dataSet.setIconsOffset(new MPPointF(0, 40));

        dataSet.setSelectionShift(5f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTypeface(easyFont.getTypeface());
        chart.setData(pieData);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

}
