package com.add.vpn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.add.vpn.R;

import java.util.List;

public class LogAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;

    public LogAdapter(@NonNull Context context, int resource, @NonNull List<String> logList) {
        super(context, resource, logList);
        this.context = context;
        this.resource = resource;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.itemTextView);
            // Инициализация других подэлементов, если есть

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Заполнение данных элемента списка
        String item = getItem(position);
        viewHolder.textView.setText(item);
        // Установка данных в другие подэлементы, если есть

        return convertView;
    }

    // ViewHolder класс
    private static class ViewHolder {
        TextView textView;
        // Дополнительные подэлементы вашего элемента списка, если есть
    }

}
