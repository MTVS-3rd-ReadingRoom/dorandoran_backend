package org.dorandoran.dorandoran_backend.user;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@Getter
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private String name;

    @Column (nullable = false)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public UserInfo(String name, String id, String password, String email, LocalDateTime createdAt) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
    }

    public UserInfo() {

    }
}
