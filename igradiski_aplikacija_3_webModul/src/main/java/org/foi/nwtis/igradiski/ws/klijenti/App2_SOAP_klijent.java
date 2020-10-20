/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ws.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.igradiski.ejb.eb.Airplanes;
import org.foi.nwtis.igradiski.ejb.eb.Airports;
import org.foi.nwtis.igradiski.ejb.eb.Myairports;
import org.foi.nwtis.igradiski.ejb.sb.AirplanesFacade;
import org.foi.nwtis.igradiski.ejb.sb.AirportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.igradiski.web.serveri.Aerodrom;
import org.foi.nwtis.igradiski.web.serveri.Lokacija;

/**
 *
 * @author Korisnik
 */
public class App2_SOAP_klijent {

    public List<Aerodrom> dajAerodromeKorisnika(String korisnik, String lozinka) {
        List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
        try { // Call Web Service Operation
            org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service service = new org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service();
            org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP port = service.getAplikacija2SOAPPort();
            aerodromi = port.popisVlastitihAerodroma(korisnik, lozinka);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return aerodromi;
    }

    public List<Aerodrom> dohvatiPodatkeAerodroma(String korisnik, String lozinka, String icao, MyairportsFacade myairportsFacade) {
        List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
        List<Myairports> myairports = new ArrayList<>();
        myairports = myairportsFacade.findAll();
        for (Myairports airport : myairports) {
            String ime = airport.getIdent().getIdent();
            if (airport.getUsername().equals(korisnik) && ime.equals(icao)) {
                aerodromi.add(KreirajAerodromObjekt(airport.getIdent()));
            }
        }
        return aerodromi;
    }

    public Airports dohvatiPraceniAerodrom(String korisnik, String lozinka, String icao, MyairportsFacade myairportsFacade) {
        String odgovor = "";
        Airports a = null;
        List<Myairports> myairports = new ArrayList<>();
        myairports = myairportsFacade.findAll();
        for (Myairports airport : myairports) {
            String ident = airport.getIdent().getIdent();
            if (ident.equals(icao) && airport.getUsername().equals(korisnik)) {
                a = airport.getIdent();
            }
        }
        return a;
    }

    public void obrisiPraceniAerodrom(String korisnik, String lozinka, Airports a, MyairportsFacade myairportsFacade) {
        List<Myairports> myairports = new ArrayList<>();
        myairports = myairportsFacade.findAll();
        String danoIme = a.getIdent();
        for (Myairports airport : myairports) {
            String ime = airport.getIdent().getIdent();
            if (airport.getUsername().equals(korisnik) && ime.equals(danoIme)) {
                myairportsFacade.remove(airport);
            }
        }

    }

    public Aerodrom KreirajAerodromObjekt(Airports airport) {
        Aerodrom a = new Aerodrom();
        a.setDrzava(airport.getIsoCountry());
        a.setIcao(airport.getIdent());
        a.setNaziv(airport.getName());
        Lokacija l = KreirajLokaciju(airport.getCoordinates());
        a.setLokacija(l);
        return a;
    }

    public Lokacija KreirajLokaciju(String kordinate) {
        String[] parts = kordinate.split(",");
        String prva = parts[0];
        String druga = parts[1];
        Lokacija l = new Lokacija();
        l.setLatitude(prva);
        l.setLongitude(druga);
        return l;
    }

    public boolean ProvjeriPostojanjeAerodroma(String korisnik, String lozinka,
            Airports airport, AirplanesFacade airplanesFacade) {
        boolean imaAerodrome = false;
        List<Airplanes> lista = new ArrayList<Airplanes>();
        lista = airplanesFacade.findAll();
        for (Airplanes a : lista) {
            if (a.getEstdepartureairport().equals(airport.getIdent())) {
                imaAerodrome = true;
            }
        }
        return imaAerodrome;
    }

    public boolean ObrisiAvioneAerodroma(String korisnik, String lozinka, Airports airport,
            AirplanesFacade airplanesFacade) {
        boolean obrisano = false;
        List<Airplanes> listaAviona = new ArrayList<>();
        List<Airplanes> listaAvionaAerodroma = new ArrayList<>();
        listaAviona = airplanesFacade.findAll();
        String trazenoIme = airport.getIdent();
        for (Airplanes a : listaAviona) {
            String ime = a.getEstdepartureairport();
            if (trazenoIme.equals(ime)) {
                listaAvionaAerodroma.add(a);
            }
        }
        if (!listaAvionaAerodroma.isEmpty()) {
            for (Airplanes a : listaAvionaAerodroma) {
                airplanesFacade.remove(a);
            }
            obrisano = true;
        }
        return obrisano;
    }

    public List<Aerodrom> dohvatiAerodrome(String korisnik, String lozinka, String nazivAerodroma, String drzava, AirportsFacade airportsFacade) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        if (nazivAerodroma.isEmpty() && drzava.isEmpty()) {
            aerodromi = kreirajListuSvihAerodroma(airportsFacade);
        } else {
            if (nazivAerodroma.isEmpty()) {
                aerodromi = dohvatiAerodromePoDrzavi(korisnik, lozinka, drzava);
            } else {
                aerodromi = dohvatiAerodromePonazivu(korisnik, lozinka, nazivAerodroma);
            }
        }
        return aerodromi;
    }

    public List<Aerodrom> dohvatiAerodromePoDrzavi(String korisnik, String lozinka, String drzava) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        try { // Call Web Service Operation
            org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service service = new org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service();
            org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP port = service.getAplikacija2SOAPPort();
            aerodromi = port.popisAerodromaPoDrzavi(korisnik, lozinka, drzava);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return aerodromi;
    }

    public List<Aerodrom> dohvatiAerodromePonazivu(String korisnik, String lozinka, String naziv) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        try { // Call Web Service Operation
            org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service service = new org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP_Service();
            org.foi.nwtis.igradiski.web.serveri.Aplikacija2SOAP port = service.getAplikacija2SOAPPort();
            aerodromi = port.popisAerodromaPoNazivu(korisnik, lozinka, naziv);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return aerodromi;
    }

    public List<Aerodrom> kreirajListuSvihAerodroma(AirportsFacade airportsFacade) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        List<Airports> airports = new ArrayList<>();
        airports = airportsFacade.findAll();
        for (Airports a : airports) {
            aerodromi.add(KreirajAerodromObjekt(a));
        }
        return aerodromi;
    }

}
