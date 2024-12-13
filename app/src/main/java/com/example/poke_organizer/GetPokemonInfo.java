package com.example.poke_organizer;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

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
                System.err.println("Error al realizar la solicitud a la API la solicitud fue:" + response);
            }
        }).start(); // Inicia el hilo para ejecutar la solicitud en segundo plano
    }

    public void processStats(String relativeUrl, LinearLayout parentLinearLayout) {
        new Thread(() -> {
            APIClient apiClient = new APIClient();
            String response = apiClient.sendGetRequest(relativeUrl);

            if (response != null) {
                try {
                    JSONObject pokemonData = new JSONObject(response);
                    JSONArray statsArray = pokemonData.getJSONArray("stats");

                    LinearLayout  containerLinearLayout1 = new LinearLayout(pokenameTextView.getContext());
                    containerLinearLayout1.setOrientation(LinearLayout.VERTICAL);
                    containerLinearLayout1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                    LinearLayout containerLinearLayout2 = new LinearLayout(pokenameTextView.getContext());
                    containerLinearLayout2.setOrientation(LinearLayout.VERTICAL);
                    containerLinearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                    for (int i = 0; i < statsArray.length(); i++) {
                        JSONObject statObject = statsArray.getJSONObject(i);
                        int baseStat = statObject.getInt("base_stat");
                        String name = statObject.getJSONObject("stat").getString("name");

                        TextView statTextView = new TextView(pokenameTextView.getContext());
                        statTextView.setText(name + ": " + baseStat);

                        // Aplica el color de texto negro
                        statTextView.setTextColor(ContextCompat.getColor(pokenameTextView.getContext(), android.R.color.black));

                        statTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        Typeface typeface = ResourcesCompat.getFont(pokenameTextView.getContext(), R.font.roboto_bold);
                        statTextView.setTypeface(typeface);

                        Drawable drawable = ContextCompat.getDrawable(pokenameTextView.getContext(), R.drawable.triangulo);
                        if (drawable != null) {
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            Drawable scaledDrawable = new BitmapDrawable(pokenameTextView.getResources(), Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, true));
                            statTextView.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, null, null);
                        }

                        // Agrega las primeras 3 estadísticas a containerLinearLayout1 y las siguientes a containerLinearLayout2
                        if (i < 3) {
                            containerLinearLayout1.addView(statTextView);
                        } else {
                            containerLinearLayout2.addView(statTextView);
                        }
                    }

                    new Handler(Looper.getMainLooper()).post(() -> {
                        parentLinearLayout.addView(containerLinearLayout1);
                        parentLinearLayout.addView(containerLinearLayout2);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Error al realizar la solicitud a la API");
            }
        }).start();
    }
    public void processTypes(String relativeUrl, LinearLayout parentLinearLayout) {
        new Thread(() -> {
            APIClient apiClient = new APIClient();
            String response = apiClient.sendGetRequest(relativeUrl);
            int widthDp = 120; // Ancho en dp
            int heightDp = 25; // Alto en dp

            if (response != null) {
                try {
                    JSONObject pokemonData = new JSONObject(response);
                    JSONArray typesArray = pokemonData.getJSONArray("types");

                    for (int i = 0; i < typesArray.length(); i++) {
                        JSONObject typeObject = typesArray.getJSONObject(i);
                        String name = typeObject.getJSONObject("type").getString("name");

                        TextView typeTextView = new TextView(pokenameTextView.getContext());

                        typeTextView.setText("Type: " + name);

                        // Aplica el color de texto negro
                        typeTextView.setTextColor(ContextCompat.getColor(pokenameTextView.getContext(), android.R.color.black));

                        typeTextView.setBackground(ContextCompat.getDrawable(pokenameTextView.getContext(), R.drawable.rounded_red_background));
                        typeTextView.setGravity(Gravity.CENTER);

                        Typeface typeface = ResourcesCompat.getFont(pokenameTextView.getContext(), R.font.roboto_bold);
                        typeTextView.setTypeface(typeface);

                        int widthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp, pokenameTextView.getContext().getResources().getDisplayMetrics());
                        int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, pokenameTextView.getContext().getResources().getDisplayMetrics());

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthPx, heightPx);
                        typeTextView.setLayoutParams(layoutParams);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            parentLinearLayout.addView(typeTextView);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Error al realizar la solicitud a la API");
            }
        }).start();
    }

}
