package ru.otus.hw.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_roles")
public class UserRole {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;

        @Column(name = "role")
        private String role;

}
