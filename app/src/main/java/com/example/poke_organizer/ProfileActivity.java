package com.example.poke_organizer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;

import java.util.ArrayList;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ProgressBar progressBar = findViewById(R.id.profileProgressBar);
        TextView experience = findViewById(R.id.profileExp);
        TextView level = findViewById(R.id.nivelUsuario);
        TextView name = findViewById(R.id.nombreUsuario);
        UserData User = JsonHandler.loadJsonData(this);
        ImageView pokeSprite = findViewById(R.id.PokeSprite);
        TextView pokename = findViewById(R.id.pokename2);
        Button tareas = findViewById(R.id.tareas);
        level.append(String.valueOf(User.getLvl()));
        name.setText(User.getNombre());

        TableLayout tableLayout = findViewById(R.id.pokedex);

        // Obtener la lista de Pokémon
        ArrayList<String> pokedex = User.getPokedex();

        // Definir el número máximo de imágenes por fila
        int maxImagesPerRow = 4; // Cambia este valor según tus necesidades

        // Crear una nueva fila
        TableRow tableRow = new TableRow(this);

        // Iterar sobre la lista de Pokémon
        // Iterar sobre la lista de Pokémon
        for (int i = 0; i < pokedex.size(); i++) {
            // Crear una nueva imagen
            ImageView imageView = new ImageView(this);

            // Ejecutar GetPokemonInfo para cargar la imagen del Pokémon
            String relativeUrl = "pokemon/" + pokedex.get(i) + "/";

            // Dentro de tu bucle for, después de crear la ImageView
            int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear un AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("¿Qué quieres hacer?");
                    builder.setMessage("¿Quieres que este Pokémon pase a ser tu nuevo compañero o quieres ver sus estadísticas?");

                    // Opción para hacer que el Pokémon sea el nuevo compañero
                    // Opción para hacer que el Pokémon sea el nuevo compañero
                    builder.setPositiveButton("Hacer compañero", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cambiar el valor de lastPokemon
                            User.setLastPokemon(Integer.parseInt(pokedex.get(finalI))); // Asume que los elementos de pokedex son los números de los Pokémon
                            // Guardar los cambios en User
                            JsonHandler.saveJsonData(ProfileActivity.this, User);

                            // Actualizar la imagen y el nombre del Pokémon en la interfaz de usuario
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pokename.setText(pokedex.get(finalI)); // Asume que pokename es un TextView que muestra el nombre del Pokémon
                                    new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl); // Asume que pokeSprite es un ImageView que muestra la imagen del Pokémon
                                }
                            });
                        }
                    });

                    // Opción para ver las estadísticas del Pokémon
                    builder.setNegativeButton("Ver estadísticas", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                // Crear un nuevo Intent para DetailActivity
                                Intent intent = new Intent(ProfileActivity.this, DetailActivity.class);
                                // Pasar la URL relativa del Pokémon a DetailActivity
                                intent.putExtra("POKEMON_URL", relativeUrl);
                                // Iniciar DetailActivity
                                startActivity(intent);
                            } catch (Exception e) {
                                // Mostrar el error en el log
                                Log.e("ProfileActivity", "Error al iniciar DetailActivity", e);
                            }
                        }
                    });

                    // Mostrar el AlertDialog
                    builder.show();
                }
            });

            // Añadir la imagen a la fila
            tableRow.addView(imageView);

            // Crear una nueva instancia de GetPokemonInfo
            TextView dummyTextView = new TextView(this); // TextView dummy, no se mostrará
            GetPokemonInfo getPokemonInfo = new GetPokemonInfo(dummyTextView, imageView);

            // Ejecutar GetPokemonInfo
            getPokemonInfo.execute(relativeUrl);

            // Si se ha alcanzado el número máximo de imágenes por fila o es el último Pokémon, añadir la fila al TableLayout
            if ((i + 1) % maxImagesPerRow == 0 || i == pokedex.size() - 1) {
                tableLayout.addView(tableRow);
                tableRow = new TableRow(this); // Crear una nueva fila para la siguiente línea de imágenes
            }
        }

        Log.d("Sprite", "Valor de getLastPokemon activity 2: " + User.getLastPokemon());
        String relativeUrl = "pokemon/"+User.getLastPokemon()+"/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.bringToFront();
        progressBar.setProgress(User.getExp());
        ColorStateList colorStateList = ColorStateList.valueOf(Color.YELLOW);
        progressBar.setProgressTintList(colorStateList);
        tareas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Crear un nuevo intent
                Intent intent = new Intent(ProfileActivity.this, TaskActivity.class);

                // Iniciar la segunda actividad
                startActivity(intent);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                experience.setText(User.getExp() +" /100 exp");
            }
        });



    }
    @Override
    protected void onResume() {
        super.onResume();
        UserData User = JsonHandler.loadJsonData(this);
        ImageView pokeSprite = findViewById(R.id.PokeSprite);
        TextView pokename = findViewById(R.id.pokename2);
        String relativeUrl = "pokemon/"+User.getLastPokemon()+"/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);
    }
}

