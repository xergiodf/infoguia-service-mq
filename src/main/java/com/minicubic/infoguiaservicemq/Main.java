package com.minicubic.infoguiaservicemq;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.minicubic.infoguiacore.dao.ClienteDao;
import com.minicubic.infoguiacore.model.Cliente;
import com.minicubic.infoguiaservicemq.services.ClienteService;
import com.minicubic.infoguiaservicemq.util.Constants;
import com.minicubic.infoguiaservicemq.util.ImprovedDateTypeAdapter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author hectorvillalba
 */
public class Main implements MqttCallback {

    private static final Logger LOG = java.util.logging.Logger.getLogger("MAIN");
    private static final String BROKER_ID = "infoguia-server-" + String.valueOf(Math.random()) ;
    private static final String MQTT_TOPIC = "/api/request/#";
    private MqttClient client;
    private final MqttConnectOptions connOpts = new MqttConnectOptions();
    private ClienteService clienteService = new ClienteService();
    private  Gson gson = new GsonBuilder().create(); 
// Register an adapter to manage the date types as long values 
 

// Register an adapter to manage the date types as long values 
    

    public static void main(String[] args) {
        Main brokerMQTTClient = new Main();
        brokerMQTTClient.connect();
        brokerMQTTClient.subscribeToRegisters();
    }

    //Conexion MQTT
    public synchronized void connect() {

        try {
            
            connOpts.setUserName(Constants.MQTT_USER);
            connOpts.setPassword(Constants.MQTT_PASS.toCharArray());
            connOpts.setAutomaticReconnect(true);
            connOpts.setCleanSession(false);
            connOpts.setKeepAliveInterval(300);
            
            client = new MqttClient(Constants.MQTT_ADDR, BROKER_ID, null);
            client.connect(connOpts);

            LOG.log(Level.INFO, "Conectado a {0}", client);
        } catch (MqttException ex) {

            LOG.log(Level.SEVERE, "Fallo conexion a MQTT. Error: {0}", ex.getMessage());
            ex.printStackTrace();
            connect();
            subscribeToRegisters();
        }

    }

    @Override
    public void connectionLost(Throwable thrwbl) {

        LOG.info("Conexion perdida, intentando recuperar...");
        connect();
        subscribeToRegisters();
    }
    
 

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // Register an adapter to manage the date types as long values
        System.out.println("-------------------------------------------------");
        System.out.println("| Topic:" + topic);
        System.out.println("| QoS: " + message.getQos());
        System.out.println("| Message: " + new String(message.getPayload()));
        System.out.println("-------------------------------------------------");
        try {
            LOG.info("Recepcionando mensaje...");
            LOG.log(Level.INFO, "Topico: {0}", topic);
            LOG.log(Level.INFO, "Mensaje: {0}", Arrays.toString(message.getPayload()));
                
            /**
             * Los topicos deberían de ser así -> /api/plataforma/pantalla/ServiceClass/Method/token
             * Obs: deben ir asi para poder idenficar la plataforma y la pantalla se uso de un modo muy espeficido en android
             * Los valores de los parámetros deben ir en el mensaje.
             */
            String service =  "com.minicubic.infoguiaservicemq.services."+ topic.split("/")[5];
            String pantalla = topic.split("/")[4];
            String method = topic.split("/")[6];
            String token = topic.split("/")[7];
            String response =  topic.replace(topic.split("/")[2], "response/"+token);

            LOG.log(Level.INFO, "Service Class: {0}", service);
            LOG.log(Level.INFO, "Method: {0}", method);
            LOG.log(Level.INFO, "Token: {0}", token);
            
            try {
                Class<?> serviceClass = Class.forName(service);
                Object serviceObj = serviceClass.newInstance();
                Object data = null;
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new String(message.getPayload()));
                JSONObject array = (JSONObject)obj; 
                data = array.get("data");
                    if(pantalla.equalsIgnoreCase("clientecategorias")){
                        client.publish(response, new MqttMessage(gson.toJson(clienteService.getClientesPorNombre(new String(message.getPayload()))).getBytes()));
                    }else if (method.contains("Cliente")) {
                        client.publish(response, new MqttMessage(gson.toJson(clienteService.getClientesPorNombre(new String(message.getPayload()))).getBytes()));
                    }else if (method.contains("Publicacion")) {
                        client.publish(response, new MqttMessage(gson.toJson(clienteService.getPublicacion(new String(message.getPayload()))).getBytes()));
                    }else if(method.contains("Sucursales")){
                        client.publish(response, new MqttMessage(gson.toJson(clienteService.getSucursales(new String(message.getPayload()))).getBytes()));
                    }else if(method.contains("Categoria")){
                        client.publish(response, new MqttMessage(gson.toJson(clienteService.getCategoria(new String(message.getPayload()))).getBytes()));
                    }
                // ESTO ES UNA PRUEBA, HAY QUE MEJORAR
                /*if (data.toString().length() >2 ) {
                    Method serviceMethod = serviceObj.getClass().getMethod(method, String.class);
                    client.publish(response, new MqttMessage(gson.toJson(serviceMethod.invoke(serviceObj,new String(message.getPayload()))).getBytes()));
                } else {
                    Method serviceMethod = serviceObj.getClass().getMethod(method);
                    client.publish(response, new MqttMessage(gson.toJson(serviceMethod.invoke(serviceObj)).getBytes()));
                }*/
            }catch (Exception e) {
                LOG.log(Level.SEVERE,"ERROR ", e.getMessage());
                e.printStackTrace();
            }
            LOG.info("Respuesta enviada");
            //client.close();

        }catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al recibir mensaje {0}", e.getMessage());
            e.printStackTrace();
        }

    }

    public void subscribeToRegisters() {
        try {
            client.setCallback(this);
            client.subscribe(MQTT_TOPIC, 2);
            LOG.info("Suscribiendose a  " + MQTT_TOPIC);

            //client.close();

            //client.disconnect();
        } catch (MqttException me) {
            // El client.close() tira este exception, pero sigue funcionando.
            // Si se quita, deja de funcionar
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al suscribirse {0}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        LOG.info("el mensaje del broker se entrego.. ");
    }

}
