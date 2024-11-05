package com.rahuldhanawade.www.amarproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rahuldhanawade.www.amarproject.R;
import com.rahuldhanawade.www.amarproject.activity.TeamDetails;

import java.util.ArrayList;

public class AgeTeamsAdapter extends RecyclerView.Adapter<AgeTeamsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AgeTeamsPOJO> ageTeamsPOJOS_list;

    private ArrayList<AgeTeamsPOJO> ageTeamsPOJOS_Filterlist;

    public AgeTeamsAdapter(Context context, ArrayList<AgeTeamsPOJO> ageTeamsPOJOS_list) {
        this.context = context;
        this.ageTeamsPOJOS_list = ageTeamsPOJOS_list;
        this.ageTeamsPOJOS_Filterlist = ageTeamsPOJOS_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_group_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AgeTeamsPOJO lp = ageTeamsPOJOS_Filterlist.get(position);
        holder.tv_age_group.setText(lp.getAge_group().toString().trim());
        holder.tv_team_name.setText(lp.getTeam_name().toString().trim());
        holder.tv_team_contact.setText(lp.getMobile_institude().toString().trim());
        holder.tv_team_gender.setText(lp.getGender());
    }

    @Override
    public int getItemCount() {
        return ageTeamsPOJOS_Filterlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_age_group,tv_team_name,tv_team_contact,tv_team_gender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_age_group = itemView.findViewById(R.id.tv_age_group);
            tv_team_name = itemView.findViewById(R.id.tv_team_name);
            tv_team_contact = itemView.findViewById(R.id.tv_team_contact);
            tv_team_gender = itemView.findViewById(R.id.tv_team_gender);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getLayoutPosition();
                    AgeTeamsPOJO teamsPOJO = ageTeamsPOJOS_Filterlist.get(itemPosition);
                    String team_id = teamsPOJO.getId();
                    String team_name = teamsPOJO.getTeam_name();
                    String team_gender = teamsPOJO.getGender();
                    String team_group = teamsPOJO.getAge_group();
                    Log.d("sta",""+team_id);
                    Intent i = new Intent(context, TeamDetails.class);
                    i.putExtra("team_id",team_id);
                    i.putExtra("team_name",team_name);
                    i.putExtra("team_gender",team_gender);
                    i.putExtra("team_group",team_group);
                    context.startActivity(i);
                }
            });
        }
    }


    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    ageTeamsPOJOS_Filterlist = ageTeamsPOJOS_list;
                    //mFilteredList = mArrayList;
                } else {

                    ArrayList<AgeTeamsPOJO> filteredList = new ArrayList<>();

                    for (AgeTeamsPOJO ageTeamsPOJOFilter : ageTeamsPOJOS_list) {

                        if (ageTeamsPOJOFilter.getTeam_name().toLowerCase().contains(charString)) {

                            filteredList.add(ageTeamsPOJOFilter);
                        }
                    }

                    ageTeamsPOJOS_Filterlist = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ageTeamsPOJOS_Filterlist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ageTeamsPOJOS_Filterlist = (ArrayList<AgeTeamsPOJO>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
