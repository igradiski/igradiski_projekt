/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.podaci;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.igradiski.ejb.eb.Airplanes;
import org.foi.nwtis.igradiski.ejb.sb.AirplanesFacade;
import org.foi.nwtis.igradiski.ejb.sb.AirportsFacade;
import org.foi.nwtis.igradiski.ejb.sb.KorisniciFacade;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author Korisnik
 */
public class AvionPodaciSOAP {

    AirportsFacade af;
    KorisniciFacade kf;
    AirplanesFacade aif;

    public AvionPodaciSOAP(AirportsFacade af, KorisniciFacade kf, AirplanesFacade aif) {
        this.af = af;
        this.kf = kf;
        this.aif = aif;
    }

    public List<AvionLeti> popisLetovaInterval(String icao, int vrijemeOD, int vrijemeDO) {
        ArrayList<AvionLeti> lista = new ArrayList<AvionLeti>();
        List<Airplanes> listaAviona = aif.pretraziPremaImenuAerodromaCAPI(icao);
        lista = ObradiInterval(listaAviona, vrijemeOD, vrijemeDO, icao);
        return lista;
    }

    private ArrayList<AvionLeti> ObradiInterval(List<Airplanes> listaAviona, int vrijemeOD, int vrijemeDO, String icao) {
        ArrayList<AvionLeti> lista = new ArrayList<AvionLeti>();
        for (Airplanes a : listaAviona) {
            int poletio = a.getFirstseen();
            int sletio = a.getLastseen();
            if (poletio >= vrijemeOD && sletio <= vrijemeDO && a.getEstdepartureairport().equals(icao)) {
                lista.add(ObradiPodatkeAviona(a));
            }
        }
        return lista;
    }

    private AvionLeti ObradiPodatkeAviona(Airplanes a) {
        AvionLeti avion = new AvionLeti();
        avion.setIcao24(a.getIcao24());
        avion.setFirstSeen(a.getFirstseen());
        avion.setEstDepartureAirport(a.getEstdepartureairport());
        avion.setLastSeen(a.getLastseen());
        avion.setEstArrivalAirport(a.getEstarrivalairport());
        avion.setCallsign(a.getCallsign());
        avion.setEstDepartureAirportHorizDistance(a.getEstdepartureairporthorizdistance());
        avion.setEstDepartureAirportVertDistance(a.getEstdepartureairportvertdistance());
        avion.setEstArrivalAirportHorizDistance(a.getEstarrivalairporthorizdistance());
        avion.setEstArrivalAirportVertDistance(a.getEstarrivalairportvertdistance());
        avion.setArrivalAirportCandidatesCount(a.getArrivalairportcandidatescount());
        return avion;
    }

    public List<AvionLeti> popisLetovaAvionInterval(String icao, int vrijemeOD, int vrijemeDO) {
        ArrayList<AvionLeti> lista = new ArrayList<AvionLeti>();
        List<Airplanes> listaAviona = aif.pretraziPremaImenuAvionaCAPI(icao);
        lista = ObradiIntervalZaAvion(listaAviona, vrijemeOD, vrijemeDO, icao);
        return lista;
    }

    private ArrayList<AvionLeti> ObradiIntervalZaAvion(List<Airplanes> listaAviona, int vrijemeOD, int vrijemeDO, String icao) {
        ArrayList<AvionLeti> lista = new ArrayList<AvionLeti>();
        for (Airplanes a : listaAviona) {
            int poletio = a.getFirstseen();
            int sletio = a.getLastseen();
            String icao2 = a.getIcao24();
            if (poletio >= vrijemeOD && sletio <= vrijemeDO && icao2.equals(icao)) {
                lista.add(ObradiPodatkeAviona(a));
            }
        }
        return lista;
    }

}
