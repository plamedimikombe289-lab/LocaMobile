package ca.cuniversel.locamobile.reseau;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LocaMobileApi {

    @GET("api/logements")
    Call<List<Logement>> listerLogements();

    @GET("api/logements/{id}")
    Call<Logement> trouverLogement(@Path("id") int id);

    @GET("api/logements")
    Call<List<Logement>> chercherParVille(@Query("ville") String ville);

    @POST("api/auth/connexion")
    Call<TokenReponse> connecter(@Body ConnexionRequete requete);

    @GET("api/moi")
    Call<Profil> monProfil(@Header("Authorization") String entete);

    @POST("api/logements")
    Call<Logement> creerLogement(@Header("Authorization") String entete, @Body LogementRequete requete);
}
