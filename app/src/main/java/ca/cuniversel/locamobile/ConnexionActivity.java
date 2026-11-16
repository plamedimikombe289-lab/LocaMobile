package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.ConnexionRequete;
import ca.cuniversel.locamobile.reseau.Profil;
import ca.cuniversel.locamobile.reseau.TokenReponse;
import ca.cuniversel.locamobile.util.Session;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {

    private EditText champCourriel;
    private EditText champMotDePasse;
    private Button boutonSeConnecter;
    private TextView resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        boutonSeConnecter = findViewById(R.id.boutonSeConnecter);
        resultat = findViewById(R.id.resultat);

        boutonSeConnecter.setOnClickListener(v -> tenterConnexion());

        String jetonExistant = Session.jeton(this);
        if (jetonExistant != null) {
            chargerProfil("Bearer " + jetonExistant);
        }
    }

    private void tenterConnexion() {
        String courriel = champCourriel.getText().toString().trim();
        String mdp = champMotDePasse.getText().toString();

        if (courriel.isEmpty()) {
            champCourriel.setError("Courriel requis");
            return;
        }
        if (mdp.isEmpty()) {
            champMotDePasse.setError("Mot de passe requis");
            return;
        }

        ClientApi.get().connecter(new ConnexionRequete(courriel, mdp)).enqueue(new Callback<TokenReponse>() {
            @Override
            public void onResponse(Call<TokenReponse> call, Response<TokenReponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TokenReponse tr = response.body();
                    Session.enregistrer(ConnexionActivity.this, tr.token, tr.nom);
                    chargerProfil("Bearer " + tr.token);
                } else {
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        JsonObject json = new Gson().fromJson(errorJson, JsonObject.class);
                        String msg = (json != null && json.has("message")) ? json.get("message").getAsString() : "Accès refusé";
                        resultat.setText("Erreur " + response.code() + " : " + msg);
                    } catch (Exception e) {
                        resultat.setText("Erreur " + response.code() + " : Connexion échouée");
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenReponse> call, Throwable t) {
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void chargerProfil(String enteteAuth) {
        ClientApi.get().monProfil(enteteAuth).enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(Call<Profil> call, Response<Profil> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profil p = response.body();
                    resultat.setText("Connecté(e) avec succès !\nNom : " + p.nom + "\nRôle : " + p.role
                            + "\nCourriel : " + p.courriel + "\nCode HTTP : " + response.code());
                } else {
                    resultat.setText("Erreur profil HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Profil> call, Throwable t) {
                resultat.setText("Erreur réseau : " + t.getMessage());
            }
        });
    }
}
