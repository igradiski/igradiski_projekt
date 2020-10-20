/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.web.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.igradiski.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Korisnik
 */
public class KorisnikDAO {

    String url = "";
    String bpkorisnik = "";
    String bplozinka = "";
    BP_Konfiguracija bpk;

    public KorisnikDAO(BP_Konfiguracija konf) {
        this.url = konf.getServerDatabase() + konf.getUserDatabase();
        this.bpkorisnik = konf.getUserUsername();
        this.bplozinka = konf.getUserPassword();
        this.bpk = konf;
    }

    public boolean ProvjeriPostojanjeKorisnika(String username, String lozinka) {
        boolean postoji = false;
        String upit = "SELECT * FROM KORISNICI WHERE KORISNICKO_IME='" + username + "' AND LOZINKA='" + lozinka + "'";
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                while (rs.next()) {
                    postoji = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postoji;
    }

    public boolean provjeriPostojanjeKorisnika(String user, String pass) {
        boolean postoji = false;
        String upit = "SELECT * FROM KORISNICI WHERE KORISNICKO_IME='" + user + "'";
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                while (rs.next()) {
                    postoji = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postoji;
    }

    public String RegistrirajKorisnika(String user, String pass) {
        String registriran = "ERR 12;";
        if (provjeriPostojanjeKorisnika(user, pass)) {
            registriran = "ERR 12;";
        } else {
            String upit = "INSERT INTO KORISNICI (KORISNICKO_IME, LOZINKA) VALUES ('" + user + "', '" + pass + "')";
            System.out.println(upit);
            if (IzvrsiDodavanjeAerodroma(upit)) {
                registriran = "OK 10;";
            }
        }

        return registriran;
    }

    public boolean IzvrsiDodavanjeAerodroma(String upitDodavanje) {
        boolean dodano = false;
        try {
            Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
            Statement s = con.createStatement();
            s.executeUpdate(upitDodavanje);
            dodano = true;
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dodano;
    }

}
