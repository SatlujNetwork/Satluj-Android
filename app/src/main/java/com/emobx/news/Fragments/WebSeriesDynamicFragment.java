package com.emobx.news.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.BookmarksAdapter;
import com.emobx.news.Adapters.CastAdapter;
import com.emobx.news.Adapters.EpisodesAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.CommentModel;
import com.emobx.news.Model.WebSeriesDetailModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WebSeriesDynamicFragment extends Fragment implements AdapterItemClickListner {

    private View view;
    private int position = 0;
    private EpisodesAdapter episodesAdapter;
    private RecyclerView rv_episodes, rv_cast;
    private ApiInterface apiInterface;
    private ProgressBar progress;
    private NewsPreferences preferences;
    private WebSeriesDetailModel.Data webSeriesDetailData;
    private CastAdapter castAdapter;
    private LinearLayout ll_more;
    private TextView tv_title, tv_genre, tv_director, tv_executive_producer,
            tv_music, tv_makeup, tv_director_of_photography;

    public static Fragment newInstance() {
        return new WebSeriesDynamicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_web_series_dynamic, container, false);

        init();

        return view;
    }

    private void init() {
        position = getArguments().getInt("position");
        webSeriesDetailData = (WebSeriesDetailModel.Data) getArguments().getSerializable("seriesData");

        preferences = new NewsPreferences(getActivity());
        rv_episodes = view.findViewById(R.id.rv_episodes);
        progress = view.findViewById(R.id.progress);
        rv_cast = view.findViewById(R.id.rv_cast);
        ll_more = view.findViewById(R.id.ll_more);

        tv_title = view.findViewById(R.id.tv_title);
        tv_genre = view.findViewById(R.id.tv_genre);
        tv_director = view.findViewById(R.id.tv_director);
        tv_executive_producer = view.findViewById(R.id.tv_executive_producer);
        tv_music = view.findViewById(R.id.tv_music);
        tv_makeup = view.findViewById(R.id.tv_makeup);
        tv_director_of_photography = view.findViewById(R.id.tv_director_of_photography);

        setScreens();
    }

    private void setScreens() {
        switch (position) {
            case 0:
                rv_episodes.setVisibility(View.VISIBLE);
                ll_more.setVisibility(View.GONE);

                if (webSeriesDetailData != null && webSeriesDetailData.getGetEpisodes() != null) {
                    setEpisodesAdapter();
                }
                break;
            case 1:
                rv_episodes.setVisibility(View.GONE);
                ll_more.setVisibility(View.VISIBLE);

                if (webSeriesDetailData != null && webSeriesDetailData.getCastMembers() != null) {
                    setData();
                    setCastAdapter();
                }
                break;
        }
    }

    private void setData() {
        if (webSeriesDetailData.getTitle() != null)
            tv_title.setText(webSeriesDetailData.getTitle() + "");

        if (webSeriesDetailData.getGeneres() != null)
            tv_genre.setText(webSeriesDetailData.getGeneres() + "");

        if (webSeriesDetailData.getDirector() != null)
            tv_director.setText(webSeriesDetailData.getDirector() + "");

        if (webSeriesDetailData.getExecutiveProducer() != null)
            tv_executive_producer.setText(webSeriesDetailData.getExecutiveProducer() + "");

        if (webSeriesDetailData.getMusic() != null)
            tv_music.setText(webSeriesDetailData.getMusic() + "");

        if (webSeriesDetailData.getMakeup() != null)
            tv_makeup.setText(webSeriesDetailData.getMakeup() + "");

        if (webSeriesDetailData.getDirectorOfPhotography() != null)
            tv_director_of_photography.setText(webSeriesDetailData.getDirectorOfPhotography() + "");

    }

    private void setEpisodesAdapter() {
        if (episodesAdapter == null) {
            episodesAdapter = new EpisodesAdapter(getActivity(), webSeriesDetailData.getGetEpisodes(), this);
            rv_episodes.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv_episodes.setAdapter(episodesAdapter);
        } else
            episodesAdapter.updateList(webSeriesDetailData.getGetEpisodes());
    }

    private void setCastAdapter() {
        if (castAdapter == null) {
            castAdapter = new CastAdapter(getActivity(), webSeriesDetailData.getCastMembers(), this);
            rv_cast.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rv_cast.setAdapter(castAdapter);
        } else
            castAdapter.updateList(webSeriesDetailData.getCastMembers());
    }

    @Override
    public void OnItemClick(String type, int position) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            view.findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.black));
            ((TextView) view.findViewById(R.id.tv_title_heading)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) view.findViewById(R.id.tv_genre_heading)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) view.findViewById(R.id.tv_director_heading)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) view.findViewById(R.id.tv_executive_producer_heading)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) view.findViewById(R.id.tv_music_heading)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) view.findViewById(R.id.tv_makeup_heading)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) view.findViewById(R.id.tv_director_of_photography_heading)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) view.findViewById(R.id.tv_cast_heading)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            rv_episodes.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            view.findViewById(R.id.rl_main).setBackground(getResources().getDrawable(R.color.white));
            ((TextView) view.findViewById(R.id.tv_title_heading)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) view.findViewById(R.id.tv_genre_heading)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) view.findViewById(R.id.tv_director_heading)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) view.findViewById(R.id.tv_executive_producer_heading)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) view.findViewById(R.id.tv_music_heading)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) view.findViewById(R.id.tv_makeup_heading)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) view.findViewById(R.id.tv_director_of_photography_heading)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) view.findViewById(R.id.tv_cast_heading)).setTextColor(getResources().getColor(R.color.appBlackDark));
            rv_episodes.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }
}