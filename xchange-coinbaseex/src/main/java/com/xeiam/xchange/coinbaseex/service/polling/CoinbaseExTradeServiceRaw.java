package com.xeiam.xchange.coinbaseex.service.polling;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.coinbaseex.CoinbaseEx;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExFill;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExIdResponse;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExOrder;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExPlaceOrder;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.dto.trade.UserTrade;
import com.xeiam.xchange.dto.trade.UserTrades;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParamPaging;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CoinbaseExTradeServiceRaw extends CoinbaseExBasePollingService<CoinbaseEx> {

  public CoinbaseExTradeServiceRaw(Exchange exchange) {

    super(CoinbaseEx.class, exchange);
  }

  public CoinbaseExOrder[] getCoinbaseExOpenOrders() {
    return coinbaseEx.getListOrders(apiKey, digest, getTimestamp(), passphrase, "open");
  }


  protected CoinbaseExOrder[] getCoinbaseExRejectedOrders() {
    return coinbaseEx.getListOrders(apiKey, digest, getTimestamp(), passphrase, "rejected");
  }

  public CoinbaseExIdResponse placeCoinbaseExLimitOrder(LimitOrder limitOrder) {

    String side = limitOrder.getType().equals(OrderType.BID) ? "buy" : "sell";
    String productId = limitOrder.getCurrencyPair().base.getCurrencyCode() + "-" + limitOrder.getCurrencyPair().counter.getCurrencyCode();

    return coinbaseEx.placeLimitOrder(new CoinbaseExPlaceOrder(limitOrder.getTradableAmount(), limitOrder.getLimitPrice(), side, productId, "limit"), apiKey,
            digest, getTimestamp(), passphrase);
  }

  public CoinbaseExIdResponse placeCoinbaseExMarketOrder(MarketOrder marketOrder) {

    String side = marketOrder.getType().equals(OrderType.BID) ? "buy" : "sell";
    String productId = marketOrder.getCurrencyPair().base.getCurrencyCode() + "-" + marketOrder.getCurrencyPair().counter.getCurrencyCode();

    return coinbaseEx.placeMarketOrder(new CoinbaseExPlaceOrder(marketOrder.getTradableAmount(), null, side, productId, "market"), apiKey,
            digest, getTimestamp(), passphrase);
  }

  public boolean cancelCoinbaseExOrder(String id) {
    try {
      coinbaseEx.cancelOrder(id, apiKey, digest, getTimestamp(), passphrase);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public UserTrades getCoinbaseExTradeHistory(TradeHistoryParamPaging params) throws IOException {

    List<UserTrade> trades = new ArrayList<>();
    CoinbaseExFill[] fills = coinbaseEx.getFills(params.getPageNumber().toString(), params.getPageLength().toString(), apiKey, digest, getTimestamp(), passphrase);

    for (CoinbaseExFill fill : fills) {
      OrderType type = fill.getSide().equals("sell") ? OrderType.ASK : OrderType.BID;
      CurrencyPair currencyPair = new CurrencyPair(fill.getProductId().replace("-", "/"));

      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSX", Locale.ENGLISH);
      Date timestamp;
      try {
        timestamp = dateFormat.parse(fill.getCreatedAt());
      } catch (ParseException e) {
        return null;
      }

      UserTrade trade = new UserTrade(type, fill.getSize(), currencyPair, fill.getPrice(), timestamp,
                             String.valueOf(fill.getTradeId()), fill.getOrderId(), fill.getFee(), currencyPair.base);
      trades.add(trade);
    }

    return new UserTrades(trades, Trades.TradeSortType.SortByTimestamp);
  }
}
