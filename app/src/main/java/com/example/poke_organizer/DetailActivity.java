package com.example.poke_organizer;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);


        ImageView pokeSprite = findViewById(R.id.PokeSprite);
        TextView pokename = findViewById(R.id.pokename2);

        LinearLayout statsLinearLayout = findViewById(R.id.linearLay);

        // Obtener el Intent que inició esta actividad
        Intent intent = getIntent();

        // Obtener el URL de los extras del Intent
        String relativeUrl = intent.getStringExtra("POKEMON_URL");

        GetPokemonInfo getPokemonInfo = new GetPokemonInfo(pokename, pokeSprite);
        getPokemonInfo.execute(relativeUrl);

        getPokemonInfo.processStats(relativeUrl, statsLinearLayout);
    }

}

