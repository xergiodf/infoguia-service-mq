package com.minicubic.infoguiaservicemq.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.minicubic.infoguiacore.dao.ClienteDao;
import com.minicubic.infoguiacore.dao.jdbc.ClienteDAOJDBC;
import com.minicubic.infoguiacore.dto.ClienteDto;
import com.minicubic.infoguiacore.dto.NovedadesDto;
import com.minicubic.infoguiacore.dto.Request;
import com.minicubic.infoguiacore.dto.Response;
import com.minicubic.infoguiacore.dto.jdbc.CategoriaDTO;
import com.minicubic.infoguiacore.dto.jdbc.ClienteDTO;
import com.minicubic.infoguiacore.dto.jdbc.PublicacionClienteDTO;
import com.minicubic.infoguiacore.dto.jdbc.SucursalClientesDTO;
import com.minicubic.infoguiacore.model.Cliente;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hectorvillalba
 */
public class ClienteService implements Serializable {
    
    
    private final ClienteDAOJDBC clienteDAOJDBC = new ClienteDAOJDBC();
    private final Gson gson = new GsonBuilder().create();
    private static final transient Logger LOG = Logger.getLogger("ClienteService");

    
    public Response<List<ClienteDTO>> getClientesPorNombre(String nombre){
        Response<List<ClienteDTO>> response = new Response<>();
        try {
            LOG.info("getClientes... "); 
            List<ClienteDTO> clientes = clienteDAOJDBC.getClientePorNombreCorto(nombre);
            LOG.log(Level.INFO, "Se encontraron {0} registros", clientes.size());
            response.setCodigo(200);
            response.setData(clientes);
            response.setMensaje("Success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
        
   
    public Response<List<PublicacionClienteDTO>> getPublicacion(String tipoPublicacion){
        Response<List<PublicacionClienteDTO>> response = new Response<>();
        try {
            LOG.info("getPublicacion...");
            List novedades = clienteDAOJDBC.getPublicacionCliente(tipoPublicacion);
            response.setCodigo(200);
            response.setData(novedades);
            response.setMensaje("Success");
            LOG.info("Success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Response<List<SucursalClientesDTO>> getSucursales(String idCliente){
        Response<List<SucursalClientesDTO>> response = new Response<>();
        try {
            LOG.info("getSucursales...");
            List sucursales = clienteDAOJDBC.getSucursales(idCliente);
            response.setCodigo(200);
            response.setData(sucursales);
            response.setMensaje("Success");
            LOG.info("Success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public Response<List<CategoriaDTO>> getCategoria(String parametro){
        Response<List<CategoriaDTO>> response = new Response<>();
        try {
            LOG.info("getCategorias...");
            List categorias = clienteDAOJDBC.getCategorias(parametro);
            response.setCodigo(200);
            response.setData(categorias);
            response.setMensaje("Success");
            LOG.info("Success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
}
