package com.tfg.servidor;

import com.tfg.servidor.api.Chat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servidor
 * 
 * @author peblo
 */
public class Servidor {

    private static final int PUERTO = 9999;
    private ServerSocket socket;
    private ExecutorService clientes;

    // Lista de todos los ObjectOutputStream de los clientes conectados
    private static final ConcurrentHashMap<String, ObjectOutputStream> clientesMap = new ConcurrentHashMap<>();
    
    /**
     * Metodo que agrega un cliente a la lista de clientes. Empareja el
     * nombre de usuario con su ObjectOutputStream
     * 
     * @param nombreUsuario
     * @param out 
     */
    public static void agregarCliente(String nombreUsuario, ObjectOutputStream out) {
        clientesMap.put(nombreUsuario, out);
    }

    /**
     * Metodo que obtiene el flujo de salida de un cliente en especifico
     * 
     * @param nombreUsuario
     * @return 
     */
    public static ObjectOutputStream getClienteStream(String nombreUsuario) {
        return clientesMap.get(nombreUsuario);
    }

    /**
     * Elimina a un cliente de la lista de clientes
     * 
     * @param nombreUsuario 
     */
    public static void eliminarCliente(String nombreUsuario) {
        clientesMap.remove(nombreUsuario);
        System.out.println("Cliente eliminado: " + nombreUsuario);
    }

    public Servidor() {
    }
    
    /**
     * Inicia el socket del servidor y crea una ThreadPoll para cada hilo
     * de cada cliente
     */
    public void iniciar() {
        try {
            this.socket = new ServerSocket(PUERTO);
            this.clientes = Executors.newCachedThreadPool();
            //TODO::join
            new Thread(() -> Chat.BUFFER.procesar()).start();
            new Thread(() -> aceptarClientes()).start();

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Acepta todos los clientes que llegan
     */
    private void aceptarClientes() {
        while (!socket.isClosed()) {
            try {
                System.out.println("Esperando cliente...");
                Socket cliente = socket.accept();
                clientes.submit(new Cliente(cliente));
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
    
    /**
     * Clase embebida Cliente de Servidor. Maneja a cada cliente
     */
    private static class Cliente implements Runnable {

        private final Socket conexion;

        public Cliente(Socket conexion) {
            this.conexion = conexion;
        }

        @Override
        public void run() {
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                out = new ObjectOutputStream(this.conexion.getOutputStream());
                in = new ObjectInputStream(this.conexion.getInputStream());

                while (!this.conexion.isClosed()) {

                    System.out.println("\nPreparado para procesar el mensaje\n");
                    boolean comprobar = false;
                    //Este try catch es por la IA
                    try {
                        comprobar = Api.procesarMensaje(out, in);
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (!comprobar) {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                        if (conexion != null) {
                            conexion.close();
                        }
                    }

                }
            } catch (SocketException ex) {
                System.out.println("Cliente desconectado: " + conexion.getRemoteSocketAddress());
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (conexion != null) {
                        conexion.close();
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        //SERVIDOR
        new Servidor().iniciar();
    }
}
