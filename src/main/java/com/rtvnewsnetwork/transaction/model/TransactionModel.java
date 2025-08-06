package com.rtvnewsnetwork.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transaction")
public class TransactionModel {

    @Id
    private String transactionId;

    private String userId;

    private TransactionType transactionType;

    private int amount;

    @CreatedDate
    private Instant createdAt = Instant.now();
}
