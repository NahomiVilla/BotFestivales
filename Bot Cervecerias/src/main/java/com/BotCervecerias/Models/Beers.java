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
    private BeersType beersType;
    private Boolean alcohol_grad;
    private Boolean btu;
    private String description;
    private String image;
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
}
