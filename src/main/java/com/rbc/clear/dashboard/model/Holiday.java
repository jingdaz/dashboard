package com.rbc.clear.dashboard.model;


import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "holidays", indexes = {
        @Index(name = "idx_holiday_country", columnList = "country")}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_holiday_date_country", columnNames = {"date", "country"})
})
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private Integer year;
    @Version
    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Holiday() {
    }
    
    public Holiday(String country, String name, LocalDate date) {
        this.country = country;
        this.name = name;
        this.date = date;
        this.year = date.getYear();
    }

    public Long getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getYear() {
        return year;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
