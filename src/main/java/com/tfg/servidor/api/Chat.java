package com.tfg.servidor.api;

import com.tfg.biblioteca.Biblioteca.Mensaje;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Clase Chat de Servidor
 * 
 * @author peblo
 */
public class Chat {
    
    private static final HashMap<String, LinkedList<Mensaje>> historiales = new HashMap<>();
    public static final Buffer BUFFER = Buffer.crear();
    
    /**
     * Metodo que genera una clave unica en una conversación que involucra a
     * dos personas. Se asegura de que vaya quien vaya primero, la clave es la
     * misma
     * 
     * @param usuario1
     * @param usuario2
     * @return 
     */
    public static String generarClaveConversacion(String usuario1, String usuario2){
        return usuario1.compareTo(usuario2) < 0 ? usuario1 + "_" + usuario2 : usuario2 + "_" + usuario1;
    }

    /**
     * Metodo que envia el mensaje al Buffer
     * 
     * @param usuario1
     * @param usuario2
     * @param msg 
     */
    public static void enviarMensaje(String usuario1, String usuario2, Mensaje msg) {
        String claveConversacion = generarClaveConversacion(usuario1, usuario2);
        BUFFER.anadir(claveConversacion, msg);
        actualizarHistorial(claveConversacion, msg);
    }
    
    /**
     * Metodo que actualiza el historial
     * 
     * @param claveConversacion
     * @param msg 
     */
    public static void actualizarHistorial(String claveConversacion, Mensaje msg){
        historiales.computeIfAbsent(claveConversacion, k -> new LinkedList<>()).add(msg);
    }
    
    /**
     * Merodo que devuelve el historial de una conversacion
     * 
     * @param claveConversacion
     * @return devuelve el historial
     */
    public static LinkedList<Mensaje> obtenerHistorial(String claveConversacion){
        return historiales.computeIfAbsent(claveConversacion, k -> new LinkedList<>());
    }

}
