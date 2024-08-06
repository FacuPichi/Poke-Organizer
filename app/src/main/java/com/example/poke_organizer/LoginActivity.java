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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText email = findViewById(R.id.editTextEmail);
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
                String emailText = email.getText().toString();

                if (emailText.isEmpty() ) {
                    Toast.makeText(LoginActivity.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    auth.sendSignInLinkToEmail(emailText, actionCodeSettings)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email Enviado.");
                                        FirebaseUser currentUser = auth.getCurrentUser();
                                        String uid = currentUser.getUid();

                                        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    UserData user = null;
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String id = document.getId();
                                                        Map<String, Object> data = document.getData();

                                                        // Verificar si el email coincide
                                                        if (Objects.equals(data.get("email"), emailText)) {
                                                            // Convertir datos de Firestore a UserData
                                                            user = new UserData(
                                                                    Objects.toString(data.get("nombre"), ""),
                                                                    Objects.toString(data.get("email"), "")
                                                            );
                                                            user.setLvl(((Long) data.get("lvl")).intValue());
                                                            user.setExp(((Long) data.get("exp")).intValue());
                                                            user.setLastPokemon(((Long) data.get("lastPokemon")).intValue());

                                                            // Convertir listas de String
                                                            user.setPokedex((ArrayList<String>) data.get("pokedex"));
                                                            user.setTarea((ArrayList<String>) data.get("tareas"));

                                                            Log.i("firebase firestore", "id:" + id + " Data:" + data);
                                                            break;
                                                        }
                                                    }

                                                    // Si el usuario no existe, crear un nuevo usuario
                                                    if (user == null) {
                                                        user = new UserData(emailText, emailText);
                                                        final int pokemonAleatorio = random.nextInt(max - min + 1) + min;
                                                        user.setLastPokemon(pokemonAleatorio);
                                                        user.addPokemon(String.valueOf(pokemonAleatorio));
                                                        user.setLvl(1);
                                                    }

                                                    // Guardar los datos del usuario en JSON
                                                    JsonHandler.saveJsonData(LoginActivity.this, user);

                                                    // Ir a TaskActivity
                                                    // startActivity(new Intent(LoginActivity.this, TaskActivity.class));
                                                    finish(); // Finalizar la actividad actual
                                                } else {
                                                    Log.e(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.w(TAG, "Error sending email", task.getException());
                                        Toast.makeText(LoginActivity.this, "Error al enviar el correo electrónico: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
