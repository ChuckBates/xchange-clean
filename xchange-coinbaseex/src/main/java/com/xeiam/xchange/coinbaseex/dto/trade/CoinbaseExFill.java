package com.xeiam.xchange.coinbaseex.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * User: Chuck Bates
 */
public class CoinbaseExFill {
    private final long tradeId;
    private final String productId;
    private final String orderId;
    private final String userId;
    private final String profileId;
    private final String liquidity;
    private final BigDecimal price;
    private final BigDecimal size;
    private final BigDecimal fee;
    private final String createdAt;
    private final String side;
    private final boolean settled;

    public CoinbaseExFill(@JsonProperty("trade_id") long tradeId, @JsonProperty("product_id") String productId, @JsonProperty("order_id") String orderId,
                            @JsonProperty("user_id") String userId, @JsonProperty("profile_id") String profileId, @JsonProperty("liquidity") String liquidity,
                            @JsonProperty("price") BigDecimal price, @JsonProperty("size") BigDecimal size, @JsonProperty("fee") BigDecimal fee,
                            @JsonProperty("created_at") String createdAt, @JsonProperty("side") String side, @JsonProperty("settled") boolean settled) {
        this.tradeId = tradeId;
        this.productId = productId;
        this.orderId = orderId;
        this.userId = userId;
        this.profileId = profileId;
        this.liquidity = liquidity;
        this.price = price;
        this.size = size;
        this.fee = fee;
        this.createdAt = createdAt;
        this.side = side;
        this.settled = settled;
    }

    public long getTradeId() {
        return tradeId;
    }

    public String getProductId() {
        return productId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getLiquidity() {
        return liquidity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSize() {
        return size;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getSide() {
        return side;
    }

    public boolean isSettled() {
        return settled;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CoinbaseExFill [tradeId=");
        builder.append(tradeId);
        builder.append(", productId=");
        builder.append(productId);
        builder.append(", orderId=");
        builder.append(orderId);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", profileId=");
        builder.append(profileId);
        builder.append(", liquidity=");
        builder.append(liquidity);
        builder.append(", price=");
        builder.append(price);
        builder.append(", size=");
        builder.append(size);
        builder.append(", fee=");
        builder.append(fee);
        builder.append(", createdAt=");
        builder.append(createdAt);
        builder.append(", side=");
        builder.append(side);
        builder.append(", settled=");
        builder.append(settled);
        builder.append("]");
        return builder.toString();
    }
}