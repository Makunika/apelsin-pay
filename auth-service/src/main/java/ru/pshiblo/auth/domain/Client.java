package ru.pshiblo.auth.domain;

import ru.pshiblo.auth.enums.GrantType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "clients")
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String clientId;
    private String clientSecret;
    private int accessTokenExpired;
    private int refreshTokenExpired;
    private String scopes;
    private String grantTypes;

    public List<GrantType> toGrantTypes() {
        List<GrantType> result = new ArrayList<>();
        for (String grantType : grantTypes.split(",")) {
            result.add(GrantType.toGrantType(grantType));
        }
        return result;
    }
}
