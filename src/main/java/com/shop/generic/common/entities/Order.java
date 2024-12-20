package com.shop.generic.common.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.shop.generic.common.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * The @RequiredArgsConstructor is used in conjunction with @NonNull to allow you to create on
 * object with only the fields annotation with NonNull. It basically lets you create an object
 * without having to specify the id, which is auto generated on persist anyways
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_gen")
    @SequenceGenerator(name = "order_id_gen", sequenceName = "order_id_seq", allocationSize = 1)
    private Integer id;

    @NonNull
    @Column(name = "ORDER_ID", nullable = false)
    private UUID orderId;

    @NonNull
    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;

    @NonNull
    @Column(name = "PRODUCT_IDS", nullable = false)
    private String productIds;

    @NonNull
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NonNull
    @Column(name = "CITY", nullable = false)
    private String city;

    @NonNull
    @Column(name = "CREATION_DATE", nullable = false)
    private LocalDateTime creationDate;

    @NonNull
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    @OrderBy("lastUpdated")
    private List<OrderAudit> auditItems = new ArrayList<>();

    //TODO: Add additional fields such as last_updated, name, address etc.

}
