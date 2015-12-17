package com.xeiam.xchange.coinbaseex.service.polling;

import java.io.IOException;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.coinbaseex.CoinbaseExAdapters;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExIdResponse;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExOrder;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.UserTrades;
import com.xeiam.xchange.exceptions.ExchangeException;
import com.xeiam.xchange.exceptions.NotAvailableFromExchangeException;
import com.xeiam.xchange.exceptions.NotYetImplementedForExchangeException;
import com.xeiam.xchange.service.polling.trade.PollingTradeService;
import com.xeiam.xchange.service.polling.trade.params.DefaultTradeHistoryParamPaging;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParamPaging;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParams;

public class CoinbaseExTradeService extends CoinbaseExTradeServiceRaw implements PollingTradeService {

  public CoinbaseExTradeService(Exchange exchange) {

    super(exchange);
  }

  @Override
  public OpenOrders getOpenOrders() throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {

    CoinbaseExOrder[] coinbaseExOpenOrders = getCoinbaseExOpenOrders();

    return CoinbaseExAdapters.adaptOpenOrders(coinbaseExOpenOrders);
  }

  protected CoinbaseExOrder[] getRejectedOrders() throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {

    return getCoinbaseExRejectedOrders();
  }

  @Override
  public String placeMarketOrder(MarketOrder marketOrder)
      throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {

    CoinbaseExIdResponse response = placeCoinbaseExMarketOrder(marketOrder);

    return response.getId();
  }

  @Override
  public String placeLimitOrder(LimitOrder limitOrder)
      throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {

    CoinbaseExIdResponse response = placeCoinbaseExLimitOrder(limitOrder);

    return response.getId();
  }

  @Override
  public boolean cancelOrder(String orderId)
      throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {

    return cancelCoinbaseExOrder(orderId);
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {

    if (params instanceof TradeHistoryParamPaging) {
      return getCoinbaseExTradeHistory((TradeHistoryParamPaging) params);
    }

    return getCoinbaseExTradeHistory((TradeHistoryParamPaging) createTradeHistoryParams());
  }

  @Override
  public TradeHistoryParams createTradeHistoryParams() {

    DefaultTradeHistoryParamPaging params = new DefaultTradeHistoryParamPaging();
    params.setPageNumber(1);
    params.setPageLength(100);
    return params;
  }
}
