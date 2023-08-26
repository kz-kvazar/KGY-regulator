package com.add.vpn.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.R;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private final List<ReportItem> reportItems;

    public ReportAdapter(List<ReportItem> reportItems) {
        this.reportItems = reportItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportItem currentItem = reportItems.get(position);
        holder.timeTextView.setText(currentItem.getTime());
        String ch4 = currentItem.getCh4() + "%";
        holder.ch4TextView.setText(ch4);
        String consumption = currentItem.getConsumption() + "м3";
        holder.consumptionTextView.setText(consumption);
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public TextView ch4TextView;
        public TextView consumptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.itemReportTime);
            ch4TextView = itemView.findViewById(R.id.itemReportCH4);
            consumptionTextView = itemView.findViewById(R.id.itemReportConsumption);
        }
    }
}