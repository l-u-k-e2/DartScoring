package com.ostfalia.bs.dartscoring.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ostfalia.bs.dartscoring.R;
import com.ostfalia.bs.dartscoring.database.UserDbHelper;
import com.ostfalia.bs.dartscoring.model.User;
import com.ostfalia.bs.dartscoring.fragment.recyclerView.SimpleStringRecyclerViewAdapter;

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
        //RecyclerView mit allen Usern
        rv = (RecyclerView) inflater.inflate(
                R.layout.user_choice_list, container, false);
        //Zugriff auf DB
        userDbHelper = new UserDbHelper(getActivity().getApplicationContext());
        updateList(userDbHelper.getAllUser());
        return rv;
    }

    /**
     * Aufruf wenn zur Aktivität zurückgekehrt wird
     */
    @Override
    public void onResume() {
        super.onResume();
        updateList(userDbHelper.getAllUser());
        clearCheckedPlayers();
    }

    /**
     * Befüllt RecyclerView mit allen übergebenen Usern
     * @param userList
     */
    public void updateList(List<User> userList){
        this.userList = userList;
        setupRecyclerView(rv);
    }

    /**
     * Erzeugt ein RecyclerView mit einem vertikalen LinearLayoutManager
     * Setzt einen Adapter zum Hinzufügen und manipulieren der anzuzeigenden Daten
     * @param recyclerView
     */
    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        //Adapter um Elemente hinzuzufügen -> userList
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),userList,checkedUserIds));
    }

    public List<Long> getCheckedPlayers(){
        return this.checkedUserIds;
    }

    public void clearCheckedPlayers(){
        this.checkedUserIds.clear();
    }

}
