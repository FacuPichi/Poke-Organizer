package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ProgressBar;
import java.util.Random;
import android.content.Intent;
import android.view.View;
import java.io.File;
import android.view.KeyEvent;


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
        Button perfil = findViewById(R.id.perfil);
        int min = 1;
        int max = 1010;
        UserData User1 = JsonHandler.loadJsonData(this);
        int numeroAleatorio = random.nextInt(max - min + 1) + min;
        String relativeUrl = "pokemon/"+numeroAleatorio+"/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);
        CheckBox checkBox = findViewById(R.id.customCheckBox);
        checkBox.setChecked(true); // Establecer el estado del CheckBox en true


        // Llamada para generar el archivo JSON con datos dummy
        level.append(String.valueOf(levelValue));

        String xp = String.valueOf(progressValue) +" /100 exp";
        experience.setText(xp);
        User1.setExp(progressValue);
        User1.setLvl(levelValue);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(progressValue);

        pokemonChange.setOnClickListener(view -> {
            int numeroAleatorio1 = random.nextInt(max - min + 1) + min;
            User1.setLastPokemon(numeroAleatorio1);
            JsonHandler.updateJsonData(this,User1);
            String relativeUrl1 = "pokemon/" + numeroAleatorio1 + "/";
            new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl1);

        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un nuevo intent
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);

                // Iniciar la segunda actividad
                startActivity(intent);

                // Actualizar el último Pokémon después de iniciar la segunda actividad
                User1.setLastPokemon(numeroAleatorio);
                JsonHandler.updateJsonData(MainActivity.this, User1);
            }
        });


        checkBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Verifica si la tecla presionada es Enter
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Realiza la acción que deseas al presionar Enter
                    // Por ejemplo, cambiar el estado del CheckBox
                    checkBox.setChecked(!checkBox.isChecked());
                    return true;
                }
                // Debes devolver un valor booleano en cualquier caso
                return false;
            }
        });


        }



    }



