package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import ca.cuniversel.locamobile.reseau.LogementRequete;
import ca.cuniversel.locamobile.util.Session;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AjoutLogementActivity extends AppCompatActivity {

    private EditText champTitre;
    private EditText champVille;
    private EditText champPrix;
    private EditText champVoyageurs;
    private Button boutonPublier;
    private TextView resultatAjout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_logement);

        champTitre = findViewById(R.id.champTitre);
        champVille = findViewById(R.id.champVille);
        champPrix = findViewById(R.id.champPrix);
        champVoyageurs = findViewById(R.id.champVoyageurs);
        boutonPublier = findViewById(R.id.boutonPublier);
        resultatAjout = findViewById(R.id.resultatAjout);

        boutonPublier.setOnClickListener(v -> publierLogement());
    }

    private void publierLogement() {
        String titre = champTitre.getText().toString().trim();
        String ville = champVille.getText().toString().trim();
        String strPrix = champPrix.getText().toString().trim();
        String strVoyageurs = champVoyageurs.getText().toString().trim();

        if (titre.isEmpty()) {
            champTitre.setError("Titre requis");
            return;
        }
        if (ville.isEmpty()) {
            champVille.setError("Ville requise");
            return;
        }
        if (strPrix.isEmpty()) {
            champPrix.setError("Prix requis");
            return;
        }
        if (strVoyageurs.isEmpty()) {
            champVoyageurs.setError("Nombre de voyageurs requis");
            return;
        }

        double prix;
        int voyageurs;
        try {
            prix = Double.parseDouble(strPrix);
            voyageurs = Integer.parseInt(strVoyageurs);
        } catch (NumberFormatException e) {
            resultatAjout.setText("Prix ou nombre de voyageurs invalide.");
            return;
        }

        String jeton = Session.jeton(this);
        if (jeton == null) {
            resultatAjout.setText("Connectez-vous d'abord.");
            return;
        }

        LogementRequete req = new LogementRequete(titre, ville, prix, voyageurs);
        ClientApi.get().creerLogement("Bearer " + jeton, req).enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(Call<Logement> call, Response<Logement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Logement cree = response.body();
                    resultatAjout.setText("Logement publié avec succès ! ID : " + cree.id);
                } else if (response.code() == 403) {
                    resultatAjout.setText("Erreur 403 : Seul un propriétaire peut publier un logement.");
                } else if (response.code() == 401) {
                    resultatAjout.setText("Erreur 401 : Session expirée ou invalide. Veuillez vous reconnecter.");
                } else if (response.code() == 400) {
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "";
                        JsonObject json = new Gson().fromJson(err, JsonObject.class);
                        StringBuilder sb = new StringBuilder("Erreur 400 :");
                        if (json != null && json.has("champs")) {
                            for (var e : json.getAsJsonObject("champs").entrySet()) {
                                sb.append("\n").append(e.getKey()).append(" : ").append(e.getValue().getAsString());
                            }
                        }
                        resultatAjout.setText(sb.toString());
                    } catch (Exception e) {
                        resultatAjout.setText("Erreur 400");
                    }
                } else {
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "";
                        JsonObject json = new Gson().fromJson(err, JsonObject.class);
                        String msg = (json != null && json.has("message")) ? json.get("message").getAsString() : "Erreur " + response.code();
                        resultatAjout.setText("Erreur " + response.code() + " : " + msg);
                    } catch (Exception e) {
                        resultatAjout.setText("Erreur HTTP " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Logement> call, Throwable t) {
                resultatAjout.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }
}
