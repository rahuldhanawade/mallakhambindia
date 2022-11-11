package com.rahuldhanawade.www.amarproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rahuldhanawade.www.amarproject.R;
import com.rahuldhanawade.www.amarproject.activity.ScoreForm;
import com.rahuldhanawade.www.amarproject.activity.TeamDetails;

import java.util.ArrayList;

public class TeamPlayersAdapter extends RecyclerView.Adapter<TeamPlayersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TeamPlayersPOJO> teamPlayersPOJOArrayList;

    public TeamPlayersAdapter(Context context, ArrayList<TeamPlayersPOJO> teamPlayersPOJOArrayList) {
        this.context = context;
        this.teamPlayersPOJOArrayList = teamPlayersPOJOArrayList;
    }

    @NonNull
    @Override
    public TeamPlayersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamPlayersAdapter.ViewHolder holder, int position) {
        TeamPlayersPOJO lp = teamPlayersPOJOArrayList.get(position);
        holder.tv_plyr_age.setText(lp.getAge().toString().trim());
        holder.tv_plyr_name.setText(lp.getParticipant_name().toString().trim());
    }

    @Override
    public int getItemCount() {
        return teamPlayersPOJOArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_plyr_age,tv_plyr_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_plyr_age = itemView.findViewById(R.id.tv_plyr_age);
            tv_plyr_name = itemView.findViewById(R.id.tv_plyr_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getLayoutPosition();
                    Intent i = new Intent(context, ScoreForm.class);
                    i.putExtra("player_id",teamPlayersPOJOArrayList.get(itemPosition).getId());
                    i.putExtra("player_name",teamPlayersPOJOArrayList.get(itemPosition).getParticipant_name());
                    i.putExtra("team_name",teamPlayersPOJOArrayList.get(itemPosition).getTeam_name());
                    i.putExtra("player_age",teamPlayersPOJOArrayList.get(itemPosition).getAge());
                    i.putExtra("player_gender",teamPlayersPOJOArrayList.get(itemPosition).getGender());
                    i.putExtra("player_group",teamPlayersPOJOArrayList.get(itemPosition).getGroup());
                    context.startActivity(i);
                }
            });
        }
    }
}
