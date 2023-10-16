//package com.add.vpn.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.add.vpn.R;
//import com.add.vpn.view.ChartView;
//
//import java.util.ArrayList;
//
//public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {
//    public ArrayList<ChartView> getChartViewList() {
//        return chartViewList;
//    }
//
//    public void setChartViewList(ArrayList<ChartView> chartViewList) {
//        this.chartViewList = chartViewList;
//    }
//
//    private ArrayList<ChartView> chartViewList;
//
//    public ChartAdapter(ArrayList<ChartView> chartViewList) {
//        this.chartViewList = chartViewList;
//    }
//
//    @NonNull
//    @Override
//    public ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_item_layout, parent, false);
//        return new ChartViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return chartViewList.size();
//    }
//
//    public static class ChartViewHolder extends RecyclerView.ViewHolder {
//        ChartView chartView;
//        public ChartViewHolder(@NonNull View itemView) {
//            super(itemView);
//            chartView = itemView.findViewById(R.id.chart_view); // Замените R.id.chart_view на ID вашего ChartView в макете
//        }
//    }
//}
