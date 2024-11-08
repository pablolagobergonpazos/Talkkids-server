package com.tfg.servidor.api;

import com.tfg.biblioteca.Biblioteca.Mensaje;
import java.util.Queue;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;

/**
 * Clase Buffer de Servidor
 * 
 * @author peblo
 */
public class Buffer {

    private static Buffer singleton = null;
    private Queue<SimpleEntry<String, Mensaje>> cola = new LinkedList();

    private Buffer() {
        cola = new LinkedList();
    }

    /**
     * Metodo que añade un mensaje a la cola del buffer
     * 
     * @param claveConversacion
     * @param msg 
     */
    public void anadir(String claveConversacion, Mensaje msg) {
        this.cola.add(new SimpleEntry(claveConversacion, msg));
    }

    /**
     * Metodo que procesa el mensaje :: TODO
     */
    public void procesar() {
        while (true) {
            if (!cola.isEmpty()) {
                SimpleEntry<String, Mensaje> mensaje = cola.remove();
                if (true) {
                    Chat.actualizarHistorial(mensaje.getKey(), mensaje.getValue());
                }
            }
        }
    }

    public static synchronized Buffer crear() {
        if (singleton == null) {
            singleton = new Buffer();
        }

        return singleton;
    }

}
