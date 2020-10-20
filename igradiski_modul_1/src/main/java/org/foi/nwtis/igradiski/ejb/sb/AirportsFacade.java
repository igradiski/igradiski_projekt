/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.igradiski.ejb.eb.Airports;

/**
 *
 * @author Korisnik
 */
@Stateless
public class AirportsFacade extends AbstractFacade<Airports> {

    @PersistenceContext(unitName = "NWTiS_Projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirportsFacade() {
        super(Airports.class);
    }
     public List<Airports> pretraziPremaImenu(String naziv) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Airports.class);
        Root<Airports> airports = cq.from(Airports.class);
        Expression<String> zaNaziv = airports.get("name");
        cq.where(cb.like(zaNaziv, naziv));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }
     
     public List<Airports> pretraziPremaDrzavi(String drzava) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Airports.class);
        Root<Airports> airports = cq.from(Airports.class);
        Expression<String> zaDrzavu = airports.get("isoCountry");
        cq.where(cb.like(zaDrzavu, drzava));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }
    
    
}
