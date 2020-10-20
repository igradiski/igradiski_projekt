/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.sb;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.igradiski.ejb.eb.Airports;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.igradiski.ejb.eb.Myairportslog;
import org.foi.nwtis.igradiski.ejb.eb.MyairportslogPK;

/**
 *
 * @author Korisnik deni state
 */
public class DretvaZaKomunikaciju extends Thread {

    public AirportsFacade airportsFacade;
    public MyairportsFacade myairportsFacade;
    public MyairportslogFacade myairportslogFacade;
    public KorisniciFacade korisniciFacade;
    public AirplanesFacade airplanesFacade;
    public BP_Konfiguracija konf;
    private OSKlijent klijentOS;
    private String korisnickoImeOS = "";
    private String lozinkaOS = "";
    private Timestamp datumPreuzimanja;
    private Timestamp doDatum;
    private int trajanjePauze = 0;
    private int trajanjeCiklusaDretve = 0;
    public boolean dopustenoPreuzimanje = true;
    private String bpkorisnik = "";
    private String bplozinka = "";
    private String url = "";
    ArrayList<String> aerodromi = new ArrayList<>();

    public DretvaZaKomunikaciju() {
    }

    DretvaZaKomunikaciju(BP_Konfiguracija konf, boolean dopustenoPreuzimanjeDretve, AirplanesFacade airplanesFacade,
            AirportsFacade airportsFacade, MyairportsFacade myairportsFacade,
            MyairportslogFacade myairportslogFacade) {
        this.konf = konf;
        this.dopustenoPreuzimanje = dopustenoPreuzimanjeDretve;
        this.airplanesFacade = airplanesFacade;
        this.airportsFacade = airportsFacade;
        this.myairportsFacade = myairportsFacade;
        this.myairportslogFacade = myairportslogFacade;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Ciklus dretve pocinje!");
                long pocetakDretve = System.currentTimeMillis();
                if (dopustenoPreuzimanje) {
                    ProvjeriPreuzimanje();
                    System.out.println("Smijem preuzimati");
                } else {
                    System.out.println("Zabranjeno preuzimanje");
                }
                long trajanjeOperacija = System.currentTimeMillis() - pocetakDretve;
                long interval = trajanjeCiklusaDretve * 1000;
                long spavanje = interval - trajanjeOperacija;
                if (spavanje <= 0) {
                    Thread.sleep(0);
                } else {
                    Thread.sleep(interval - trajanjeOperacija);
                }
                PovecajDanDretve();
                System.out.println("Ciklus dretve gotov!");
            } catch (InterruptedException ex) {
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        PostaviKonfiguraciju();
    }

    public synchronized void PostaviKonfiguraciju() {
        trajanjeCiklusaDretve = Integer.parseInt(konf.getKonfig().dajPostavku("preuzimanje.ciklus"));
        trajanjePauze = Integer.parseInt(konf.getKonfig().dajPostavku("preuzimanje.pauza"));
        String datumOD = konf.getKonfig().dajPostavku("preuzimanje.pocetak");
        System.out.println(datumOD);
        datumPreuzimanja = TimestampConverter(datumOD);
        doDatum = new Timestamp(datumPreuzimanja.getTime() + 86400000);
        korisnickoImeOS = konf.getKonfig().dajPostavku("OpenSkyNetwork.korisnik");
        lozinkaOS = konf.getKonfig().dajPostavku("OpenSkyNetwork.lozinka");
    }

    public synchronized Timestamp TimestampConverter(String datum) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date parsedDate;
        Timestamp timestamp = new Timestamp(10);
        try {
            parsedDate = dateFormat.parse(datum);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
            return timestamp;

        } catch (ParseException ex) {
            System.out.println("Krivi parse");
        }
        return timestamp;
    }

    private synchronized void IzracunajDatumDo() {
        int dan = 86400000;
        Timestamp danDo = new Timestamp(datumPreuzimanja.getTime() + dan);
        doDatum = danDo;
    }

    public synchronized void ProvjeriPreuzimanje() {
        //TODO PROVJERI DA LI SMIJE PREUZIMATI
        dopustenoPreuzimanje = true;
        if (dopustenoPreuzimanje) {
            SveFunkcijeDretve();
        } else {
            //sleep
        }
    }

    public synchronized void SveFunkcijeDretve() {
        KreirajOSKlijent();
        PreuzimanjePodatakaAerodroma();
        ObradiAerodrome();
    }

    public synchronized void KreirajOSKlijent() {
        klijentOS = new OSKlijent(korisnickoImeOS, lozinkaOS);
    }

    public synchronized void ObradiAerodrome() {
        AirportORM airportORM = new AirportORM();
        for (String a : aerodromi) {
            if (airportORM.AerodromPostojiULogu(a, datumPreuzimanja, myairportslogFacade)) {
                System.out.println("Aerodrom vec postoji!");
            } else {
                try {
                    PreuzmiPodatkaSaServisa(a);
                    System.out.println("Spavanje za aerodrom");
                    Thread.sleep(trajanjePauze * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DretvaZaKomunikaciju.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public synchronized void UpisiDaJeAerodromPreuzet(String a, Timestamp datumPreuzimanja, int brojPreuzimanja) {
        Myairportslog log = new Myairportslog();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        log.setStored(timestamp);
        log.setBrojLetova(brojPreuzimanja);
        MyairportslogPK pk = new MyairportslogPK(a, datumPreuzimanja);
        List<Airports> lista = airportsFacade.findAll();
        for (Airports airport : lista) {
            if (airport.getIdent().equals(a)) {
                log.setAirports(airport);
            }
        }
        log.setMyairportslogPK(pk);
        myairportslogFacade.create(log);
        System.out.println("Kreiran log");
    }

    public synchronized void PreuzmiPodatkaSaServisa(String a) {
        System.out.println(a);
        List<AvionLeti> listaAviona = klijentOS.getDepartures(a, datumPreuzimanja, doDatum);
        int brojPreuzetihAerodroma = listaAviona.size();
        AirportORM airportORM = new AirportORM();
        if (listaAviona != null) {
            for (AvionLeti avion : listaAviona) {
                airportORM.DodajAvionUBazu(avion, airplanesFacade);
            }
            UpisiDaJeAerodromPreuzet(a, datumPreuzimanja, brojPreuzetihAerodroma);

        } else {
            System.out.println("Lista aviona prazna!");
        }
    }

    public synchronized void PreuzimanjePodatakaAerodroma() {
        AirportORM airportORM = new AirportORM();
        aerodromi = airportORM.dohvatiMojeAerodromeDistinct(myairportsFacade);
    }

    public synchronized void PovecajDanDretve() {
        int dan = 86400000;
        Timestamp noviDatum = new Timestamp(datumPreuzimanja.getTime() + dan);
        datumPreuzimanja = noviDatum;
        if (ProvjeriNoviDatum(datumPreuzimanja)) {
            try {
                Thread.sleep(86400000);
            } catch (InterruptedException ex) {

            }
        }
        doDatum = new Timestamp(datumPreuzimanja.getTime() + dan);

    }

    /**
     * Funckija za provjeru novog datuma da li treba zaustaviti dretu
     *
     * @param datum- datum koji se provjerava
     * @return vraca da li je kraj (DA NE)
     */
    private synchronized boolean ProvjeriNoviDatum(Timestamp datum) {
        boolean doKraja = false;
        String datumKraj = konf.getKonfig().dajPostavku("preuzimanje.kraj");
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date datumProvjere = new Date(datum.getTime());
        try {
            Date date = formatter.parse(datumKraj);
            if (date.equals(datumProvjere)) {
                doKraja = true;
            }
        } catch (ParseException ex) {

        }
        return doKraja;
    }

}
