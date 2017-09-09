package com.example.fho10.flicks.networking;

import com.example.fho10.flicks.models.Data;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FlicksClient {
    static FlicksService service;
    public static final String BASE_URL =  "https://api.themoviedb.org/3/movie/";
    public static final String API_KEY = "1fecdea9ec965aa7437713388bfd8418";

    public interface FlicksService {
        @GET("now_playing/")
        Call<Data> listNowPlaying(@Query("page") int page);
    }

    public static FlicksService getService() {
        if (service == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            HttpUrl originalHttpUrl = original.url();

                            HttpUrl url = originalHttpUrl.newBuilder()
                                    .addQueryParameter("api_key", API_KEY)
                                    .build();

                            Request.Builder requestBuilder = original.newBuilder()
                                    .url(url);

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(FlicksService.class);
        }

        return service;
    }
}
