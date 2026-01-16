package ma.projet.cycledevie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeteoListModel extends ArrayAdapter<MeteoItem> {

    private int resource;
    public static Map<String, Integer> images = new HashMap<>();

    static {
        images.put("Clear", R.drawable.clear);
        images.put("Clouds", R.drawable.clouds);
        images.put("Rain", R.drawable.rain);
        images.put("Thunderstorm", R.drawable.thunderstorm);
    }

    public MeteoListModel(Context context, int resource, List<MeteoItem> data) {
        super(context, resource, data);
        this.resource = resource;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textViewTempMax;
        TextView textViewTempMin;
        TextView textViewPression;
        TextView textViewHumidite;
        TextView textViewDate;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View listItem = convertView;

        if (listItem == null) {
            holder = new ViewHolder();
            listItem = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            holder.imageView = listItem.findViewById(R.id.imageView);
            holder.textViewTempMax = listItem.findViewById(R.id.textViewTempMAX);
            holder.textViewTempMin = listItem.findViewById(R.id.textViewTempMin);
            holder.textViewPression = listItem.findViewById(R.id.textViewPression);
            holder.textViewHumidite = listItem.findViewById(R.id.textViewHumidite);
            holder.textViewDate = listItem.findViewById(R.id.textViewDate);
            listItem.setTag(holder);
        } else {
            holder = (ViewHolder) listItem.getTag();
        }

        MeteoItem currentItem = getItem(position);

        if (currentItem != null) {
            String key = currentItem.image;
            if (key != null && images.containsKey(key)) {
                holder.imageView.setImageResource(images.get(key));
            } else {
                // Optional: set a default image if the key is not found
                // holder.imageView.setImageResource(R.drawable.default_icon);
            }

            holder.textViewTempMax.setText(String.valueOf(currentItem.tempMax) + " C ");
            holder.textViewTempMin.setText(String.valueOf(currentItem.tempMin) + " C ");
            holder.textViewPression.setText(String.valueOf(currentItem.pression) + " hPa");
            holder.textViewHumidite.setText(String.valueOf(currentItem.humidite) + " %");
            holder.textViewDate.setText(String.valueOf(currentItem.date));
        }

        return listItem;
    }
}
