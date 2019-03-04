package project.tronku.line_up;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PlayerPOJO> list;

    public RouteAdapter (Context context, ArrayList<PlayerPOJO> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.route_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.zealid.setText(list.get(position).getZealId());
        holder.lat.setText("Lat: " + list.get(position).getLat());
        holder.lng.setText("Lng: " + list.get(position).getLng());
        if (position + 1 == list.size())
            holder.hanger.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, zealid, lat, lng;
        private View hanger;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.teammate_name);
            zealid = itemView.findViewById(R.id.teammate_zealid);
            hanger = itemView.findViewById(R.id.hanger);
            lat = itemView.findViewById(R.id.latitude);
            lng = itemView.findViewById(R.id.longitude);
        }
    }

    public void updateList(ArrayList<PlayerPOJO> list) {
        this.list = list;
    }
}
