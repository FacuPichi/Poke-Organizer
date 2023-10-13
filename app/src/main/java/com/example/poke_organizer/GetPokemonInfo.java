package com.example.poke_organizer;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPokemonInfo {
    private final TextView pokenameTextView;

    public GetPokemonInfo(TextView pokenameTextView) {
        this.pokenameTextView = pokenameTextView;
    }

    public void execute(String relativeUrl) {
        // Creo un nuevo hilo para realizar la solicitud en segundo plano
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Creo una instancia de APIClient para realizar la solicitud
                APIClient apiClient = new APIClient();
                String response = apiClient.sendGetRequest(relativeUrl);

                if (response != null) {
                    try {
                        // Procesa la respuesta JSON
                        JSONObject pokemonData = new JSONObject(response);
                        String name = pokemonData.getString("name");

                        // Utilizo un Handler para ejecutar la actualización en el hilo principal
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // Establecemos el nombre en el TextView en el hilo principal
                                pokenameTextView.setText(name);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Maneja el caso de respuesta nula
                    System.err.println("Error al realizar la solicitud a la API");
                }
            }
        }).start(); // Inicia el hilo para ejecutar la solicitud en segundo plano
    }
}
