package com.shop.generic.common.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.shop.generic.common.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders_audit")
public class OrderAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_audit_id_gen")
    @SequenceGenerator(name = "order_audit_id_gen", sequenceName = "order_audit_id_seq", allocationSize = 1)
    private Integer id;

    @NonNull
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NonNull
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Order order;

}
