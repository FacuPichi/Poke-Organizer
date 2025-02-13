package com.example.poke_organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth; // Instancia de Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        db = FirebaseFirestore.getInstance(); // Inicializa Firestore
        mAuth = FirebaseAuth.getInstance(); // Inicializa Firebase Authentication

        EditText name = findViewById(R.id.editTextName);
        EditText email = findViewById(R.id.editTextEmail);
        EditText password = findViewById(R.id.editPassword2);  // Campo de contraseña
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
                String passwordText = password.getText().toString(); // Obtener la contraseña

                if (nameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Verificar si el correo electrónico ya está registrado en Firestore
                    db.collection("users").whereEqualTo("email", emailText).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                                        // Si el correo no está registrado, crear usuario en Authentication
                                        createUserInAuthentication(nameText, emailText, passwordText, random);
                                    } else {
                                        // El correo ya está registrado
                                        Toast.makeText(RegisterActivity.this, "Este correo ya está registrado.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void createUserInAuthentication(String name, String email, String password, Random random) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Usuario creado exitosamente en Authentication
                            Toast.makeText(RegisterActivity.this, "Usuario registrado en Authentication.", Toast.LENGTH_SHORT).show();

                            // Guardar datos adicionales en Firestore
                            saveUserToFirestore(name, email, password, random);
                        } else {
                            // Error al crear el usuario en Authentication
                            Toast.makeText(RegisterActivity.this, "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String name, String email, String password, Random random) {
        // Crear un objeto User con los datos
        UserData user = new UserData(name, password, email);
        final int pokemonAleatorio = random.nextInt(1010 - 1 + 1) + 1;
        user.setLastPokemon(pokemonAleatorio);
        user.addPokemon(String.valueOf(pokemonAleatorio));
        user.setLvl(1);

        // Guardamos los datos en Firestore bajo la colección "users"
        db.collection("users")
                .add(user)  // Se usa `add()` para crear un nuevo documento con un ID único
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Informar al usuario que el registro fue exitoso
                            Toast.makeText(RegisterActivity.this, "Registro exitoso en Firestore.", Toast.LENGTH_LONG).show();

                            // Finalizar la actividad actual
                            finish();
                        } else {
                            // En caso de error, notificar al usuario
                            Toast.makeText(RegisterActivity.this, "Error al registrar el usuario en Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}