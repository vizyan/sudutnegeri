package com.qiscus.internship.sudutnegeri.ui.dashboard;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.qiscus.internship.sudutnegeri.R;
import com.qiscus.internship.sudutnegeri.adapter.project.ProjectAdapter;
import com.qiscus.internship.sudutnegeri.adapter.project.ProjectListener;
import com.qiscus.internship.sudutnegeri.data.model.DataProject;
import com.qiscus.internship.sudutnegeri.ui.project.ProjectActivity;
import com.qiscus.internship.sudutnegeri.util.CircleTransform;
import com.qiscus.internship.sudutnegeri.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNegeri extends Fragment implements DashboardView, ProjectListener {

    private DashboardPresenter dashboardPresenter;
    private ProjectAdapter projectAdapter;
    ImageView ivNegeriNoData, ivItemP;
    List<DataProject> dataProjectList;
    RecyclerView rvNegeri;
    SearchView searchViewNegeri;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvNegeriNoData;

    public static FragmentNegeri newInstance() {
        // Required empty public constructor
        return new FragmentNegeri();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_negeri, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniPresenter();
        initView();
        initDataPresenter();
        dashboardPresenter.getProjectByVerify();

        refresh();
        search();
    }

    @Override
    public void onResume() {
        super.onResume();
        initDataPresenter();
        dashboardPresenter.getProjectByVerify();

        refresh();
    }

    private void iniPresenter() {
        dashboardPresenter = new DashboardPresenter(this);
    }

    private void initView() {
        rvNegeri = getActivity().findViewById(R.id.rvNegeri);
        swipeRefreshLayout = getActivity().findViewById(R.id.srlNegeri);
        searchViewNegeri = getActivity().findViewById(R.id.svDashboardNegeri);
        ivNegeriNoData = getActivity().findViewById(R.id.ivNegeriNoData);
        ivItemP = getActivity().findViewById(R.id.ivItemP);
        tvNegeriNoData = getActivity().findViewById(R.id.tvNegeriNoData);
    }

    private void initAdapter() {
        projectAdapter.setAdapterListener(this);
        rvNegeri.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNegeri.setAdapter(projectAdapter);
    }

    private void initDataPresenter() {
        dashboardPresenter.getProjectByVerify();
    }

    private void refresh(){
        swipeRefreshLayout.setOnRefreshListener(() -> initDataPresenter());
    }

    private void search(){
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchViewNegeri.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchViewNegeri.setMaxWidth(Integer.MAX_VALUE);
        int id = searchViewNegeri.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchViewNegeri.findViewById(id);
        textView.setTextColor(Color.WHITE);

        searchViewNegeri.setOnClickListener(v -> {searchViewNegeri.setIconified(false);});
        searchViewNegeri.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                projectAdapter.getFilter().filter(query);
                initAdapter();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                projectAdapter.getFilter().filter(newText);
                initAdapter();
                return false;
            }
        });
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
    public void successLogout() {

    }

    @Override
    public void successShowProjectVerify(List<DataProject> dataProject) {
        projectAdapter = new ProjectAdapter(dataProjectList);
        swipeRefreshLayout.setRefreshing(false);
        dataProjectList = dataProject;
        if (dataProject.size()>0){
            ivNegeriNoData.setVisibility(View.GONE);
            tvNegeriNoData.setVisibility(View.GONE);
        }
        initAdapter();
    }

    @Override
    public void successShowProjectByUser(List<DataProject> dataProjectList) {}

    @Override
    public void failed(String s) {

    }

    @Override
    public void onProjectClick(DataProject dataProject) {
        Intent intent = new Intent(getActivity(), ProjectActivity.class);
        intent.putExtra(Constant.Extra.Project, dataProject);
        intent.putExtra(Constant.Extra.param, "negeri");
        startActivity(intent);
    }

    @Override
    public void displayImgProject(ImageView imgProject, DataProject dataProject) {
        Picasso.with(getActivity())
                .load(dataProject.getPhoto())
                .transform(new CircleTransform())
                .into(imgProject);
    }
}
