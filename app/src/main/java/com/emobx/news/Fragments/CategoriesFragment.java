package com.emobx.news.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.Activities.EntertainmentActivity;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Adapters.BookmarksAdapter;
import com.emobx.news.Adapters.CategoriesAdapter;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.CategoryListModel;
import com.emobx.news.Model.CategoryWiseNewsListModel;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.DownloadFile;
import com.emobx.news.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;


public class CategoriesFragment extends Fragment implements View.OnClickListener, AdapterItemClickListner {

    private View view;
    private Activity activity;
    private NewsPreferences preferences;
    private ApiInterface apiInterface;
    private Realm realm;
    private RecyclerView rv_categories;
    private CategoriesAdapter categoriesAdapter;
    private RealmResults<CategoryListModel> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_categories, container, false);


        return view;
    }

    private void init() {
        activity = getActivity();
        realm = Utils.getRealmInstance(activity);
        preferences = new NewsPreferences(activity);

        rv_categories = view.findViewById(R.id.rv_categories);
    }


    private void listener() {
        view.findViewById(R.id.iv_entertainment).setOnClickListener(this);
        view.findViewById(R.id.iv_travel).setOnClickListener(this);
        view.findViewById(R.id.iv_science).setOnClickListener(this);
        view.findViewById(R.id.iv_sports).setOnClickListener(this);
        view.findViewById(R.id.iv_technology).setOnClickListener(this);
        view.findViewById(R.id.iv_politics).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_entertainment:
                activity.startActivity(new Intent(activity, EntertainmentActivity.class).putExtra("category", "Entertainment"));
                break;
            case R.id.iv_travel:
                activity.startActivity(new Intent(activity, EntertainmentActivity.class).putExtra("category", "Travel"));
                break;
            case R.id.iv_science:
                activity.startActivity(new Intent(activity, EntertainmentActivity.class).putExtra("category", "Science"));
                break;
            case R.id.iv_sports:
                activity.startActivity(new Intent(activity, EntertainmentActivity.class).putExtra("category", "Sports"));
                break;
            case R.id.iv_technology:
                activity.startActivity(new Intent(activity, EntertainmentActivity.class).putExtra("category", "Technology"));
                break;
            case R.id.iv_politics:
                activity.startActivity(new Intent(activity, EntertainmentActivity.class).putExtra("category", "Politics"));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        listener();
        callCategoryListApi();

        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            view.findViewById(R.id.sv_main).setBackgroundColor(activity.getResources().getColor(R.color.black));
        } else {
            view.findViewById(R.id.sv_main).setBackgroundColor(activity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (realm != null && !realm.isClosed())
            realm.close();
    }

    private void callCategoryListApi() {
        if (!Utils.checkNetworkAvailability(activity)) {
            setCategoryAdapter();
            return;
        }
        Utils.showProgressDialog(activity);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(activity);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("login_token", preferences.getUserData().getLoginToken());
        map.put("device_type", Constants.DEVICE_TYPE);
        map.put("device_token", preferences.getDeviceToken());

        Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                .flatMap(s -> {
                    Observable<CategoryListModel> couponsObservable
                            = s.getCategoryList(map).subscribeOn(Schedulers.io());
                    return Observable.concatArray(couponsObservable);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessCategoryList, this::handleError);

    }

    private void OnSuccessCategoryList(CategoryListModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                CategoryListModel categoryListModel = modelResult;
                setRealmCategoryData(categoryListModel);

                ArrayList<String> id = new ArrayList<>();
                ArrayList<String> url = new ArrayList<>();
                if (categoryListModel.getData() != null) {
                    for (int i = 0; i < categoryListModel.getData().size(); i++) {

                        id.add(categoryListModel.getData().get(i).getId() + "");

                        if (categoryListModel.getData().get(i).getCoverImage().startsWith("http"))
                            url.add(categoryListModel.getData().get(i).getCoverImage());
                        else
                            url.add(Constants.BASE_URl + "/" + categoryListModel.getData().get(i).getCoverImage());

                    }
                    new DownloadFile(id, url);
                }
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(activity);
            }/* else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
       */
        } /*else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    */
    }

    private void setRealmCategoryData(CategoryListModel categoryListModel) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<CategoryListModel> data = realm.where(CategoryListModel.class).findAll();
                if (data != null)
                    data.deleteAllFromRealm();
                categoryListModel.setId(1);
                realm.copyToRealmOrUpdate(categoryListModel);
            }
        });


        setCategoryAdapter();
    }

    private void setCategoryAdapter() {

        data = realm.where(CategoryListModel.class).findAll();
        if (data.size() > 0)
            if (data.get(0).getData() != null && data.get(0).getData().size() > 0)
                if (categoriesAdapter == null) {
                    categoriesAdapter = new CategoriesAdapter(activity, data.get(0).getData(), this);
                    rv_categories.setLayoutManager(new GridLayoutManager(activity, 2));
                    rv_categories.setAdapter(categoriesAdapter);
                } else
                    categoriesAdapter.updateList(data.get(0).getData());
            else
                callCategoryListApi();
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(activity, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnItemClick(String type, int position) {
        switch (type) {
            case "categoryClick":
                activity.startActivity(new Intent(activity, EntertainmentActivity.class)
                        .putExtra("categoryId", data.get(0).getData().get(position).getId() + ""));
                break;
        }
    }
}