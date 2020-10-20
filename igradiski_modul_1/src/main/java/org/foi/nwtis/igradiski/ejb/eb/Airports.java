/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.ejb.eb;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Korisnik
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Airports.findAll", query = "SELECT a FROM Airports a")})
public class Airports implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(nullable = false, length = 10)
    private String ident;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String type;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String name;
    @Size(max = 10)
    @Column(name = "ELEVATION_FT", length = 10)
    private String elevationFt;
    @Size(max = 30)
    @Column(length = 30)
    private String continent;
    @Size(max = 30)
    @Column(name = "ISO_COUNTRY", length = 30)
    private String isoCountry;
    @Size(max = 10)
    @Column(name = "ISO_REGION", length = 10)
    private String isoRegion;
    @Size(max = 30)
    @Column(length = 30)
    private String municipality;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "GPS_CODE", nullable = false, length = 10)
    private String gpsCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "IATA_CODE", nullable = false, length = 10)
    private String iataCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "LOCAL_CODE", nullable = false, length = 10)
    private String localCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(nullable = false, length = 30)
    private String coordinates;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "airports")
    private List<Myairportslog> myairportslogList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ident")
    private List<Myairports> myairportsList;

    public Airports() {
    }

    public Airports(String ident) {
        this.ident = ident;
    }

    public Airports(String ident, String type, String name, String gpsCode, String iataCode, String localCode, String coordinates) {
        this.ident = ident;
        this.type = type;
        this.name = name;
        this.gpsCode = gpsCode;
        this.iataCode = iataCode;
        this.localCode = localCode;
        this.coordinates = coordinates;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElevationFt() {
        return elevationFt;
    }

    public void setElevationFt(String elevationFt) {
        this.elevationFt = elevationFt;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getIsoCountry() {
        return isoCountry;
    }

    public void setIsoCountry(String isoCountry) {
        this.isoCountry = isoCountry;
    }

    public String getIsoRegion() {
        return isoRegion;
    }

    public void setIsoRegion(String isoRegion) {
        this.isoRegion = isoRegion;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getGpsCode() {
        return gpsCode;
    }

    public void setGpsCode(String gpsCode) {
        this.gpsCode = gpsCode;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getLocalCode() {
        return localCode;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @XmlTransient
    public List<Myairportslog> getMyairportslogList() {
        return myairportslogList;
    }

    public void setMyairportslogList(List<Myairportslog> myairportslogList) {
        this.myairportslogList = myairportslogList;
    }

    @XmlTransient
    public List<Myairports> getMyairportsList() {
        return myairportsList;
    }

    public void setMyairportsList(List<Myairports> myairportsList) {
        this.myairportsList = myairportsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ident != null ? ident.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Airports)) {
            return false;
        }
        Airports other = (Airports) object;
        if ((this.ident == null && other.ident != null) || (this.ident != null && !this.ident.equals(other.ident))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.igradiski.ejb.eb.Airports[ ident=" + ident + " ]";
    }
    
}
