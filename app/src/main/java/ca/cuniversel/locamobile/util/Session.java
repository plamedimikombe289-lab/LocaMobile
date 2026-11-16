package ca.cuniversel.locamobile.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private static final String FICHIER = "session";
    private static final String CLE_JETON = "jeton";
    private static final String CLE_NOM = "nom";

    public static void enregistrer(Context context, String jeton, String nom) {
        SharedPreferences sp = context.getSharedPreferences(FICHIER, Context.MODE_PRIVATE);
        sp.edit()
                .putString(CLE_JETON, jeton)
                .putString(CLE_NOM, nom)
                .apply();
    }

    public static String jeton(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FICHIER, Context.MODE_PRIVATE);
        return sp.getString(CLE_JETON, null);
    }

    public static String nom(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FICHIER, Context.MODE_PRIVATE);
        return sp.getString(CLE_NOM, null);
    }

    public static void effacer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FICHIER, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}
