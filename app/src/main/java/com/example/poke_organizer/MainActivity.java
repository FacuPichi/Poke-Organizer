package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView pokenameTextView;

        ImageView pokeSprite;

        pokenameTextView = findViewById(R.id.pokename);
        pokeSprite = findViewById(R.id.PokeSprite);
   // Comienza desde 1 para obtener el primer Pokémon
            String relativeUrl = "pokemon/3/";
            new GetPokemonInfo(pokenameTextView, pokeSprite).execute(relativeUrl);



    }
}
