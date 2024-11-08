package com.tfg.servidor.ia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/**
 * Clase ProcesamientoIA del Servidor
 * @author peblo
 */
public class ProcesamientoIA {

    private static final String MODEL_NAME = "gemma2:27b";
    private static final String IA_ENDPOINT = "http://localhost:11434/api/generate";

    /**
     * Procesa un mensaje con la IA para analizar su contenido.
     *
     * @param mensaje El mensaje a procesar.
     * @return La respuesta de la IA.
     * @throws IOException Si hay un error de entrada/salida.
     * @throws URISyntaxException Si la URI es incorrecta.
     */
    public static String procesarMensajeConIA(String mensaje) throws IOException, URISyntaxException {
        String promptText = "Eres un sistema para procesar mensajes de texto con "
                + "el fin de detectar actividad sospechosa en conversaciones de adolescentes. "
                + "Teniendo en cuenta que el mensaje ha sido enviado por un menor de edad, "
                + "responde unicamente SI si el mensaje es adecuado o NO en caso contrario. "
                //+ "En caso de que la respuesta fuese NO, clasifica si el mensaje es un INSULTO o INFO (información muy personal y/o sensible). "
                + "El mensaje es el siguiente: " + mensaje; //Prompt del sys y del user en el mismo prompt (vulneable a prompt injection)

        URI uri = new URI(IA_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        // Cuerpo JSON de la solicitud
        String jsonInputString = String.format(
                "{\"model\": \"%s\", \"prompt\":\"%s\", \"stream\": false}", MODEL_NAME, promptText
        );

        // Enviar el cuerpo de la solicitud
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Leer la respuesta
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // Analizar la respuesta JSON y obtener el campo "response"
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("response");
    }
}