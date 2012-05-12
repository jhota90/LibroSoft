/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Male
 */
public class FachadaBaseDatos {
    
    public static EntityManagerFactory factoria;
    
    public static EntityManagerFactory getEntityManagerFactory(){
        if(factoria == null){
            factoria = Persistence.createEntityManagerFactory("LibroSoftBD");         
            return factoria;
        }else{
            return factoria;        
        }
    }
    
}
