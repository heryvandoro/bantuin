package edu.bluejack17_1.bantuin.Notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import edu.bluejack17_1.bantuin.Helper.Config;
import edu.bluejack17_1.bantuin.Model.ActiveUser;
import edu.bluejack17_1.bantuin.Services.ImageService;
import edu.bluejack17_1.bantuin.Services.TokenService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceToken {
    public static void send() {
        String user_id = ActiveUser.getUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        TokenService service = new Retrofit.Builder().baseUrl(Config.BASE_URL_TOKEN)
                .build().create(TokenService.class);

        retrofit2.Call<okhttp3.ResponseBody> req = service.postToken(user_id, token);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    Log.w("resToken", response.body().string());
                }catch (Exception e){}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
