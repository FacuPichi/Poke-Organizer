package com.example.poke_organizer;

import java.io.BufferedReader; // Importación de clases para entrada/salida
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection; // Importación de clases para trabajar con conexiones HTTP
import java.net.URL;

public class APIClient { // Declaración de la clase APIClient
    private static final String BASE_URL = "https://pokeapi.co/api/v2/"; // Definición de la URL base de la API

    public APIClient() { // Constructor de la clase que toma la clave de API como argumento
    }

    public String sendGetRequest(String relativeUrl) { // Declaración del método para enviar solicitudes GET a la API
        try { // Inicio de un bloque try para manejar excepciones
            URL url = new URL(BASE_URL + relativeUrl); // Creación de un objeto URL combinando la URL base y la URL relativa
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Inicio de una conexión HTTP

            try { // Inicio de otro bloque try
                connection.setRequestMethod("GET"); // Establecimiento del método de solicitud como GET

                int responseCode = connection.getResponseCode(); // Obtención del código de respuesta de la conexión

                if (responseCode == HttpURLConnection.HTTP_OK) { // Comprobación de si la respuesta es exitosa (código 200)
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); // Configuración de un lector de entrada para leer la respuesta
                    String inputLine;
                    StringBuilder response = new StringBuilder(); // Creación de un StringBuilder para almacenar la respuesta

                    while ((inputLine = in.readLine()) != null) { // Lectura de la respuesta línea por línea
                        response.append(inputLine); // Agregando cada línea a la respuesta
                    }
                    in.close(); // Cierre del lector de entrada

                    return response.toString(); // Devolución de la respuesta como una cadena
                } else {
                    // Puedes manejar códigos de respuesta no exitosos aquí si es necesario
                    System.err.println("Error al realizar la solicitud. Código de respuesta: " + responseCode);
                }
            } finally {
                connection.disconnect(); // Asegura que la conexión se cierre
            }
        } catch (IOException e) { // Manejo de excepciones de entrada/salida
            e.printStackTrace(); // Imprime detalles de la excepción
        }

        return null; // Devuelve null si ocurre algún error
    }
}
