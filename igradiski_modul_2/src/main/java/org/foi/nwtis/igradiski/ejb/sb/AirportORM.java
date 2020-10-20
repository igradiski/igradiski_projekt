/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.sb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.igradiski.ejb.eb.Airplanes;
import org.foi.nwtis.igradiski.ejb.eb.Airports;
import org.foi.nwtis.igradiski.ejb.eb.Myairportslog;
import org.foi.nwtis.podaci.Airport;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author Korisnik
 */
public class AirportORM {

    public synchronized ArrayList<String> dohvatiMojeAerodromeDistinct(MyairportsFacade myairportsFacade) {
        List<String> listaMyAirports = myairportsFacade.pretraziDistinctString();
        ArrayList<String> lista = new ArrayList<>();
        for (String s : listaMyAirports) {
            lista.add(s);
        }
        System.out.println(lista.size());
        return lista;
    }

    public synchronized Airport KreirajObjektAirport(Airports airport) {
        Airport a = new Airport();
        a.setContinent(airport.getContinent());
        a.setCoordinates(airport.getCoordinates());
        a.setElevation_ft(airport.getElevationFt());
        a.setGps_code(airport.getGpsCode());
        a.setIata_code(airport.getIataCode());
        a.setIdent(airport.getIdent());
        a.setIso_country(airport.getIsoCountry());
        a.setIso_region(airport.getIsoRegion());
        a.setLocal_code(airport.getLocalCode());
        a.setMunicipality(airport.getMunicipality());
        a.setName(airport.getName());
        a.setType(airport.getType());
        return a;
    }

    public synchronized void DodajAvionUBazu(AvionLeti a, AirplanesFacade airplanesFacade) {
        Airplanes avion = new Airplanes();
        avion = kreirajAvion(a);
        airplanesFacade.create(avion);
        System.out.println("Dodano");
    }

    public synchronized Airplanes kreirajAvion(AvionLeti a) {
        Airplanes avion = new Airplanes();
        avion.setArrivalairportcandidatescount(a.getArrivalAirportCandidatesCount());
        avion.setCallsign(a.getCallsign());
        avion.setDepartureairportcandidatescount(a.getDepartureAirportCandidatesCount());
        avion.setEstarrivalairport(a.getEstArrivalAirport());
        avion.setEstarrivalairporthorizdistance(a.getEstDepartureAirportHorizDistance());
        avion.setEstarrivalairportvertdistance(a.getEstDepartureAirportVertDistance());
        avion.setEstdepartureairport(a.getEstDepartureAirport());
        avion.setFirstseen(a.getFirstSeen());
        avion.setIcao24(a.getIcao24());
        avion.setLastseen(a.getLastSeen());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        avion.setStored(timestamp);
        return avion;
    }

    public synchronized boolean AerodromPostojiULogu(String a, Timestamp datum, MyairportslogFacade myairportslogFacade) {
        boolean postoji = false;
        List<Myairportslog> lista = myairportslogFacade.findAll();
        for (Myairportslog log : lista) {
            String ime = log.getAirports().getIdent();
            Timestamp ts = new Timestamp(log.getMyairportslogPK().getFlightdate().getTime());
            if (ime.equals(a) && ts.equals(datum)) {
                postoji = true;
            }
        }
        return postoji;
    }

}
