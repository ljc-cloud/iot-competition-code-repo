package com.example.g21;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyAdapter extends ArrayAdapter<Parking> {
    private final int resourceId;
    public MyAdapter(@NonNull Context context, int resource, @NonNull List<Parking> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Parking item = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = View.inflate(getContext(), resourceId, parent);
            viewHolder = new ViewHolder();
            viewHolder.idText = view.findViewById(R.id.id_text);
            viewHolder.openText = view.findViewById(R.id.open_text);
            viewHolder.closeText = view.findViewById(R.id.close_text);
            viewHolder.continueText = view.findViewById(R.id.continue_text);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.idText.setText(item.getId());
        viewHolder.openText.setText(item.getOpenTime());
        viewHolder.closeText.setText(item.getCloseTime());
        String s = item.getContinueTime() + "s";
        viewHolder.continueText.setText(s);
        return view;
    }

    static class ViewHolder {
        TextView idText;
        TextView openText;
        TextView closeText;
        TextView continueText;
    }
}
