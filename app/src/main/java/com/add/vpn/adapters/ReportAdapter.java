//package com.add.vpn.adapters;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.add.vpn.R;
//import com.add.vpn.roomDB.ReportItem;
//
//import java.util.List;
//
//public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
//    private List<ReportItem> reportItems;
//
//    public ReportAdapter(List<ReportItem> reportItems) {
//        this.reportItems = reportItems;
//    }
//    public void setReportItems(List<ReportItem> reportItems){
//        this.reportItems = reportItems;
//    }
//    public boolean isEmpty(){
//        return reportItems.isEmpty();
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ReportItem currentItem = reportItems.get(position);
//        holder.timeTextView.setText(currentItem.getTimeString());
//        String ch4 = currentItem.getCH4() + "%";
//        holder.ch4TextView.setText(ch4);
//        String consumption = currentItem.getConsumption() + "M3";
//        holder.consumptionTextView.setText(consumption);
//        String constPower = currentItem.getConstPower() + "kW";
//        holder.constPower.setText(constPower);
//    }
//
//    @Override
//    public int getItemCount() {
//        return reportItems.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView constPower;
//        public final TextView timeTextView;
//        public final TextView ch4TextView;
//        public final TextView consumptionTextView;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            timeTextView = itemView.findViewById(R.id.itemReportTime);
//            ch4TextView = itemView.findViewById(R.id.itemReportCH4);
//            consumptionTextView = itemView.findViewById(R.id.itemReportConsumption);
//            constPower = itemView.findViewById(R.id.constPower);
//        }
//    }
//    public void addItem(ReportItem reportItem){
//        reportItems.add(0,reportItem);
//    }
//}
