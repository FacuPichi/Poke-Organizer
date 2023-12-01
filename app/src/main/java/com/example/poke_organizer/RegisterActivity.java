package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        EditText name = findViewById(R.id.editTextName);
        EditText email = findViewById(R.id.editTextEmail);
        EditText emailconf = findViewById(R.id.editTextEmail2);
        Button register = findViewById(R.id.register);
        int min = 1;
        int max = 1010;
        Random random = new Random();

        register.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeColorDark));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name.getText().toString();
                String emailText = email.getText().toString();
                String emailConfirmation = emailconf.getText().toString();

                if (!emailText.equals(emailConfirmation)) {
                    // Mensaje de alerta de que los correos electrónicos no coinciden
                    Toast.makeText(RegisterActivity.this, "Los correos electrónicos no coinciden", Toast.LENGTH_SHORT).show();
                } else {
                    // Guardar y enviar a TaskActivity
                    UserData user = new UserData(nameText, emailText);
                    final int pokemonAleatorio = random.nextInt(max - min + 1) + min;
                    user.setLastPokemon(pokemonAleatorio);
                    user.addPokemon(String.valueOf(pokemonAleatorio));
                    JsonHandler.saveJsonData(RegisterActivity.this, user);
                    user.setLvl(1);
                    // Ir a TaskActivity
                    startActivity(new Intent(RegisterActivity.this, TaskActivity.class));
                    finish(); // Finalizar la actividad actual
                }
            }
        });


}

}
