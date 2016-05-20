package com.ostfalia.bs.dartscoring.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.ostfalia.bs.dartscoring.R;
import com.ostfalia.bs.dartscoring.UserStatisticActivity;
import com.ostfalia.bs.dartscoring.database.UserDbHelper;
import com.ostfalia.bs.dartscoring.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukas on 29.04.2016.
 */
public class UserchoiceFragment extends Fragment {

    private List<User> userList = new ArrayList<>();
    private List<Long> checkedUserIds = new ArrayList<>();
    private UserDbHelper userDbHelper;
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //RecyclerView
        rv = (RecyclerView) inflater.inflate(
                R.layout.user_choice_list, container, false);
        userDbHelper = new UserDbHelper(getActivity().getApplicationContext());
        updateList(userDbHelper.getAllUser());
        setupRecyclerView(rv);
        return rv;
    }

    public void updateList(List<User> userList){
        this.userList = userList;
        setupRecyclerView(rv);
    }

    public List<Long> getCheckedPlayers(){
        return this.checkedUserIds;
    }

    public void clearCheckedPlayers(){
        this.checkedUserIds.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Test","Tst");
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        //Adapter um Elemente hinzuzufügen
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),userList,checkedUserIds));
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

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

        //Hier wird die View beschrieben, die in der RecyclerView als Element dargestellt werden soll
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;
            public long mBoundID;

            public final View mView;
            public final TextView mTextView;
            public final ImageView mImageView;
            public final CheckBox mCheckBox;

            public ViewHolder(View view) {
                super(view);
                //HIer kommt die list_item view an
                mView = view;
                //initialisierung der Elemente in List_item
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mCheckBox = (CheckBox) view.findViewById(android.R.id.checkbox);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        //Befüllung bei intialisierung der des ViewHolder
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            //List_item wird dem Viewholder übergeben
            return new ViewHolder(view);
        }

        //Darstellung der Daten
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = valuesUser.get(position).getVorname();
            holder.mBoundID = valuesUser.get(position).getId();

            holder.mTextView.setText(valuesUser.get(position).getVorname());
            holder.mTextView.setTag(R.id.userId,valuesUser.get(position));
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
            Glide.with(holder.mImageView.getContext())
                    .load(R.drawable.dart_board)
                    .fitCenter()
                    .into(holder.mImageView);

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
}
