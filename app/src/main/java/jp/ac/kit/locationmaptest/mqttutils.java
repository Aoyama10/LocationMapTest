package jp.ac.kit.locationmaptest;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public abstract class mqttutils implements MqttCallback {
    private static MqttClient client;

    public static MqttClient getClient() {
        return client;
    }

    public boolean connect(String url, String username, String password) {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            MemoryPersistence persistance = new MemoryPersistence();
            client = new MqttClient("ssl://" + url + ":1883", "client1", persistance);

            //SSL
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            //keystore
            trustManagerFactory.init();
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            options.setSocketFactory(sslContext.getSocketFactory());

            client.setCallback(this);
            client.connect(options);

            return true;

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean pub(String topic, String payload, int qos) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        try {
            message.setQos(qos);
            client.publish(topic, message);
            return true;
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String msg = String.valueOf(message);
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean sub(String topic, int qos) {
        try {
            client.subscribe(topic, qos);

            return true;
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return false;

    }
}
