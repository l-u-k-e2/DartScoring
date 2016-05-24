package com.ostfalia.bs.dartscoring.fragment.recyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.bumptech.glide.Glide;
import com.ostfalia.bs.dartscoring.R;
import com.ostfalia.bs.dartscoring.UserStatisticActivity;
import com.ostfalia.bs.dartscoring.model.User;
import java.util.List;

/**
 * Created by lukas on 24.05.2016.
 */
public class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>{

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<User> valuesUser;
    private List<Long> checkedUserIds;

    //Constructor
    public SimpleStringRecyclerViewAdapter(Context context, List<User> user, List<Long> checkedUserIds) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        valuesUser = user;
        this.checkedUserIds = checkedUserIds;
    }

    //Wird jedesmal aufgerufen wenn der RecyclerView ein ViewHolder für das nächste Item benötigt
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        view.setBackgroundResource(mBackground);
        //list_item wird dem ViewHolder über den Construktor übergeben
        return new ViewHolder(view);
    }

    //Aufruf zur Darstellung des ViewHolders
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBoundString = valuesUser.get(position).getVorname();
        holder.mBoundID = valuesUser.get(position).getId();
        //Befüllen des Vornamens
        holder.mTextView.setText(valuesUser.get(position).getVorname());
        //Anhängen des Userobjekts an die Textview
        holder.mTextView.setTag(R.id.userId,valuesUser.get(position));
        //Setzen eines Listeners der ausgewählte User der checkedUserIds hinzufügt/löscht
        holder.mCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                long id = ((User)holder.mTextView.getTag(R.id.userId)).getId();
                boolean checked = false;
                for (int i = 0; i < checkedUserIds.size(); i++) {
                    //Prüfung ob Feld vorher checked war
                    if (checkedUserIds.get(i) == id){
                        checked = true;
                        checkedUserIds.remove(i);
                    }
                }
                if (checked == false){
                    checkedUserIds.add(id);
                }
            }
        });
        //Bild vor Namen setzen
        Glide.with(holder.mImageView.getContext())
                .load(R.drawable.dart_board)
                .fitCenter()
                .into(holder.mImageView);

        //Listener: Bei Click öffnen der Statistik des zugehörigen Users
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserStatisticActivity.class);
                intent.putExtra(UserStatisticActivity.EXTRA_NAME, holder.mBoundString);
                intent.putExtra(UserStatisticActivity.USER_ID, holder.mBoundID);

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return valuesUser.size();
    }

}
