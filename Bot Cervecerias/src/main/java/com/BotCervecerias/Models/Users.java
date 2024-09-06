package com.BotCervecerias.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String name;
    @Email
    @NotBlank
    private  String email;
    private  String password;
    @ManyToOne
    @JoinColumn(name = "user_type")
    private UsersType usersType;
    private Date created_date=new Date(System.currentTimeMillis());

    public void setUsersTypeName(String usersType) {
        if (this.usersType == null) {
            this.usersType = new UsersType();
        }
        this.usersType.setName(usersType);
    }

    public String getUsersTypeName() {
        return usersType != null ? usersType.getName():null;
    }
}
