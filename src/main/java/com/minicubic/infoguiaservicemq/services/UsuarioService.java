package com.minicubic.infoguiaservicemq.services;

import com.minicubic.infoguiacore.dao.UsuarioDao;
import com.minicubic.infoguiacore.model.TipoUsuario;
import com.minicubic.infoguiacore.model.Usuario;
import java.util.List;
import javax.persistence.NoResultException;

/**
 *
 * @author xergio
 */
public class UsuarioService {
    
    private UsuarioDao dao = new UsuarioDao();
    
    /**
     * Obtiene un usuario en base al ID
     * @param id
     * @return Registro especifico de usuario
     */
    public Usuario getUsuario(Long id) {
        return dao.getUsuario(id);
    }
    
    /**
     * Obtiene una Lista de Tipos de Usuario
     * @return Lista de Tipos de Usuarios
     */
    public List<TipoUsuario> getTiposUsuarios() {
        return dao.getTiposUsuarios();
    }
}
