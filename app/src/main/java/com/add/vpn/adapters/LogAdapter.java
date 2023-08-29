package com.add.vpn.adapters;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.add.vpn.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    private final List<String> logList;

    public LogAdapter(List<String> logList) {
        this.logList = logList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = logList.get(position);
        holder.textView.setText(item);
        Drawable img;
        if (item.contains("↑")){
            img = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.power_up_icon);
        } else if (item.contains("↓")) {
            img = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.power_down_icon);
        }else {
            img = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.warning_icon);
        }
        holder.imageView.setImageDrawable(img);
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemLogView);
            imageView = itemView.findViewById(R.id.actionImage);
            // Инициализация других элементов, если есть
        }
    }
}
