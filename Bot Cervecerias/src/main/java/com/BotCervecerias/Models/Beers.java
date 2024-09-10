package com.BotCervecerias.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="beers")
public class Beers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private   String name;
    @ManyToOne
    @JoinColumn(name = "beers_type")
    private BeersType beersType;
    private Boolean alcohol_grad;
    private Boolean btu;
    private String description;
    private String image;
    @ManyToOne
    @JoinColumn(name = "companies")
    private Companies companies;

    public Long getCompanies() {
        return companies != null ? companies.getId():null;
    }
    public void setCompanies(Long user) {
        if (this.companies == null) {
            this.companies = new Companies();
        }
        this.companies.setId(user);
    }
    public Long getBeersTypeID() {
        return beersType != null ? beersType.getId():null;
    }
    public void setBeersTypeID(Long beersType) {
        if (this.beersType == null) {
            this.beersType = new BeersType();
        }
        this.beersType.setId(beersType);
    }
}
