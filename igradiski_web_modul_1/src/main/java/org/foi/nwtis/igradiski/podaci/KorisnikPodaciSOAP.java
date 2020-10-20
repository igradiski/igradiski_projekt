/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.podaci;

import java.util.List;
import org.foi.nwtis.igradiski.ejb.eb.Korisnici;
import org.foi.nwtis.igradiski.ejb.sb.KorisniciFacade;

/**
 *
 * @author Korisnik
 */
public class KorisnikPodaciSOAP {

    KorisniciFacade kf;

    public KorisnikPodaciSOAP(KorisniciFacade kf) {
        this.kf = kf;
    }

    public boolean ProvjeriPodatkeKorisnika(String username, String password) {
        boolean postoji = false;
        List<Korisnici> listaKorisnikaFacade = kf.findAll();
        for (Korisnici k : listaKorisnikaFacade) {
            if (k.getKorisnickoIme().equals(username) && k.getLozinka().equals(password)) {
                postoji = true;
            }
        }
        return postoji;
    }
}
