package ca.cuniversel.locamobile.reseau;

public class LogementRequete {
    public String titre;
    public String ville;
    public double prixParNuit;
    public int nbVoyageurs;

    public LogementRequete(String titre, String ville, double prixParNuit, int nbVoyageurs) {
        this.titre = titre;
        this.ville = ville;
        this.prixParNuit = prixParNuit;
        this.nbVoyageurs = nbVoyageurs;
    }
}
