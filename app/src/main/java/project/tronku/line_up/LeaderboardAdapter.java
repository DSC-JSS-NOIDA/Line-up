package project.tronku.line_up;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PlayerPOJO> playerList;

    public LeaderboardAdapter (Context context, ArrayList<PlayerPOJO> list) {
        this.context = context;
        playerList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(playerList.get(position).getName());
        holder.zealid.setText(playerList.get(position).getZealId());
        holder.score.setText(playerList.get(position).getScore());
        holder.pokeball.setImageResource(getPokeballs());

    }

    private int getPokeballs() {
        Random random = new Random();
        int code = random.nextInt(4);
        switch (code) {
            case 0 : return R.drawable.pokeball_red;
            case 1 : return R.drawable.pokeball_green;
            case 2 : return R.drawable.pokeball_blue;
            case 3 : return R.drawable.pokeball_yellow;
            default: return R.drawable.pokeball_red;
        }
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView score, name, zealid;
        private ImageView pokeball;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            score = itemView.findViewById(R.id.player_score);
            name = itemView.findViewById(R.id.player_name);
            zealid = itemView.findViewById(R.id.player_zealid);
            pokeball = itemView.findViewById(R.id.pokeball);
        }
    }

    public void updateList(ArrayList<PlayerPOJO> list) {
        playerList = list;
        notifyDataSetChanged();
    }
}
