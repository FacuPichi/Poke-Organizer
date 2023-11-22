package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;


public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView level = findViewById(R.id.nivelUsuario);
        TextView name = findViewById(R.id.nombreUsuario);
        UserData User1 = JsonHandler.loadJsonData(this);
        ImageView pokeSprite = findViewById(R.id.PokeSprite);
        TextView pokename = findViewById(R.id.pokename2);
        Button tareas = findViewById(R.id.tareas);
        level.append(String.valueOf(User1.getLvl()));
        name.setText(User1.getNombre());

        Log.d("Sprite", "Valor de getLastPokemon activity 2: " + User1.getLastPokemon());
        String relativeUrl = "pokemon/"+User1.getLastPokemon()+"/";
        new GetPokemonInfo(pokename, pokeSprite).execute(relativeUrl);

        tareas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Crear un nuevo intent
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);

                // Iniciar la segunda actividad
                startActivity(intent);
            }
        });

    }
}