package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import java.util.Random;
import android.content.Intent;
import android.view.View;
import java.io.File;

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
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button pokemonChange = findViewById(R.id.pokemonChange);
        TextView experience = findViewById(R.id.exp);
        Button botonIrAMain2 = findViewById(R.id.perfil);
        int min = 1;
        int max = 1010;

        int numeroAleatorio = random.nextInt(max - min + 1) + min;

        String relativeUrl = "pokemon/"+numeroAleatorio+"/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);

        // Llamada para generar el archivo JSON con datos dummy
       level.append(String.valueOf(levelValue));

        String xp = String.valueOf(progressValue) +" /100 exp";
        experience.setText(xp);

        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(progressValue);

        pokemonChange.setOnClickListener(view -> {
            int numeroAleatorio1 = random.nextInt(max - min + 1) + min;
            String relativeUrl1 = "pokemon/" + numeroAleatorio1 + "/";
            new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl1);

        });

        botonIrAMain2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                // Agrega el número aleatorio como extra al Intent
                intent.putExtra("numeroAleatorio1", numeroAleatorio);
                // Inicia la segunda actividad
                startActivity(intent);
            }
        });


        }



    }



