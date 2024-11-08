package com.tfg.servidor.entidades;

import com.tfg.biblioteca.Biblioteca.Mensaje;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Grupo {

    private UUID gid;
    private Set<UUID> usuarios;
    private ArrayList<Mensaje> conversacion;

    public Grupo() {
        this.gid = UUID.randomUUID();
        this.conversacion = new ArrayList<>();
    }

    public Grupo(Set<UUID> usuarios) {
        this();
        this.usuarios = usuarios;
    }

    public void anadirUsuario(UUID usuario) {
        this.usuarios.add(usuario);
    }

    public boolean tieneUsuario(UUID usuario) {
        return this.usuarios.contains(usuario);
    }

    public void borrarUsuario(UUID usuario) {
        this.usuarios.remove(usuario);
    }
    
    public void anadirMensaje(Mensaje msg){
        this.conversacion.add(msg);
    }
    
    public ArrayList<Mensaje> recuperarHistorial(){
        return this.conversacion;
    }

}
