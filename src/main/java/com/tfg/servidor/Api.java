package com.tfg.servidor;

import com.tfg.servidor.api.Autenticacion;
import com.tfg.servidor.api.Chat;
import com.tfg.biblioteca.Biblioteca.Mensaje;
import com.tfg.servidor.entidades.Usuario;
import com.tfg.servidor.ia.ProcesamientoIA;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Clase Api del servidor
 * 
 * @author peblo
 */
public class Api {
    /**
     * Metodo que procesa todos los mensajes que llegan al servidor. Su respuesta
     * depende del mensaje serializado que reciba.
     * 
     * @param respuestaSerializada
     * @param mensajeSerializado
     * @return
     * @throws URISyntaxException 
     */
    public static boolean procesarMensaje(ObjectOutputStream respuestaSerializada, ObjectInputStream mensajeSerializado) throws URISyntaxException {
        boolean toRet = false;
        try {
            respuestaSerializada.flush(); // Asegurarse de que el flujo está listo para usar
            String accion = (String) mensajeSerializado.readObject();
            System.out.println(accion);
            switch (accion) {
                case "login" -> {
                    String usuario = (String) mensajeSerializado.readObject();
                    String contrasena = (String) mensajeSerializado.readObject();
                    UUID uid = Autenticacion.login(usuario, contrasena);
                    //TODO::revisar (por qué uid) propuesta:class usuario
                    if (uid != null) {
                        respuestaSerializada.writeObject("OK");
                        respuestaSerializada.writeObject(uid);
                        respuestaSerializada.flush();
                    } else {
                        respuestaSerializada.writeObject("OKNO");
                        respuestaSerializada.writeObject(uid);
                        respuestaSerializada.flush();
                    }
                }
                case "registro" -> {
                    String usuarioNuevo = (String) mensajeSerializado.readObject();
                    String contrasenaNueva = (String) mensajeSerializado.readObject();
                    boolean ok = Autenticacion.registro(usuarioNuevo, contrasenaNueva);
                    respuestaSerializada.writeObject(accion);
                    respuestaSerializada.writeObject(ok);
                    respuestaSerializada.flush();
                }
                case "mensaje" -> {
                    String emisor = (String) mensajeSerializado.readObject();
                    String destinatario = (String) mensajeSerializado.readObject();
                    Mensaje msg = (Mensaje) mensajeSerializado.readObject();
                    Mensaje respuestaNegativa = new Mensaje(msg.getRemitente(), "Mensaje inadecuado.");

                    //Meter en buffer 
                    String respuesta = ProcesamientoIA.procesarMensajeConIA(msg.getMsg());
                    System.out.println(respuesta);
                    
                    // Obtener el ObjectOutputStream del destinatario
                    ObjectOutputStream out = Servidor.getClienteStream(destinatario);
                    if (out != null) {
                        try {
                            out.writeObject("mensaje");
                            if (respuesta.contains("SI")) {
                                Chat.enviarMensaje(emisor, destinatario, msg);
                                out.writeObject(msg);
                            } else {
                                Chat.enviarMensaje(emisor, destinatario, respuestaNegativa);
                                out.writeObject(respuestaNegativa);
                            }
                            out.flush();
                            System.out.println("Mensaje enviado de " + msg.getRemitente().getValue() + " a " + destinatario);
                        } catch (IOException e) {
                            System.err.println("Error al enviar mensaje a " + destinatario + ": " + e.getMessage());
                        }
                    } else {
                        System.out.println("Destinatario no encontrado: " + destinatario);
                    }
                    System.out.println("}");
                }
                case "solicitarHistorial" -> {
                    System.out.println("hist{");
                    String usuario1 = (String) mensajeSerializado.readObject();
                    String usuario2 = (String) mensajeSerializado.readObject();

                    String claveConversacion = Chat.generarClaveConversacion(usuario1, usuario2);
                    LinkedList<Mensaje> historial = Chat.obtenerHistorial(claveConversacion);

                    respuestaSerializada.writeObject("historialRespuesta");
                    respuestaSerializada.writeObject(historial);
                    respuestaSerializada.flush();
                    System.out.println("}");
                }

                case "anadirAmigo" -> { //TODO:: ERROR AQUI
                    String nombreAmigo = (String) mensajeSerializado.readObject();
                    UUID uuidUsuario = (UUID) mensajeSerializado.readObject();
                    Usuario usuarioBuscado = Autenticacion.buscarUsuario(uuidUsuario);
                    Usuario agregarAmigo = usuarioBuscado.agregarAmigo(nombreAmigo);
                    String enviar = "";
                    if (agregarAmigo != null) {
                        enviar = agregarAmigo.getUsuario();
                    }
                    respuestaSerializada.writeObject("amigoAgregado");
                    respuestaSerializada.writeObject(enviar);
                    respuestaSerializada.flush();
                    usuarioBuscado.mostrarAmigos();
                }

                case "solicitarListaAmigos" -> {
                    UUID usuarioId = (UUID) mensajeSerializado.readObject();
                    List<String> listaUsuarios = Autenticacion.buscarUsuario(usuarioId).getListaNombreAmigos();
                    respuestaSerializada.writeObject(accion);
                    respuestaSerializada.writeObject(listaUsuarios);
                    respuestaSerializada.flush();
                }

                case "desconectar" -> {
                    String nombre = (String) mensajeSerializado.readObject();
                    Autenticacion.logout(nombre);
                    System.out.println("Cliente desconectado");

                }

                case "setNombreUsuario" -> {
                    String nombreUsuario = (String) mensajeSerializado.readObject();
                    Servidor.agregarCliente(nombreUsuario, respuestaSerializada);
                    System.out.println("Cliente agregado: " + nombreUsuario);
                }

                default -> {
                    System.err.println("Accion no reconocida: " + accion);
                }

            }
            //respuestaSerializada.flush(); // Asegurarse de que el flujo está listo para usar
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error al procesar el mensaje: " + ex.getMessage());
            return false;
        }
        return true;
    }

}
