package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {
    private TextView pokenameTextView;
    private ImageView pokeSprite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pokenameTextView = findViewById(R.id.pokename);
        pokeSprite = findViewById(R.id.PokeSprite);
   // Comienza desde 1 para obtener el primer Pokémon
            String relativeUrl = "pokemon/1/";
            new GetPokemonInfo(pokenameTextView, pokeSprite).execute(relativeUrl);



    }
}
