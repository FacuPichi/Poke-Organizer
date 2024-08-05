package com.example.poke_organizer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        auth = FirebaseAuth.getInstance();

        EditText name = findViewById(R.id.editTextName);
        EditText email = findViewById(R.id.editTextEmail);
        EditText emailconf = findViewById(R.id.editTextEmail2);
        Button register = findViewById(R.id.register);
        int min = 1;
        int max = 1010;
        Random random = new Random();
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://pokeorganizer.page.link")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.example.poke_organizer",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();
        register.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeColorDark));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name.getText().toString();
                String emailText = email.getText().toString();
                String emailConfirmation = emailconf.getText().toString();

                if (nameText.isEmpty() || emailText.isEmpty() || emailConfirmation.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else if (!emailText.equals(emailConfirmation)) {
                    Toast.makeText(RegisterActivity.this, "Los correos electrónicos no coinciden", Toast.LENGTH_SHORT).show();
                } else {
                    auth.sendSignInLinkToEmail(emailText, actionCodeSettings)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");

                                        // Guardar y enviar a TaskActivity
                                        UserData user = new UserData(nameText, emailText);
                                        final int pokemonAleatorio = random.nextInt(max - min + 1) + min;
                                        user.setLastPokemon(pokemonAleatorio);
                                        user.addPokemon(String.valueOf(pokemonAleatorio));
                                        JsonHandler.saveJsonData(RegisterActivity.this, user);
                                        user.setLvl(1);
                                        // Ir a TaskActivity
                                        //startActivity(new Intent(RegisterActivity.this, TaskActivity.class));
                                        finish(); // Finalizar la actividad actual
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error sending email", e);
                                Toast.makeText(RegisterActivity.this, "Error al enviar el correo electrónico: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }
}