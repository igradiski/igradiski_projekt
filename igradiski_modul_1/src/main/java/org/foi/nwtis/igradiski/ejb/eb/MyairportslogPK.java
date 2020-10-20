/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Korisnik
 */
@Embeddable
public class MyairportslogPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(nullable = false, length = 10)
    private String ident;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date flightdate;

    public MyairportslogPK() {
    }

    public MyairportslogPK(String ident, Date flightdate) {
        this.ident = ident;
        this.flightdate = flightdate;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public Date getFlightdate() {
        return flightdate;
    }

    public void setFlightdate(Date flightdate) {
        this.flightdate = flightdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ident != null ? ident.hashCode() : 0);
        hash += (flightdate != null ? flightdate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MyairportslogPK)) {
            return false;
        }
        MyairportslogPK other = (MyairportslogPK) object;
        if ((this.ident == null && other.ident != null) || (this.ident != null && !this.ident.equals(other.ident))) {
            return false;
        }
        if ((this.flightdate == null && other.flightdate != null) || (this.flightdate != null && !this.flightdate.equals(other.flightdate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.igradiski.ejb.eb.MyairportslogPK[ ident=" + ident + ", flightdate=" + flightdate + " ]";
    }
    
}
