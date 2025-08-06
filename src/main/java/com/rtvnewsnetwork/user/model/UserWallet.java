package com.rtvnewsnetwork.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWallet {
    private int coinBalance = 0;
    private int totalCoinsEarned = 0;
}
