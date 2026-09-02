package com.example.campbooking.vo;

public class PointsBalanceVO {
    private Integer balance;
    private Integer totalEarned;
    private Integer totalSpent;

    public PointsBalanceVO() {}

    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }
    public Integer getTotalEarned() { return totalEarned; }
    public void setTotalEarned(Integer totalEarned) { this.totalEarned = totalEarned; }
    public Integer getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Integer totalSpent) { this.totalSpent = totalSpent; }
}
