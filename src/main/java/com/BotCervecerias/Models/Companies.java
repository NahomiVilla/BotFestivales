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
@Table(name="companies")
public class Companies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private  String tipo;
    private String logo;
    private String addres;
    @ManyToOne
    @JoinColumn(name = "users")
    private Users users;

    public Long getUsers() {
        return users != null ? users.getId():null;
    }
    public void setUsers(Long user) {
        if (this.users == null) {
            this.users = new Users();
        }
        this.users.setId(user);
    }
}
