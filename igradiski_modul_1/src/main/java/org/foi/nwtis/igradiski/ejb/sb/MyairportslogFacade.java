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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.foi.nwtis.igradiski.ejb.eb.Myairports;
import org.foi.nwtis.igradiski.ejb.eb.Myairportslog;

/**
 *
 * @author Korisnik
 */
@Stateless
public class MyairportslogFacade extends AbstractFacade<Myairportslog> {

    @PersistenceContext(unitName = "NWTiS_Projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportslogFacade() {
        super(Myairportslog.class);
    }
    
    
    @Override
    public List<String> pretraziDistinctDatum() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        Root<Myairports> ruleVariableRoot = query.from(Myairports.class);
        query.select(ruleVariableRoot.get("flightdate").get("flightdate")).distinct(true);
        Query q = em.createQuery(query);
        return q.getResultList();
    }
    
}
