package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import java.util.Random;
import android.view.View;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random random = new Random();
        int levelValue = 10;
        int progressValue = 35;
        TextView pokename = findViewById(R.id.pokename);
        TextView level = findViewById(R.id.level);
        ImageView pokeSprite = findViewById(R.id.PokeSprite);
        ProgressBar progressBar = findViewById(R.id.progressBar); // Asegúrate de que el ID sea el correcto en tu XML.
        Button pokemonChange = findViewById(R.id.pokemonChange);
        // Define el rango de números aleatorios que deseas
        int min = 1; // Valor mínimo
        int max = 1010; // Valor máximo

        // Genera un número aleatorio en el rango [min, max]
        int numeroAleatorio = random.nextInt(max - min + 1) + min;

        // numero pokedex para obtener el Pokémon
        String relativeUrl = "pokemon/"+numeroAleatorio+"/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);

        // establece el numero de nivel
        level.append(String.valueOf(levelValue));

        // Establece el progreso de la ProgressBar
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(progressValue);

        pokemonChange.setOnClickListener(view -> {
            int numeroAleatorio1 = random.nextInt(max - min + 1) + min;
            String relativeUrl1 = "pokemon/" + numeroAleatorio1 + "/";
            new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl1);
        });
    }
}
