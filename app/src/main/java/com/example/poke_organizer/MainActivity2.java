package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView experience = findViewById(R.id.nivelUsuario);
        TextView name = findViewById(R.id.nombreUsuario);
        UserData User1 = JsonHandler.loadJsonData(this);
        ImageView pokeSprite = findViewById(R.id.PokeSprite);


        experience.append(String.valueOf(User1.getExp()));
        name.setText(User1.getNombre());
    }
}