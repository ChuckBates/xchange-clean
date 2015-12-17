package com.xeiam.xchange.coinbaseex.service.polling;

import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.coinbaseex.CoinbaseExExchange;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExOrder;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.UserTrade;
import com.xeiam.xchange.service.polling.trade.params.DefaultTradeHistoryParamPaging;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParamPaging;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.junit.Assert.*;


/**
 * User: Chuck Bates
 */
public class CoinbaseExTradeServiceTest {

    CoinbaseExExchange exchange;
    TradeHistoryParamPaging params;
    ExchangeSpecification buyExchangeSpecification;
    ExchangeSpecification sellExchangeSpecification;
    CurrencyPair currencyPair;

    @Before
    public void setUp() throws Exception {
        buyExchangeSpecification = new ExchangeSpecification(this.getClass().getCanonicalName());
        buyExchangeSpecification.setSslUri("https://api-public.sandbox.exchange.coinbase.com");
        buyExchangeSpecification.setApiKey("e3184777012be27817bf1109f4dbe3b8");
        buyExchangeSpecification.setExchangeSpecificParametersItem("passphrase", "esjhvoy1yb7kqpvi");
        buyExchangeSpecification.setSecretKey("fHkoVqPlBo/MbbiOXg2aLNq6gnzooxSx/Q6Vo1bnHjhjvKqAcWInicJ8uDnjUWsx/9OhKQpy4sczlhlsB7FsFw==");

        sellExchangeSpecification = new ExchangeSpecification(this.getClass().getCanonicalName());
        sellExchangeSpecification.setSslUri("https://api-public.sandbox.exchange.coinbase.com");
        sellExchangeSpecification.setApiKey("8cf5354f1b9422ede10ea85aab709b0e");
        sellExchangeSpecification.setExchangeSpecificParametersItem("passphrase", "jlzfcvn0bbkjra4i");
        sellExchangeSpecification.setSecretKey("ojcFm4pwYIe11XlEAYHqqa8At3kJ35zVNTFAV76qwuR0joNg5C86B+8H0nrvH5pw0Bb95vHK45272HXqKRi1QQ==");

        exchange = new CoinbaseExExchange();

        params = new DefaultTradeHistoryParamPaging(100, 1);
        currencyPair = new CurrencyPair("BTC", "USD");
    }

    @Test
    public void testGetTradeHistoryNotNull() throws IOException {
        exchange.applySpecification(buyExchangeSpecification);
        assertNotNull(exchange.getPollingTradeService().getTradeHistory(params).getUserTrades());
    }

    @Test
    public void testGetTradeHistorySize() throws Exception {
        exchange.applySpecification(buyExchangeSpecification);
        assertTrue(exchange.getPollingTradeService().getTradeHistory(params).getUserTrades().size() > 0);
    }

    @Test
    public void testGetTradeHistoryUserTradeFields() throws Exception {
        exchange.applySpecification(buyExchangeSpecification);
        UserTrade userTrade = exchange.getPollingTradeService().getTradeHistory(params).getUserTrades().get(0);

        assertTrue(!isEmpty(userTrade.getOrderId()));
        assertTrue(userTrade.getFeeAmount().doubleValue() >= BigDecimal.ZERO.doubleValue());
        assertNotNull(userTrade.getFeeCurrency());

        assertNotNull(userTrade.getType());
        assertTrue(userTrade.getTradableAmount().doubleValue() >= BigDecimal.ZERO.doubleValue());
        assertNotNull(userTrade.getCurrencyPair());
        assertTrue(userTrade.getPrice().doubleValue() >= BigDecimal.ZERO.doubleValue());
        assertTrue(userTrade.getTimestamp().before(new Date()));
        assertTrue(!isEmpty(userTrade.getId()));
    }

    @Test
    public void testListOrderAndMarketOrderFill() throws Exception {
        exchange.applySpecification(buyExchangeSpecification);
        CoinbaseExTradeService tradeService = new CoinbaseExTradeService(exchange);

        BigDecimal buyTradableAmount = new BigDecimal(1).setScale(8, RoundingMode.HALF_UP);
        BigDecimal buyLimitPrice = new BigDecimal(225);
        LimitOrder buyLimitOrder = new LimitOrder(Order.OrderType.BID, buyTradableAmount, currencyPair, "", new Date(), buyLimitPrice);
        String buyLimitOrderId = tradeService.placeLimitOrder(buyLimitOrder);
        assertTrue("No response from exchange", !isEmpty(buyLimitOrderId));

        CoinbaseExOrder[] rejectedOrders = ((CoinbaseExTradeService) exchange.getPollingTradeService()).getRejectedOrders();
        for (CoinbaseExOrder order : rejectedOrders) {
            assertFalse("Buy Limit Order has been rejected", buyLimitOrderId.equals(order.getId()));
        }

        exchange.applySpecification(sellExchangeSpecification);
        BigDecimal sellTradableAmount = new BigDecimal(1).setScale(8, RoundingMode.HALF_UP);
        MarketOrder sellMarketOrder = new MarketOrder(Order.OrderType.ASK, sellTradableAmount, currencyPair);
        String sellMarketOrderId = exchange.getPollingTradeService().placeMarketOrder(sellMarketOrder);

        rejectedOrders = ((CoinbaseExTradeService) exchange.getPollingTradeService()).getRejectedOrders();
        for (CoinbaseExOrder order : rejectedOrders) {
            assertFalse("Sell Market Order has been rejected", sellMarketOrderId.equals(order.getId()));
        }

        List<UserTrade> sellAllUserTrades = exchange.getPollingTradeService().getTradeHistory(params).getUserTrades();
        List<UserTrade> relevantSellUserTrades = new ArrayList<>();
        for (UserTrade userTrade : sellAllUserTrades) {
            if (userTrade.getOrderId().equals(sellMarketOrderId)) {
                relevantSellUserTrades.add(userTrade);
            }
        }

        assertTrue("No trades for Market Order, not yet filled or rejected", !relevantSellUserTrades.isEmpty());

        BigDecimal filledAmount = BigDecimal.ZERO;
        for (UserTrade userTrade : relevantSellUserTrades) {
            filledAmount = filledAmount.add(userTrade.getTradableAmount());
        }
        filledAmount = filledAmount.setScale(8, RoundingMode.HALF_UP);

        assertEquals("filled amount does not equal tradable amount", sellTradableAmount, filledAmount);
    }

    @Test
    public void testListOrderIsOpen() throws Exception {
        exchange.applySpecification(buyExchangeSpecification);
        CoinbaseExTradeService tradeService = new CoinbaseExTradeService(exchange);

        BigDecimal tradableAmount = new BigDecimal(0.1).setScale(8, RoundingMode.HALF_UP);
        BigDecimal limitPrice = new BigDecimal(0.1);
        LimitOrder limitOrder = new LimitOrder(Order.OrderType.BID, tradableAmount, currencyPair, "", new Date(), limitPrice);
        String response = tradeService.placeLimitOrder(limitOrder);
        assertTrue("No response from exchange", !isEmpty(response));

        CoinbaseExOrder[] rejectedOrders = ((CoinbaseExTradeService) exchange.getPollingTradeService()).getRejectedOrders();
        for (CoinbaseExOrder order : rejectedOrders) {
            assertFalse("Limit Order has been rejected", response.equals(order.getId()));
        }

        OpenOrders openOrders = exchange.getPollingTradeService().getOpenOrders();
        boolean isOrderOpen = false;
        for (LimitOrder order : openOrders.getOpenOrders()) {
            if (response.equals(order.getId())) {
                isOrderOpen = true;
            }
        }
        assertTrue("Limit Order is not open", isOrderOpen);

        boolean isCancelled = exchange.getPollingTradeService().cancelOrder(response);
        assertTrue("Limit Order not cancelled", isCancelled);
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
}