/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.sb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.podaci.Airport;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 *
 * @author Korisnik
 */
public class AirportDAO {

    BP_Konfiguracija bpk;
    String url = "jdbc:derby://localhost:1527/nwtis_igradiski_bp_2";
    String bpkorisnik = "igradiski";
    String bplozinka = "roleta66";

    public AirportDAO(BP_Konfiguracija bpk) {
        this.bpk = bpk;

    }

    public ArrayList<Airport> PreuzmiAerodrome() {
        String upitMyAirports = "SELECT DISTINCT IDENT FROM MYAIRPORTS";
        ArrayList<String> listaIdenata = DohvatiIdente(upitMyAirports);
        ArrayList<Airport> listaIdent = new ArrayList<>();
        ArrayList<Airport> lista = new ArrayList<>();
        for (String ident : listaIdenata) {
            String upit = "SELECT * FROM AIRPORTS WHERE IDENT='" + ident + "'";
            listaIdent = DovhatiSveAerodrome(upit);
            for (Airport a : listaIdent) {
                lista.add(a);
            }
        }
        return lista;
    }

    public ArrayList<String> DohvatiIdente(String upit) {
        ArrayList<String> listaIdenata = new ArrayList<String>();
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                while (rs.next()) {
                    listaIdenata.add(ObradiPodatkeAMyAirports(rs));
                }
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaIdenata;
    }

    public String ObradiPodatkeAMyAirports(ResultSet rs) {
        String ident = "";
        try {
            ident = rs.getString("ident");
        } catch (SQLException ex) {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ident;
    }

    public void UpisiMyAirporstLog(Airport a, Timestamp datumPreuzimanja, int brojPreuzimanja) {
        Timestamp trenutnoVrijeme = new Timestamp(System.currentTimeMillis());
        String upit = "INSERT INTO MYAIRPORTSLOG VALUES ('" + a.getIdent() + "','" + datumPreuzimanja + "','" + trenutnoVrijeme + "'," + brojPreuzimanja + ")";
        IzvrsiInsertOrUpdate(upit);

    }

    public ArrayList<Airport> DovhatiSveAerodrome(String upit) {
        ArrayList<Airport> lista = new ArrayList<>();
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                while (rs.next()) {
                    lista.add(ObradiPodatkeAerodroma(rs));
                }
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public Airport ObradiPodatkeAerodroma(ResultSet rs) throws SQLException {
        String ident = rs.getString("ident");
        String type = rs.getString("type");
        String name = rs.getString("name");
        String elevation_ft = rs.getString("elevation_ft");
        String continent = rs.getString("continent");
        String iso_country = rs.getString("iso_country");
        String iso_region = rs.getString("iso_region");
        String municipality = rs.getString("municipality");
        String gps_code = rs.getString("gps_code");
        String iata_code = rs.getString("iata_code");
        String local_code = rs.getString("local_code");
        String coordinates = rs.getString("coordinates");
        Airport a = new Airport(ident, type, name, elevation_ft,
                continent, iso_country, iso_region, municipality,
                gps_code, iata_code, local_code, coordinates);
        return a;
    }

    public boolean AerodromPostojiULogu(Airport a, Timestamp datum) {
        boolean postoji = false;
        Date datumZaPromjenu = new Date(datum.getTime());
        String datumUpit = FormatirajDatum(datumZaPromjenu);
        String upit = "SELECT * FROM MYAIRPORTSLOG WHERE IDENT= " + "'" + a.getIdent() + "'"
                + " AND FLIGHTDATE=" + "'" + datumUpit + "'";
        postoji = DohvatiLogAerodroma(upit);
        return postoji;
    }

    /**
     * Dohvaća postojanje aerodroma u bazi my airports log
     *
     * @param upit upit za dohvacanje
     * @return postoji true ne postoji false
     */
    public boolean DohvatiLogAerodroma(String upit) {
        boolean postoji = false;
        int brojac = 0;
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                while (rs.next()) {
                    brojac++;
                }
                if (brojac > 0) {
                    return true;
                }
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postoji;
    }

    /**
     * Metoda za formatiranje datuma u drugi format
     *
     * @param datumFormat datum za formatiranje
     * @return string datum drugog formata yyyy-MM-dd
     */
    public String FormatirajDatum(Date datumFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(datumFormat);
        return date;
    }

    public void DodajAvionUBazu(AvionLeti avionLeti) {
        if (avionLeti != null) {
            if (avionLeti.getEstArrivalAirport() != null) {
                if (!avionLeti.getEstArrivalAirport().isEmpty()) {
                    String upit = KreirajUpitZaDodavanjeAviona(avionLeti);
                    IzvrsiInsertOrUpdate(upit);
                }
            }
        }
    }

    public String KreirajUpitZaDodavanjeAviona(AvionLeti avionLeti) {
        Timestamp vrijemeSpremanja = new Timestamp(System.currentTimeMillis());
        String icao24 = avionLeti.getIcao24();
        int firstSeen = avionLeti.getFirstSeen();
        String estDepartureAirport = avionLeti.getEstDepartureAirport();
        int lastSeen = avionLeti.getLastSeen();
        String estArrivalAirport = avionLeti.getEstArrivalAirport();
        String callsign = avionLeti.getCallsign();
        int estDepartureAirportHorizDistance = avionLeti.getEstDepartureAirportHorizDistance();
        int estDepartureAirportVertDistance = avionLeti.getEstDepartureAirportVertDistance();
        int estArrivalAirportHorizDistance = avionLeti.getEstArrivalAirportHorizDistance();
        int estArrivalAirportVertDistance = avionLeti.getEstArrivalAirportVertDistance();
        int departureAirportCandidatesCount = avionLeti.getDepartureAirportCandidatesCount();
        int arrivalAirportCandidatesCount = avionLeti.getArrivalAirportCandidatesCount();
        String upit = "INSERT INTO AIRPLANES VALUES (" + "DEFAULT," + "'" + icao24 + "'," + firstSeen + "," + "'" + estDepartureAirport + "'"
                + "," + lastSeen + "," + "'" + estArrivalAirport + "'," + "'" + callsign + "'," + estDepartureAirportHorizDistance
                + "," + estDepartureAirportVertDistance + "," + estArrivalAirportHorizDistance + ","
                + estArrivalAirportVertDistance + "," + departureAirportCandidatesCount + "," + arrivalAirportCandidatesCount + ",'" + vrijemeSpremanja + "')";
        return upit;
    }

    public void IzvrsiInsertOrUpdate(String upit) {
        try {
            Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
            Statement s = con.createStatement();
            s.executeUpdate(upit);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
