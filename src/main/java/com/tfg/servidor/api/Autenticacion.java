package com.tfg.servidor.api;

import com.tfg.servidor.entidades.Usuario;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Clase Autenticacion de Servidor
 * 
 * @author peblo
 */
public class Autenticacion {

    public static final ArrayList<Usuario> usuariosRegistrados = new ArrayList<>();
    public static final ArrayList<Usuario> usuariosLogueados = new ArrayList<>();

    /**
     * Método que comprueba si los datos proporcionados por el cliente coinciden
     * con algun uruario de la base de daton volatil
     * 
     * @param usuario
     * @param contrasena
     * @return 
     */
    public static UUID login(String usuario, String contrasena) {

        UUID uid = null;

        for (Usuario usuario_registrado : usuariosRegistrados) {
            if (usuario.equals(usuario_registrado.getUsuario()) && contrasena.equals(usuario_registrado.getContrasena())) {
                uid = usuario_registrado.getUUID();
                usuariosLogueados.add(usuario_registrado); //Se le da un UUID a un usuario que acaba de
                //loguearse y de añade a lso usuarios logueados
            }
        }
        return uid;
    }

    /**
     * Método que desconecta a un usuario
     * 
     * @param usuario 
     */
    public static void logout(String usuario) {

        for (Usuario usuario_registrado : usuariosRegistrados) {
            if(usuario_registrado.getUsuario().equals(usuario))
                usuariosLogueados.remove(usuario_registrado); //Se le da un UUID a un usuario que acaba de
        }
        
    }

    /**
     * Método que permite a un usuario registrarse.
     * 
     * @param usuario
     * @param contrasena
     * @return devuelve true si el usuario no existe y permite registrarse.
     * Devuelve false en caso contrario
     */
    public static boolean registro(String usuario, String contrasena) {
        // Verificar si el usuario ya existe
        for (Usuario u : usuariosRegistrados) {
            if (u.getUsuario().equals(usuario)) {
                return false; // Usuario ya existe
            }
        }
        // Registrar el nuevo usuario
        Usuario nuevoUsuario = new Usuario(usuario, contrasena);
        usuariosRegistrados.add(nuevoUsuario);
        return true; // Registro exitoso
    }

    /**
     * Busca a un usuario en la lista de usuario logueados
     * 
     * @param usuario
     * @return devuelve true si lo encontró o false en caso contrario
     */
    public static boolean buscarLogueado(String usuario) {
        boolean existe = false;
        for (Usuario usuario_logueado : usuariosLogueados) {
            if (usuario_logueado.getUsuario().equals(usuario)) {
                existe = true;
            }
        }
        return existe;
    }

    /**
     * Busca a un usuario registrado en la lista de usuarios registrados
     * 
     * @param usuario
     * @return devuelve al usuario Usuario si lo encontro, en caso
     * contrario, devuelve null
     */
    public static Usuario buscarRegistrado(String usuario) {
        Usuario usuarioBuscado = null;
        for (Usuario usuarioRegistrado : usuariosRegistrados) {
            if (usuarioRegistrado.getUsuario().equals(usuario)) {
                usuarioBuscado = usuarioRegistrado;
            }
        }
        return usuarioBuscado;
    }

    /**
     * Metodo para comprobar si existe un usuario registrado con ese nombre
     * 
     * @param usuario
     * @return devuelve true si existe, en caso contrario devuelve false
     */
    public static boolean existeUsuarioRegistrado(String usuario) {
        boolean existe = false;
        for (Usuario usuarioRegistrado : usuariosRegistrados) {
            if (usuarioRegistrado.getUsuario().equals(usuario)) {
                existe = true;
            }
        }
        return existe;
    }

    /**
     * Busca un usuario por UUID en la lista de usuarios registrados.
     * @param uuid
     * @return devuelve al usuario si lo encontró, en caso contrario devuelve null
     */
    public static Usuario buscarUsuario(UUID uuid) {
        Usuario usuarioEncontrado = null;
        for (Usuario usuario : usuariosRegistrados) {
            if (usuario.getUUID().equals(uuid)) {
                usuarioEncontrado = usuario;
            }
        }
        return usuarioEncontrado;
    }

}
