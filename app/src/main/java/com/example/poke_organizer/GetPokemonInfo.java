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

    // Agrega un parámetro para el número del Pokémon en el constructor
    public GetPokemonInfo(TextView pokenameTextView, ImageView spriteImageView) {
        this.pokenameTextView = pokenameTextView;
        this.spriteImageView = spriteImageView;
    }

    public void execute(String relativeUrl) {
        // Creo un nuevo hilo para realizar la solicitud en segundo plano
        new Thread(() -> {
            // Creo una instancia de APIClient para realizar la solicitud
            APIClient apiClient = new APIClient();
            String response = apiClient.sendGetRequest(relativeUrl);

            if (response != null) {
                try {
                    // Proceso la respuesta JSON
                    JSONObject pokemonData = new JSONObject(response);
                    String name = pokemonData.getString("name");
                    String mayusName = name.substring(0, 1).toUpperCase() + name.substring(1);

                    // Obtengo el número del Pokémon
                    int pokemonNumber = pokemonData.getInt("id");

                    // Obtengo el sprite
                    JSONObject sprites = pokemonData.getJSONObject("sprites");
                    String spriteUrl = sprites.getString("front_default");

                    // Utilizo un Handler para ejecutar la actualización en el hilo principal
                    new Handler(Looper.getMainLooper()).post(() -> {

                        // Establece el nombre completo en el TextView en el hilo principal
                        pokenameTextView.setText("#" + pokemonNumber + " " + mayusName);

                        // Utiliza Picasso (o la biblioteca de tu elección) para cargar y mostrar el sprite
                        Picasso.get().load(spriteUrl)
                                .resize(350, 350) // Establece el tamaño deseado
                                .centerInside()   // Escala la imagen para que se ajuste manteniendo su aspecto
                                .into(spriteImageView);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Maneja el caso de respuesta nula
                System.err.println("Error al realizar la solicitud a la API");
            }
        }).start(); // Inicia el hilo para ejecutar la solicitud en segundo plano
    }
}
