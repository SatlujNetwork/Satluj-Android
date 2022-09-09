package com.emobx.news.API;

import com.emobx.news.Model.AddComment;
import com.emobx.news.Model.BannerModel;
import com.emobx.news.Model.BookmarkData;
import com.emobx.news.Model.CategoryListModel;
import com.emobx.news.Model.CategoryWiseNewsListModel;
import com.emobx.news.Model.CommentModel;
import com.emobx.news.Model.ForceUpdateModel;
import com.emobx.news.Model.LiveStreamModel;
import com.emobx.news.Model.LoginModel;
import com.emobx.news.Model.LogoutModel;
import com.emobx.news.Model.NewsBookmarkModel;
import com.emobx.news.Model.NewsLikeModel;
import com.emobx.news.Model.NewsListModel;
import com.emobx.news.Model.NewsListModelDatum;
import com.emobx.news.Model.NewsSliderListModel;
import com.emobx.news.Model.NotificationCountModel;
import com.emobx.news.Model.NotificationListModel;
import com.emobx.news.Model.NotificationSettingModel;
import com.emobx.news.Model.PurchaseDetailModel;
import com.emobx.news.Model.SingleNewModel;
import com.emobx.news.Model.VideoWebSeriesModel;
import com.emobx.news.Model.WebSeriesDetailModel;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/register_newUser")
    Observable<LoginModel> callRegistration(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/login")
    Observable<LoginModel> callLogin(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/social_login")
    Observable<LoginModel> callSocialLogin(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/logout")
    Observable<LogoutModel> callLogout(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/live_streaming")
    Observable<LiveStreamModel> callLiveStream(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/forget_password")
    Observable<LogoutModel> callForgot(@FieldMap Map<String, String> params);

    @Multipart
    @POST("api/user_info_edit")
    Observable<LoginModel> callUpdateProfile(@Part("id") RequestBody id,
                                             @Part("name") RequestBody name,
                                             @Part MultipartBody.Part image,
                                             @Part("login_token") RequestBody login_token,
                                             @Part("device_type") RequestBody device_type,
                                             @Part("device_token") RequestBody device_token);

    @FormUrlEncoded
    @POST("api/user_info_edit")
    Observable<LoginModel> callUpdateProfile(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/notified_news_list")
    Observable<NotificationListModel> getNotificationList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/news_list_view")
    Observable<NewsListModel> getNewsList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/news_list_view")
    Observable<NewsListModel> getVideoNewsList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/news_list_view")
    Observable<CategoryWiseNewsListModel> getCategoryWiseNewsList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/news_list_view_both")
    Observable<VideoWebSeriesModel> getVideoList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/comments_specific_news_view")
    Observable<CommentModel> getComment(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/comments_add")
    Observable<AddComment> addComment(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("api/comment_is_spam")
    Observable<LogoutModel> callFlagComment(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/comments_specific_news_view")
    Observable<LogoutModel> callReportComment(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/comment_is_delete")
    Observable<LogoutModel> callDeleteComment(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/push_like")
    Observable<NewsLikeModel> callNewsLike(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/push_bookmark")
    Observable<NewsBookmarkModel> callNewBookmark(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/otp_send_to_mail")
    Observable<LogoutModel> callSendOtp(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/otp_verification_email")
    Observable<LoginModel> callEmailVeri(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/news_slider_view")
    Observable<NewsSliderListModel> getNewSliderList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/category_list")
    Observable<CategoryListModel> getCategoryList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/news_list_search")
    Observable<NewsListModel> searchNews(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/bookmark_list_view")
    Observable<BookmarkData> getBookmarkList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/notification_mute_unmute")
    Observable<NotificationSettingModel> setNotificationSetting(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/skip_login")
    Observable<LoginModel> callSkipLogin(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/save_contact_us_detail")
    Observable<LoginModel> callContactUs(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/unread_notification_count")
    Observable<NotificationCountModel> callNotificationCount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/news_single_view")
    Observable<SingleNewModel> callSingleNews(@FieldMap Map<String, String> params);

    @GET
    Observable<WebSeriesDetailModel> callWebSeriesDetail(@Url String url,
                                                         @Header("login-token") String login_token);

    @FormUrlEncoded
    @POST("api/storeSeenTime")
    Observable<WebSeriesDetailModel> storeSeenTime(@FieldMap Map<String, String> params,
                                                   @Header("login-token") String login_token);

    @FormUrlEncoded
    @POST("api/user_delete")
    Observable<WebSeriesDetailModel> deleteAccount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/store_PaymentDetails")
    Observable<WebSeriesDetailModel> storePaymentDetails(@FieldMap Map<String, String> params,
                                                         @Header("login-token") String login_token);

    @GET("api/list_PaymentDetails")
    Observable<PurchaseDetailModel> getPurchaseToken(@Header("login-token") String login_token);

    @GET
    Observable<ForceUpdateModel> getForceUpdate(@Url String url);

    @GET("api/get_banner")
    Observable<BannerModel> getBanner();

}
