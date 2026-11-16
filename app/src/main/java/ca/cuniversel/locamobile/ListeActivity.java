package ca.cuniversel.locamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeActivity extends AppCompatActivity {

    private EditText champVille;
    private Button boutonChercher;
    private Button boutonConnexion;
    private Button boutonAjouter;
    private TextView messageErreur;
    private LinearLayout conteneurLogements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        champVille = findViewById(R.id.champVille);
        boutonChercher = findViewById(R.id.boutonChercher);
        boutonConnexion = findViewById(R.id.boutonConnexion);
        boutonAjouter = findViewById(R.id.boutonAjouter);
        messageErreur = findViewById(R.id.messageErreur);
        conteneurLogements = findViewById(R.id.conteneurLogements);

        boutonConnexion.setOnClickListener(v -> {
            Intent intent = new Intent(ListeActivity.this, ConnexionActivity.class);
            startActivity(intent);
        });

        boutonAjouter.setOnClickListener(v -> {
            Intent intent = new Intent(ListeActivity.this, AjoutLogementActivity.class);
            startActivity(intent);
        });

        boutonChercher.setOnClickListener(v -> effectuerRecherche());
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerLogements();
    }

    private void chargerLogements() {
        Log.d("LocaMobile", "1");
        ClientApi.get().listerLogements().enqueue(new Callback<List<Logement>>() {
            @Override
            public void onResponse(Call<List<Logement>> call, Response<List<Logement>> response) {
                Log.d("LocaMobile", "3");
                int n = response.body() != null ? response.body().size() : 0;
                Log.d("LocaMobile", "code " + response.code() + " - " + n + " logements");
                if (response.isSuccessful() && response.body() != null) {
                    afficher(response.body());
                } else {
                    afficherErreur("Erreur HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Logement>> call, Throwable t) {
                Log.d("LocaMobile", "Échec : " + t.getMessage());
                afficherErreur("Serveur injoignable : " + t.getMessage());
            }
        });
        Log.d("LocaMobile", "2");
    }

    private void effectuerRecherche() {
        String ville = champVille.getText().toString().trim();
        Call<List<Logement>> call = ville.isEmpty()
                ? ClientApi.get().listerLogements()
                : ClientApi.get().chercherParVille(ville);

        call.enqueue(new Callback<List<Logement>>() {
            @Override
            public void onResponse(Call<List<Logement>> call, Response<List<Logement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        afficherErreur("Aucun logement à " + ville + ".");
                    } else {
                        afficher(response.body());
                    }
                } else {
                    afficherErreur("Erreur HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Logement>> call, Throwable t) {
                afficherErreur("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void afficher(List<Logement> logements) {
        messageErreur.setVisibility(View.GONE);
        conteneurLogements.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Logement l : logements) {
            View carte = inflater.inflate(R.layout.item_logement, conteneurLogements, false);
            TextView titre = carte.findViewById(R.id.carteTitre);
            TextView ville = carte.findViewById(R.id.carteVille);
            TextView prix = carte.findViewById(R.id.cartePrix);

            titre.setText(l.titre);
            ville.setText(l.ville);
            prix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));

            carte.setOnClickListener(v -> {
                Intent intent = new Intent(ListeActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.CLE_ID, l.id);
                startActivity(intent);
            });

            conteneurLogements.addView(carte);
        }
    }

    private void afficherErreur(String me) {
        conteneurLogements.removeAllViews();
        messageErreur.setText(me);
        messageErreur.setVisibility(View.VISIBLE);
    }
}
