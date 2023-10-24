package com.example.poke_organizer;

import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;
import com.squareup.picasso.Picasso;

public class GetPokemonInfo {
    private final TextView pokenameTextView;
    private final ImageView spriteImageView;

    public GetPokemonInfo(TextView pokenameTextView, ImageView spriteImageView) {
        this.pokenameTextView = pokenameTextView;
        this.spriteImageView = spriteImageView;
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

                        // Obten el sprite
                        JSONObject sprites = pokemonData.getJSONObject("sprites");
                        String spriteUrl = sprites.getString("front_default");

                        // Utilizo un Handler para ejecutar la actualización en el hilo principal
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // Establece el nombre en el TextView en el hilo principal
                                pokenameTextView.setText(name);

                                // Utiliza Picasso (o la biblioteca de tu elección) para cargar y mostrar el sprite
                                Picasso.get().load(spriteUrl)
                                        .resize(350, 350) // Establece el tamaño deseado
                                        .centerInside()   // Escala la imagen para que se ajuste manteniendo su aspecto
                                        .into(spriteImageView);
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
