package com.qiscus.internship.sudutnegeri.ui.dashboard;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.qiscus.internship.sudutnegeri.R;
import com.qiscus.internship.sudutnegeri.adapter.project.ProjectAdapter;
import com.qiscus.internship.sudutnegeri.adapter.project.ProjectListener;
import com.qiscus.internship.sudutnegeri.data.model.DataProject;
import com.qiscus.internship.sudutnegeri.data.model.DataUser;
import com.qiscus.internship.sudutnegeri.ui.addproject.AddProjectActivity;
import com.qiscus.internship.sudutnegeri.ui.project.ProjectActivity;
import com.qiscus.internship.sudutnegeri.ui.recentchat.RecentChatActivity;
import com.qiscus.internship.sudutnegeri.util.CircleTransform;
import com.qiscus.internship.sudutnegeri.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSudut extends Fragment implements ProjectListener, DashboardView {

    private DashboardPresenter dashboardPresenter;
    private ProjectAdapter projectAdapter;
    private List<DataProject> dataProjectList;
    private DataUser dataUser;
    Button btnSudutCreate;
    FloatingActionButton fabSudutChat;
    ImageView ivSudutNoData, ivItemP;
    RecyclerView rvSudut;
    SearchView searchViewSudut;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvSudutNoData;

    public static FragmentSudut newInstance() {
        // Required empty public constructor
        return new FragmentSudut();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sudut, container, false);
        initPresenter();
        initView(view);
        initDataIntent();
        initDataPresenter();
        initAdapter();

        refresh();
        postProject();
        search();
        chat();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDataPresenter();

        refresh();
        chat();
    }

    private void initPresenter() {
        dashboardPresenter = new DashboardPresenter(this);
    }

    private void initAdapter() {
        projectAdapter = new ProjectAdapter(dataProjectList);
        initDataAdapter();
    }

    private void initDataAdapter(){
        projectAdapter.setAdapterListener(this);
        rvSudut.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSudut.setAdapter(projectAdapter);
    }

    private void initView(View view) {
        btnSudutCreate = view.findViewById(R.id.btnSudutCreate);
        ivSudutNoData = view.findViewById(R.id.ivSudutNoData);
        fabSudutChat = view.findViewById(R.id.fabSudutChat);
        rvSudut = view.findViewById(R.id.rvSudut);
        ivItemP = view.findViewById(R.id.ivItemP);
        searchViewSudut = getActivity().findViewById(R.id.svDashboardSudut);
        swipeRefreshLayout = view.findViewById(R.id.srlSudut);
        tvSudutNoData = view.findViewById(R.id.tvSudutNoData);
    }

    private void initDataPresenter() {
        dashboardPresenter.getProjectByUser(dataUser);
    }

    private void initDataIntent() {
        dataUser = getActivity().getIntent().getParcelableExtra(Constant.Extra.DATA);
    }

    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(()-> initDataPresenter());
    }

    private void postProject() {
        btnSudutCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProjectActivity.class);
            intent.putExtra(Constant.Extra.User, dataUser);
            intent.putExtra(Constant.Extra.param, "negeri");
            startActivity(intent);
        });
    }

    private void search() {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchViewSudut.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchViewSudut.setMaxWidth(Integer.MAX_VALUE);
        int id = searchViewSudut.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchViewSudut.findViewById(id);
        textView.setTextColor(Color.WHITE);

        searchViewSudut.setOnClickListener(v -> {searchViewSudut.setIconified(false);});

        searchViewSudut.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                projectAdapter.getFilter().filter(query);
                initDataAdapter();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                projectAdapter.getFilter().filter(newText);
                initDataAdapter();
                return false;
            }
        });
    }

    private void chat(){
        fabSudutChat.setOnClickListener(v -> {
            Intent recentChat = new Intent(getActivity(), RecentChatActivity.class);
            recentChat.putExtra(Constant.Extra.DATA, dataUser);
            startActivity(recentChat);
        });
    }

    @Override
    public void onProjectClick(DataProject dataProject) {
        Intent intent = new Intent(getActivity(), ProjectActivity.class);
        intent.putExtra(Constant.Extra.Project, dataProject);
        intent.putExtra(Constant.Extra.param, "sudut");
        startActivity(intent);
    }

    @Override
    public void displayImgProject(ImageView imgProject, DataProject dataProject) {
        Picasso.with(getActivity())
                .load(dataProject.getPhoto())
                .transform(new CircleTransform())
                .into(imgProject);
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void successLogout() {}

    @Override
    public void successShowProjectVerify(List<DataProject> dataProject) {}

    @Override
    public void successShowProjectByUser(List<DataProject> dataProjects) {
        swipeRefreshLayout.setRefreshing(false);
        dataProjectList = dataProjects;
        if (dataProjects.size()>0){
            ivSudutNoData.setVisibility(View.GONE);
            tvSudutNoData.setVisibility(View.GONE);
        }
        initAdapter();
    }

    @Override
    public void failed(String s) {

    }

}
