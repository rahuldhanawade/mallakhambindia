package com.rahuldhanawade.www.amarproject.Adapters;

import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.checkNullExcHandler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rahuldhanawade.www.amarproject.R;
import com.rahuldhanawade.www.amarproject.activity.ScoreForm;
import com.rahuldhanawade.www.amarproject.activity.ScoreResultActivity;
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
        holder.tv_sr_score.setText(checkNullExcHandler(lp.getSr_judge_score().toString().trim()));
        holder.tv_j1_score.setText(checkNullExcHandler(lp.getJ1_score().toString().trim()));
        holder.tv_j2_score.setText(checkNullExcHandler(lp.getJ2_score().toString().trim()));
        holder.tv_j3_score.setText(checkNullExcHandler(lp.getJ3_score().toString().trim()));
        holder.tv_j4_score.setText(checkNullExcHandler(lp.getJ4_score().toString().trim()));
        String setScore_id = lp.getScore_flag().toString();
        if(setScore_id.equals("1")){
           holder.iv_checked.setVisibility(View.VISIBLE);
        }else{
            holder.iv_checked.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return teamPlayersPOJOArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_plyr_age,tv_plyr_name,tv_sr_score,tv_j1_score,tv_j2_score,tv_j3_score,tv_j4_score;
        ImageView iv_checked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_plyr_age = itemView.findViewById(R.id.tv_plyr_age);
            tv_plyr_name = itemView.findViewById(R.id.tv_plyr_name);
            tv_sr_score = itemView.findViewById(R.id.tv_sr_score);
            tv_j1_score = itemView.findViewById(R.id.tv_j1_score);
            tv_j2_score = itemView.findViewById(R.id.tv_j2_score);
            tv_j3_score = itemView.findViewById(R.id.tv_j3_score);
            tv_j4_score = itemView.findViewById(R.id.tv_j4_score);
            iv_checked = itemView.findViewById(R.id.iv_checked);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getLayoutPosition();
                    String Score_flag = teamPlayersPOJOArrayList.get(itemPosition).getScore_flag();
                    Intent i;
                    if(Score_flag.equals("1")){
                        i = new Intent(context, ScoreResultActivity.class);
                    }else{
                        i = new Intent(context, ScoreForm.class);
                    }
                    i.putExtra("score_id",teamPlayersPOJOArrayList.get(itemPosition).getScore_id());
                    i.putExtra("player_id",teamPlayersPOJOArrayList.get(itemPosition).getId());
                    i.putExtra("player_name",teamPlayersPOJOArrayList.get(itemPosition).getParticipant_name());
                    i.putExtra("team_name",teamPlayersPOJOArrayList.get(itemPosition).getTeam_name());
                    i.putExtra("team_id",teamPlayersPOJOArrayList.get(itemPosition).getTeam_id());
                    i.putExtra("player_age",teamPlayersPOJOArrayList.get(itemPosition).getAge());
                    i.putExtra("player_gender",teamPlayersPOJOArrayList.get(itemPosition).getGender());
                    i.putExtra("player_group",teamPlayersPOJOArrayList.get(itemPosition).getGroup());
                    context.startActivity(i);
                }
            });
        }
    }
}
