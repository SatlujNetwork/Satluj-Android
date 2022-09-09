package com.emobx.news.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emobx.news.API.ApiInterface;
import com.emobx.news.API.RetrofitFactory;
import com.emobx.news.AdapterListener.AdapterItemClickListner;
import com.emobx.news.Database.NewsPreferences;
import com.emobx.news.Model.LoginModel;
import com.emobx.news.Permission.CheckPermissions;
import com.emobx.news.R;
import com.emobx.news.Utils.Constants;
import com.emobx.news.Utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener, AdapterItemClickListner {

    private NewsPreferences preferences;
    private EditText et_name;
    private TextView tv_update;
    private ApiInterface apiInterface;
    private ImageView iv_profile;
    private File outputFile;
    private String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();
        listener();
        setData();
    }

    private void setData() {
        if (preferences.getUserData() != null && preferences.getUserData().getData() != null) {
            if (preferences.getUserData().getData().getName() != null && !preferences.getUserData().getData().getName().isEmpty())
                et_name.setText(preferences.getUserData().getData().getName());
            if (preferences.getUserData().getData().getPic() != null && !preferences.getUserData().getData().getPic().isEmpty()) {
                imageUrl = preferences.getUserData().getData().getPic();
                if (imageUrl.startsWith("http"))
                    Picasso.with(this).load(imageUrl).into(iv_profile);
                else
                    Picasso.with(this).load(Constants.BASE_URl + "/" + imageUrl).into(iv_profile);
            }
        }
    }

    private void init() {
        preferences = new NewsPreferences(this);

        et_name = findViewById(R.id.et_name);
        tv_update = findViewById(R.id.tv_update);
        findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("Edit Profile");
        findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        iv_profile = findViewById(R.id.iv_profile);
    }

    private void listener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        tv_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_profile:
                if (new CheckPermissions().checkPermissions(this)) {
                    ImagePicker.with(this)
                            .crop()
                            .cropSquare()
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .start();
                }
                break;
            case R.id.tv_update:
                if (et_name.getText().toString().isEmpty())
                    Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
                else if (outputFile == null && imageUrl.isEmpty())
                    Toast.makeText(this, "Please select profile image", Toast.LENGTH_SHORT).show();
                else
                    callUpdateProfileApi();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                outputFile = new File(uri.getPath());

                if (outputFile != null && !outputFile.getAbsolutePath().isEmpty())
                    if (outputFile.exists()) {
                        Picasso.with(this).load(outputFile).into(iv_profile);
                    }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
        }
    }

    @Override
    public void OnItemClick(String type, int position) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.googleAnalytics(this);
        if (preferences.getTheme().equalsIgnoreCase("dark")) {
            Utils.clearLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.black));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.black));
            et_name.setBackgroundColor(getResources().getColor(R.color.black));
            et_name.setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackxLight));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_white));
        } else {
            Utils.setLightStatusBar(findViewById(android.R.id.content).getRootView(), this);
            findViewById(R.id.rl_main).setBackgroundColor(getResources().getColor(R.color.white));
            findViewById(R.id.rl_top_view).setBackgroundColor(getResources().getColor(R.color.white));
            et_name.setBackgroundColor(getResources().getColor(R.color.white));
            et_name.setTextColor(getResources().getColor(R.color.appBlackDark));
            ((TextView) findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.appBlackDark));
            ((ImageView) findViewById(R.id.iv_back)).setImageDrawable(getResources().getDrawable(R.drawable.back_black));
        }
    }

    private void callUpdateProfileApi() {
        if (!Utils.checkNetworkAvailability(this)) {
            return;
        }
        Utils.showProgressDialog(this);

        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);
        Utils.showProgressDialog(this);
        apiInterface = RetrofitFactory.getInstance().create(ApiInterface.class);

        if (outputFile != null) {
            RequestBody id = RequestBody.create(MediaType.parse("text/plain"), preferences.getUserData().getData().getId() + "");
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), et_name.getText().toString());

            RequestBody rbImage = RequestBody.create(MediaType.parse("*/*"), outputFile);
            MultipartBody.Part mpImage = MultipartBody.Part.createFormData("image", outputFile.getName(), rbImage);

            RequestBody login_token = RequestBody.create(MediaType.parse("text/plain"), preferences.getUserData().getLoginToken());
            RequestBody device_type = RequestBody.create(MediaType.parse("text/plain"), Constants.DEVICE_TYPE);
            RequestBody device_token = RequestBody.create(MediaType.parse("text/plain"), preferences.getDeviceToken());

            Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                    .flatMap(s -> {
                        Observable<LoginModel> couponsObservable
                                = s.callUpdateProfile(id, name, mpImage, login_token, device_type, device_token).subscribeOn(Schedulers.io());
                        return Observable.concatArray(couponsObservable);
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessRegistration, this::handleError);
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", preferences.getUserData().getData().getId() + "");
            map.put("name", et_name.getText().toString());
            map.put("login_token", preferences.getUserData().getLoginToken());
            map.put("device_type", Constants.DEVICE_TYPE);
            map.put("device_token", preferences.getDeviceToken());

            Observable.just(apiInterface).subscribeOn(Schedulers.computation())
                    .flatMap(s -> {
                        Observable<LoginModel> couponsObservable
                                = s.callUpdateProfile(map).subscribeOn(Schedulers.io());
                        return Observable.concatArray(couponsObservable);
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(this::OnSuccessRegistration, this::handleError);
        }
    }

    private void OnSuccessRegistration(LoginModel modelResult) {
        Utils.hideProgressDialog();
        if (modelResult != null && modelResult.getErrorCode() != null) {
            if (modelResult.getErrorCode().equalsIgnoreCase("200")) {
                preferences.setUserData(modelResult);
                sendBroadcast(new Intent("UpdateProfile"));
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else if (modelResult.getErrorCode().equalsIgnoreCase("700")) {
                Utils.sessionExpired(this);
            } else
                Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, modelResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable throwable) {
        Utils.hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}