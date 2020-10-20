/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.dretve;

import java.io.StringReader;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.foi.nwtis.igradiski.ejb.eb.MqttZapis;
import org.foi.nwtis.igradiski.ejb.eb.Myairports;
import org.foi.nwtis.igradiski.ejb.sb.MqttZapisFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.igradiski.web.podaci.MQTTPoruka;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

/**
 *
 * @author Korisnik
 */
public class DretvaMQTT extends Thread {

    public BP_Konfiguracija konf;
    private String user;
    private String pass;
    private String host;
    private int port;
    private String destination;
    private MQTT mqtt;
    private boolean kraj = false;
    public MyairportsFacade myairportsFacade;
    public MqttZapisFacade mqttZapisFacade;
    List<Myairports> listaSviAerodromi;
    private String mojaPoruka = "";

    @Override
    public void run() {
        System.out.println("Pokrenuta je!");
        mqtt = new MQTT();
        try {
            mqtt.setHost(host, port);
            mqtt.setUserName(user);
            mqtt.setPassword(pass);
        } catch (URISyntaxException ex) {
            System.out.println("Error");
            Logger.getLogger(DretvaMQTT.class.getName()).log(Level.SEVERE, null, ex);
        }
        final CallbackConnection connection = mqtt.callbackConnection();
        connection.listener(new org.fusesource.mqtt.client.Listener() {
            @Override
            public void onConnected() {
                System.out.println("Otvorena veza na MQTT");
            }

            @Override
            public void onDisconnected() {
                System.out.println("Prekinuta veza na MQTT");
            }

            @Override
            public void onPublish(UTF8Buffer utfb, Buffer buffer, Runnable r) {
                String body = buffer.utf8().toString();
                MQTTPoruka obradenaPoruka = new MQTTPoruka();
                obradenaPoruka = obradiPoruku(body);
                provjeriObjekt(obradenaPoruka);
            }

            @Override
            public void onFailure(Throwable thrwbl) {
                System.out.println("Problem u vezi na MQTT");
            }
        });
        connection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                Topic[] topics = {new Topic(destination, QoS.AT_LEAST_ONCE)};
                connection.subscribe(topics, new Callback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] qoses) {
                        System.out.println("Pretplata na: " + destination);
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        System.out.println("Problem kod pretplate na: " + destination);
                        System.exit(-2);
                    }
                });
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("Neuspjela pretplata na: " + destination);
                System.exit(-2);
            }
        });
        synchronized (DretvaMQTT.class) {
            while (!kraj && !this.isInterrupted()) {
                try {
                    DretvaMQTT.class.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(DretvaMQTT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Upaljena");
        preuzmiKonfiguraciju();
        listaSviAerodromi = myairportsFacade.findAll();
    }

    public MQTTPoruka obradiPoruku(String poruka) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        JsonObject jsonObject;
        JsonReader reader = Json.createReader(new StringReader(poruka));
        jsonObject = reader.readObject();
        MQTTPoruka porukaObject = new MQTTPoruka();
        porukaObject.setKorisnik(jsonObject.getString("korisnik"));
        porukaObject.setAerodrom(jsonObject.getString("aerodrom"));
        porukaObject.setAvion(jsonObject.getString("avion"));
        porukaObject.setOznaka(jsonObject.getString("oznaka"));
        porukaObject.setPoruka(jsonObject.getString("poruka"));
        porukaObject.setVrijeme(jsonObject.getString("vrijeme"));
        String vrijeme = jsonObject.getString("vrijeme");
        porukaObject.setVrijeme(vrijeme);
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(vrijeme);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            porukaObject.setVrijemeTS(timestamp);
        } catch (ParseException ex) {
            Logger.getLogger(DretvaMQTT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return porukaObject;
    }

    public void provjeriObjekt(MQTTPoruka poruka) {
        if (poruka.getKorisnik().equals(mojaPoruka)) {
            for (Myairports a : listaSviAerodromi) {
                if (a.getIdent().getIdent().equals(poruka.getAerodrom())) {
                    dodajPorukuUBazu(poruka);
                }
            }
        }
    }

    public void dodajPorukuUBazu(MQTTPoruka poruka) {
        MqttZapis zapis = new MqttZapis();
        zapis.setAerodrom(poruka.getAerodrom());
        zapis.setAvion(poruka.getAvion());
        zapis.setKorisnik(poruka.getKorisnik());
        zapis.setOznaka(poruka.getOznaka());
        zapis.setPoruka(poruka.getPoruka());
        zapis.setVrijeme(poruka.getVrijemeTS());
        mqttZapisFacade.create(zapis);
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    public synchronized void preuzmiKonfiguraciju() {
        user = konf.getKonfig().dajPostavku("svn.user");
        pass = konf.getKonfig().dajPostavku("svn.password");
        host = konf.getKonfig().dajPostavku("mqtt.host");
        mojaPoruka = konf.getKonfig().dajPostavku("moje.poruke");
        port = Integer.parseInt(konf.getKonfig().dajPostavku("mqtt.port"));
        destination = "/NWTiS/" + user;
        System.out.println(user + " " + pass + " " + host + " " + port + " " + destination);
    }

}
