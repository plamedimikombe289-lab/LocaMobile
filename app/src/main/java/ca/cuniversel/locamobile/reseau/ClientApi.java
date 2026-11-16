package ca.cuniversel.locamobile.reseau;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientApi {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static LocaMobileApi instance;

    public static synchronized LocaMobileApi get() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(LocaMobileApi.class);
        }
        return instance;
    }
}
