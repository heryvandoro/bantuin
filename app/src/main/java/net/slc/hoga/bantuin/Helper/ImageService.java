package net.slc.hoga.bantuin.Helper;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageService {
    @Multipart
    @POST("/kiiriim.php")
    Call<ResponseBody> postImage(@Part List<MultipartBody.Part> image);
}
