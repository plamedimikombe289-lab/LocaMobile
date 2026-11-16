package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    public static final String CLE_ID = "LOGEMENT_ID";

    private TextView detailTitre;
    private TextView detailVille;
    private TextView detailPrix;
    private TextView detailVoyageurs;
    private TextView detailProprietaire;
    private TextView etat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailTitre = findViewById(R.id.detailTitre);
        detailVille = findViewById(R.id.detailVille);
        detailPrix = findViewById(R.id.detailPrix);
        detailVoyageurs = findViewById(R.id.detailVoyageurs);
        detailProprietaire = findViewById(R.id.detailProprietaire);
        etat = findViewById(R.id.etat);

        int id = getIntent().getIntExtra(CLE_ID, -1);
        chargerFiche(id);
    }

    private void chargerFiche(int id) {
        ClientApi.get().trouverLogement(id).enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(Call<Logement> call, Response<Logement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    afficherFiche(response.body());
                } else if (response.code() == 404) {
                    afficherEtat("Ce logement n'existe plus (404).");
                } else {
                    afficherEtat("Erreur HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Logement> call, Throwable t) {
                afficherEtat("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void afficherFiche(Logement l) {
        etat.setVisibility(View.GONE);
        detailTitre.setText(l.titre);
        detailVille.setText("Ville : " + l.ville);
        detailPrix.setText(String.format(Locale.CANADA_FRENCH, "Prix : %.2f $ / nuit", l.prixParNuit));
        detailVoyageurs.setText("Capacité : " + l.nbVoyageurs + " voyageurs");
        detailProprietaire.setText("Hôte : " + (l.proprietaireNom != null ? l.proprietaireNom : "N/A"));
    }

    private void afficherEtat(String me) {
        detailTitre.setText("");
        detailVille.setText("");
        detailPrix.setText("");
        detailVoyageurs.setText("");
        detailProprietaire.setText("");
        etat.setText(me);
        etat.setVisibility(View.VISIBLE);
    }
}
