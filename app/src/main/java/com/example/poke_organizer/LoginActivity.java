package com.example.poke_organizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText email, password;
    private Button registerButton, loginButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editPassword);
        registerButton = findViewById(R.id.register);
        loginButton = findViewById(R.id.loginbutton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.setCancelable(false);

        checkUserSession(); // Verifica si ya hay una sesión iniciada
        setButtonListeners();
    }

    private void checkUserSession() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Si hay una sesión activa, redirigir automáticamente
            redirectToTaskActivity();
        }
    }

    private void setButtonListeners() {
        registerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.ThemeColorDark));
        registerButton.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        loginButton.setOnClickListener(view -> handleLogin());
    }

    private void handleLogin() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (!isValidEmail(emailText)) {
            showToast("Ingrese un email válido");
            return;
        }

        if (passwordText.isEmpty()) {
            showToast("Ingrese su contraseña");
            return;
        }

        progressDialog.show();

        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fetchUserData();
                    } else {
                        progressDialog.dismiss();
                        showToast("Credenciales incorrectas");
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("Login", "Error al autenticar: " + e.getMessage());
                    showToast("Error al autenticar, intente de nuevo");
                });
    }

    private void fetchUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();

            if (email == null) {
                showToast("No se pudo obtener el email del usuario");
                progressDialog.dismiss();
                return;
            }

            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            String nombre = document.getString("nombre");
                            int lvl = document.contains("level") ? document.getLong("level").intValue() : 1;
                            int exp = document.contains("experience") ? document.getLong("experience").intValue() : 0;
                            int lastPokemon = document.contains("lastPokemon") ? document.getLong("lastPokemon").intValue() : 0;

                            ArrayList<String> pokedex = document.contains("pokedex") ? (ArrayList<String>) document.get("pokedex") : new ArrayList<>();
                            ArrayList<String> tareas = document.contains("tasks") ? (ArrayList<String>) document.get("tasks") : new ArrayList<>();

                            UserData userData = new UserData(nombre != null ? nombre : "Desconocido", password.getText().toString(), email);
                            userData.setLvl(lvl);
                            userData.setExp(exp);
                            userData.setLastPokemon(lastPokemon);
                            userData.setPokedex(pokedex);
                            userData.setTarea(tareas);

                            JsonHandler.saveJsonData(LoginActivity.this, userData);
                            redirectToTaskActivity();
                        } else {
                            showToast("No se encontraron datos del usuario en Firestore");
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Log.e("Firestore", "Error al obtener los datos del usuario: " + e.getMessage());
                        showToast("Error al obtener los datos del usuario");
                    });
        } else {
            showToast("No se encontraron datos del usuario en Firebase Authentication");
            progressDialog.dismiss();
        }
    }



    private void redirectToTaskActivity() {
        startActivity(new Intent(LoginActivity.this, TaskActivity.class));
        finish();
    }

    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
