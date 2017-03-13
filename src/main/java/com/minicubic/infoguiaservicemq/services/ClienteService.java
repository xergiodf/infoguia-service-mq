package com.minicubic.infoguiaservicemq.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.minicubic.infoguiacore.dao.ClienteDao;
import com.minicubic.infoguiacore.dto.ClientesDTO;
import com.minicubic.infoguiacore.dto.NovedadesDTO;
import com.minicubic.infoguiacore.dto.Request;
import com.minicubic.infoguiacore.dto.Response;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hectorvillalba
 */
public class ClienteService {
    
    private final ClienteDao dao = new ClienteDao();
    private final Gson gson = new GsonBuilder().create();
    private static final Logger LOG = Logger.getLogger("ClienteService");
    
    public List<ClientesDTO> getClientesPorSucursal(String msg){
        Type listType = new TypeToken<Request<ClientesDTO>>() {}.getType();
        Request<ClientesDTO> request = gson.fromJson(msg, listType);
        try {
            LOG.info("getClientesPorSucursal... "); 
            List clientes = dao.getClientes(request.getData());
            LOG.log(Level.INFO, "Se encontraron {0} registros", clientes.size());
            return clientes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Response<List<NovedadesDTO>> getNovedades(){
        Response<List<NovedadesDTO>> response = new Response<>();
        try {
            LOG.info("getNovedades...");
            List novedades = dao.getNovedades();
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
    
}
