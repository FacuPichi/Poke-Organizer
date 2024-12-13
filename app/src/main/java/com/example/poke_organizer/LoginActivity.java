package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText email, password;
    private Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editPassword);
        registerButton = findViewById(R.id.register);
        loginButton = findViewById(R.id.loginbutton);

        setButtonListeners();
    }

    private void setButtonListeners() {
        registerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeColorDark));

        // Redirigir a RegisterActivity para registro de nuevo usuario
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Login con correo y contraseña
        loginButton.setOnClickListener(view -> handleLogin());
    }

    private void handleLogin() {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (emailText.isEmpty() || passwordText.isEmpty()) {
            showToast("Por favor, rellene todos los campos");
            return;
        }

        // Autenticación con Firebase (correo y contraseña)
        db.collection("users")
                .whereEqualTo("email", emailText)  // Buscar por email
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String storedPassword = document.getString("password");

                            if (passwordText.equals(storedPassword)) {
                                // Login exitoso
                                Log.d("Login", "Usuario autenticado con Firestore");

                                // Crear un objeto UserData con los datos obtenidos
                                String nombre = document.getString("name");
                                String email = document.getString("email");
                                String password = document.getString("password");

                                // Inicializar los datos de nivel, experiencia y Pokémon
                                int lvl = document.contains("level") ? document.getLong("level").intValue() : 1; // Default: 1 si no existe
                                int exp = document.contains("experience") ? document.getLong("experience").intValue() : 0; // Default: 0 si no existe
                                int lastPokemon = document.contains("lastPokemon") ? document.getLong("lastPokemon").intValue() : 0; // Default: 0 si no existe
                                ArrayList<String> pokedex = (ArrayList<String>) document.get("pokedex"); // Puede ser null o vacío
                                if (pokedex == null) pokedex = new ArrayList<>();
                                ArrayList<String> tareas = (ArrayList<String>) document.get("tasks"); // Puede ser null o vacío
                                if (tareas == null) tareas = new ArrayList<>();

                                // Crear el objeto UserData
                                UserData userData = new UserData(nombre, password, email);
                                userData.setLvl(lvl);
                                userData.setExp(exp);
                                userData.setLastPokemon(lastPokemon);
                                userData.setPokedex(pokedex);
                                userData.setTarea(tareas);

                                // Guardar el UserData en un archivo JSON utilizando tu función saveJsonData
                                JsonHandler.saveJsonData(LoginActivity.this, userData);

                                // Redirigir a la TaskActivity después de guardar los datos
                                Intent intent = new Intent(LoginActivity.this, TaskActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Contraseña incorrecta");
                            }
                        }
                    } else {
                        showToast("Usuario no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Login", "Error al autenticar con Firestore: " + e.getMessage());
                    showToast("Error al autenticar, intente de nuevo");
                });
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
