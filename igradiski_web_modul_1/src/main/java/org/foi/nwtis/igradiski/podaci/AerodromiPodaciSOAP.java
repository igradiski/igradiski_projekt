/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.podaci;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.igradiski.ejb.eb.Airports;
import org.foi.nwtis.igradiski.ejb.eb.Myairports;
import org.foi.nwtis.igradiski.ejb.sb.AirportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.KorisniciFacade;
import org.foi.nwtis.igradiski.ejb.sb.MyairportsFacade;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author Korisnik
 */
public class AerodromiPodaciSOAP {

    AirportsFacade af;
    KorisniciFacade kf;

    public AerodromiPodaciSOAP(AirportsFacade af, KorisniciFacade kf) {
        this.af = af;
        this.kf = kf;
    }

    public List<Aerodrom> popisAerodromaPoNazivu(String username, String password, String naziv) {
        ArrayList<Aerodrom> lista = new ArrayList<Aerodrom>();
        List<Airports> listaAerodromaFasada = af.pretraziPremaImenu(naziv);
        for (Airports a : listaAerodromaFasada) {
            lista.add(KreirajAerodrom(a));
        }
        return lista;
    }

    public List<Aerodrom> popisAerodromaPoDrzavi(String username, String password, String drzava) {
        ArrayList<Aerodrom> lista = new ArrayList<Aerodrom>();
        List<Airports> listaAerodromaFasada = af.pretraziPremaDrzavi(drzava);
        for (Airports a : listaAerodromaFasada) {
            lista.add(KreirajAerodrom(a));
        }
        return lista;
    }

    public List<Aerodrom> popisVlastitihAerodroma(String username, String password, MyairportsFacade myAF, String korisnik) {
        ArrayList<Aerodrom> lista = new ArrayList<Aerodrom>();
        List<Myairports> listaMojihAerodromaFasada = myAF.findAll();
        List<Airports> listaAerodromaFasada = new ArrayList<Airports>();
        for (Myairports myairports : listaMojihAerodromaFasada) {
            if (myairports.getUsername().equals(korisnik)) {
                listaAerodromaFasada.add(myairports.getIdent());
            }
        }
        for (Airports a : listaAerodromaFasada) {
            lista.add(KreirajAerodrom(a));
        }
        return lista;
    }

    public Aerodrom KreirajAerodrom(Airports a) {
        Aerodrom aerodrom = new Aerodrom();
        aerodrom.setNaziv(a.getName());
        aerodrom.setIcao(a.getIdent());
        aerodrom.setDrzava(a.getIsoCountry());
        Lokacija l = izracunajLokaciju(a.getCoordinates());
        aerodrom.setLokacija(l);
        return aerodrom;
    }

    public Lokacija izracunajLokaciju(String koordinate) {
        String[] parts = koordinate.split(",");
        String prva = parts[0];
        String druga = parts[1];
        Lokacija l = new Lokacija(prva, druga);
        return l;
    }

    public double izracunajUdaljenost(String icao1, String icao2) {
        double udaljenost = 0;
        Aerodrom aerodrom1 = DohvatiAerodromPremaICAO(icao1);
        Aerodrom aerodrom2 = DohvatiAerodromPremaICAO(icao2);
        Koordinata koordinata1 = StvoriKoordinatu(aerodrom1);
        Koordinata koordinata2 = StvoriKoordinatu(aerodrom2);
        udaljenost = izracunUdaljenosti(koordinata1, koordinata2);
        return udaljenost;
    }

    /**
     * Preuzeto s neta na: https://www.geodatasource.com/developers/java
     *
     * @param k1
     * @param k2
     * @return
     */
    public double izracunUdaljenosti(Koordinata k1, Koordinata k2) {
        double udaljenost = 0;
        String unit = "K";
        double lat1 = k1.geoSirina;
        double lat2 = k2.geoSirina;
        double lon1 = k1.geoDuzina;
        double lon2 = k2.geoDuzina;
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            udaljenost = dist;
        }
        return udaljenost;
    }

    public Koordinata StvoriKoordinatu(Aerodrom aerodrom) {
        Koordinata k = new Koordinata();
        Lokacija lokacija = aerodrom.getLokacija();
        String latidude = lokacija.getLatitude();
        String longitude = lokacija.getLongitude();
        k.geoSirina = Double.parseDouble(latidude);
        k.geoDuzina = Double.parseDouble(longitude);
        return k;
    }

    public Aerodrom DohvatiAerodromPremaICAO(String icao) {
        Aerodrom a = new Aerodrom();
        List<Airports> listaAerodromaFasada = af.findAll();
        for (Airports airports : listaAerodromaFasada) {
            String icaoDani = airports.getIdent();
            if (icaoDani.equals(icao)) {
                a.setIcao(airports.getIdent());
                a.setDrzava(airports.getIsoCountry());
                Lokacija l = izracunajLokaciju(airports.getCoordinates());
                a.setLokacija(l);
                a.setNaziv(airports.getName());
            }
        }
        return a;
    }

    public List<Aerodrom> dohvatiAerodromeKojiZadovoljavajuInterval(String icao, int intervalOD, int intervalDO, MyairportsFacade myAF, String korisnik) {
        List<Aerodrom> lista = new ArrayList<Aerodrom>();
        Aerodrom aerodrom = DohvatiAerodromPremaICAO(icao);
        Aerodrom mojAerodrom = new Aerodrom();
        List<Myairports> listaMojihAerodromaFasada = myAF.findAll();
        for (Myairports mojiAerodromi : listaMojihAerodromaFasada) {
            if (mojiAerodromi.getUsername().equals(korisnik)) {
                mojAerodrom = pretvoriAirportsUAerodrom(mojiAerodromi.getIdent());
                double duzina = izracunajUdaljenost(mojAerodrom.getIcao(), aerodrom.getIcao());
                if (duzina >= intervalOD && duzina <= intervalDO) {
                    lista.add(mojAerodrom);
                }
            }
        }
        return lista;
    }

    public Aerodrom pretvoriAirportsUAerodrom(Airports a) {
        Aerodrom aerodrom = new Aerodrom();
        aerodrom.setDrzava(a.getIsoCountry());
        aerodrom.setIcao(a.getIdent());
        Lokacija l = izracunajLokaciju(a.getCoordinates());
        aerodrom.setLokacija(l);
        aerodrom.setNaziv(a.getName());
        return aerodrom;
    }

}
