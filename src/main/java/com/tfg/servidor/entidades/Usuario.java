package com.tfg.servidor.entidades;

import com.tfg.servidor.api.Autenticacion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Clase Usuario de Servidor
 * 
 * @author peblo
 */
public class Usuario implements Serializable {

    private String usuario;
    private String contrasena;
    private UUID uid;
    private List<Usuario> amigos;

    public Usuario() {}

    public Usuario(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.uid = UUID.randomUUID();
        this.amigos = new ArrayList<>();
    }
    
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public UUID getUUID(){
        return uid;
    }
    
        
    public List<Usuario> getListaAmigos(){
        return amigos;
    }
    
    public List<String> getListaNombreAmigos() {
        return amigos.stream()
                     .map(Usuario::getUsuario)
                     .collect(Collectors.toList());
    }
    
    /**
     * Metodo para coger un amigo de la lista de amigos
     * 
     * @param nombreAmigo
     * @return devuelve al amigo si lo encontro, en caso contrario devuelve null
     */
    public Usuario cogerAmigo(String nombreAmigo) {
        Usuario toRet = null;
        for(Usuario amigo: amigos){
            if(nombreAmigo.equals(amigo.usuario)){
                toRet = amigo;
            }
        }
        return toRet;
    }

    /**
     * Metodo para agregar un amigo a la lista de amigos
     * 
     * @param amigo
     * @return devuelve al amigo agregado
     */
    public Usuario agregarAmigo(String amigo) {
        
        Usuario agregarAmigo = Autenticacion.buscarRegistrado(amigo);

        if(agregarAmigo != null && cogerAmigo(agregarAmigo.usuario) == null){
            amigos.add(agregarAmigo);
        }

        return agregarAmigo;
    }
    
    /**
     * Metodo para el debug que muestra los amigos
     */
    public void mostrarAmigos(){  
        for (Usuario amigo : amigos){
            System.out.println(amigo.usuario);
        }
    }
 
}
