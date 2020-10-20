/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.igradiski.dretve.DretvaMQTT;
import org.foi.nwtis.igradiski.ejb.sb.MqttZapisFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.igradiski.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Korisnik
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    @Inject
    MyairportsFacade myairportsFacade;

    @Inject
    MqttZapisFacade mqttZapisFacade;

    DretvaMQTT d = null;

    /**
     * Metoda koja se pali kod pokrtanja konteksta i preuzima konfiguraciju
     *
     * @param sce servelet kontekst
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("/WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            System.out.println("Start");
            d = new DretvaMQTT();
            d.myairportsFacade = myairportsFacade;
            d.mqttZapisFacade = mqttZapisFacade;
            d.konf = konf;
            d.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("webModul_aplikacija_3_pokrenut");
    }

    /**
     * Metoda kod unistavanja konteksta
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (d != null) {
            d.interrupt();
        }
    }
}
