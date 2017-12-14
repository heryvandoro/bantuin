package edu.bluejack17_1.bantuin.Services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TokenService {
    @FormUrlEncoded
    @POST("/users")
    Call<ResponseBody> postToken(@Field("user_id") String user_id, @Field("token") String token);
}

