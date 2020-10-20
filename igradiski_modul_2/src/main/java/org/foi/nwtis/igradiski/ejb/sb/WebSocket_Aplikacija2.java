/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.sb;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.foi.nwtis.igradiski.ejb.eb.Airports;
import org.foi.nwtis.igradiski.ejb.eb.Dnevnik;
import org.foi.nwtis.igradiski.ejb.eb.Myairports;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Korisnik
 */
@ServerEndpoint("/app2Socket")
@LocalBean
public class WebSocket_Aplikacija2 {

    private static List<Session> sesije = new ArrayList<>();
    private static ScheduledExecutorService timer
            = Executors.newSingleThreadScheduledExecutor();

    @Inject
    KorisniciFacade korisniciFacade;

    @Inject
    MyairportsFacade myairportsFacade;

    @Inject
    DnevnikFacade dnevnikFacade;

    @Inject
    AirportsFacade airportsFacade;

    @Inject
    ServletContext context;

    /**
     * Metoda koja registrira spajanje korisnika na vezu i dodaje ga u kolekciju
     * aktivnih korisnika.
     *
     * @param session sjednica korisnika
     */
    @OnOpen
    public void openConnection(Session session) {
        sesije.add(session);
        try {
            session.getBasicRemote().sendText("");
        } catch (IOException ex) {
            System.out.println("Greska: " + ex);
        }
        BP_Konfiguracija konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        int intervalTimera = Integer.parseInt(konf.getKonfig().dajPostavku("webSocket.cekanje"));
        timer.scheduleAtFixedRate(() -> slanjeSvima(), 0, intervalTimera, TimeUnit.SECONDS);
    }

    /**
     * Metoda koja vraća odgovor korisniku o zadnjem ažuriranju.
     *
     * @param poruka primljeni zahtjev korisnika
     */
    @OnMessage
    public void onMessage(String poruka) {
        System.out.println("========>Stigla poruka: " + poruka);
        dodajAerodromKorisniku(poruka);
        for (Session s : sesije) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText("");
                } catch (IOException ex) {
                    System.out.println("Greška: " + ex);
                }
            }
        }

    }

    @OnClose
    public void closedConnection(Session session) {
        sesije.remove(session);
        System.out.println("Zatvorena veza.");
    }

    @OnError
    public void error(Session session, Throwable t) {
        sesije.remove(session);
        System.out.println("Zatvorena veza zbog greške.");
    }

    public void slanjeSvima() {
        int broj = myairportsFacade.pretraziDistinct().size();
        String poruka = String.valueOf(broj);
        for (Session s : sesije) {
            if (s.isOpen()) {
                try {
                    System.out.println("saljem poruku!");
                    s.getBasicRemote().sendText(poruka);
                } catch (IOException ex) {
                    System.out.println("Greška: " + ex);
                }
            }
        }
        System.out.println("Broj u listi konacni: " + broj);
    }

    public void StvoriZapisDnevnik(String url, String username, long trajanje) {
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setUrlAdresa(url);
        dnevnik.setUsername(username);
        dnevnik.setTrajanjeObrade((int) trajanje);
        Date date = new Date();
        Timestamp vrijemePrijema = new Timestamp(date.getTime());
        dnevnik.setVrijemePrijema(vrijemePrijema);
        dnevnikFacade.create(dnevnik);
    }

    public void dodajAerodromKorisniku(String poruka) {
        long pocetak = System.currentTimeMillis();
        String[] podaci = poruka.split(";");
        String korisnik = podaci[0];
        String aeroString = podaci[1];
        Airports aerodrom = DohvatiTrazeniAerodrom(aeroString);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Myairports myairports = new Myairports();
        myairports.setIdent(aerodrom);
        myairports.setUsername(korisnik);
        myairports.setStored(timestamp);
        myairportsFacade.create(myairports);
        long kraj = System.currentTimeMillis();
        long trajanje = kraj - pocetak;
        String URL = "ws://localhost:8084/igradiski_web_modul_1/app2Socket";
        StvoriZapisDnevnik(URL, korisnik, trajanje);
    }

    public Airports DohvatiTrazeniAerodrom(String aeroString) {
        Airports aerodrom = new Airports();
        List<Airports> lista = new ArrayList<>();
        lista = airportsFacade.findAll();
        for (Airports a : lista) {
            if (a.getIdent().equals(aeroString)) {
                aerodrom = a;
            }
        }
        return aerodrom;
    }

}
