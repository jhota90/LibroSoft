/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import DaoJPA.UsuarioJpaController;
import Entidades.Usuario;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Male
 */

@ManagedBean
public class ControladorUsuario {
    
    private UsuarioJpaController daoUsuario;
    private EntityManagerFactory bd;
    private String usuario;
    private String clave;
    
    public void crearDao(){
        if(daoUsuario == null){
            bd = FachadaBaseDatos.getEntityManagerFactory();
            daoUsuario = new UsuarioJpaController(bd);
        }
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
        
    public String login(){
        try{
            crearDao();
        }
        catch(Exception e){
            return "newxhtml";
        }
        Usuario u = new Usuario();
        int codigoUsuario;
        try{
            codigoUsuario = Integer.parseInt(usuario);
            System.out.println("2");
        }catch(NumberFormatException en){
            en.printStackTrace();
            return "index";
        }
        try{
            u = daoUsuario.findUsuario(codigoUsuario);
            System.out.println("3");
        }catch(IllegalArgumentException eu){
            eu.printStackTrace();
            return "index";
        }
        
        if(clave.equals(u.getClave())
                && usuario.equals(u.getIdUsuario()+"")){
            return "index";
        }
        return "index";
    }
    
    public static void main(String[] a){
        try{
            ControladorUsuario c = new ControladorUsuario();
            c.crearDao();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
