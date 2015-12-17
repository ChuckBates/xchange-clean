package com.xeiam.xchange.coinbaseex;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.xeiam.xchange.coinbaseex.dto.account.CoinbaseExAccount;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExProduct;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExProductBook;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExProductStats;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExProductTicker;
import com.xeiam.xchange.coinbaseex.dto.marketdata.CoinbaseExTrade;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExFill;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExIdResponse;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExOrder;
import com.xeiam.xchange.coinbaseex.dto.trade.CoinbaseExPlaceOrder;

import si.mazi.rescu.ParamsDigest;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface CoinbaseEx {

  @GET
  @Path("products")
  List<CoinbaseExProduct> getProducts() throws IOException;

  @GET
  @Path("products/{baseCurrency}-{targetCurrency}/ticker")
  CoinbaseExProductTicker getProductTicker(@PathParam("baseCurrency") String baseCurrency, @PathParam("targetCurrency") String targetCurrency)
      throws IOException;

  @GET
  @Path("products/{baseCurrency}-{targetCurrency}/stats")
  CoinbaseExProductStats getProductStats(@PathParam("baseCurrency") String baseCurrency, @PathParam("targetCurrency") String targetCurrency)
      throws IOException;

  @GET
  @Path("products/{baseCurrency}-{targetCurrency}/book?level={level}")
  CoinbaseExProductBook getProductOrderBook(@PathParam("baseCurrency") String baseCurrency, @PathParam("targetCurrency") String targetCurrency,
      @PathParam("level") String level) throws IOException;

  @GET
  @Path("products/{baseCurrency}-{targetCurrency}/trades?limit={limit}")
  CoinbaseExTrade[] getTrades(@PathParam("baseCurrency") String baseCurrency, @PathParam("targetCurrency") String targetCurrency,
      @PathParam("limit") String limit) throws IOException;

  @GET
  @Path("fills?before={page}&limit={limit}")
  CoinbaseExFill[] getFills(@PathParam("page") String page, @PathParam("limit") String limit, @HeaderParam("CB-ACCESS-KEY") String apiKey, @HeaderParam("CB-ACCESS-SIGN") ParamsDigest signer,
      @HeaderParam("CB-ACCESS-TIMESTAMP") String timestamp, @HeaderParam("CB-ACCESS-PASSPHRASE") String passphrase);

  /** Authenticated calls */

  @GET
  @Path("accounts")
  CoinbaseExAccount[] getAccounts(@HeaderParam("CB-ACCESS-KEY") String apiKey, @HeaderParam("CB-ACCESS-SIGN") ParamsDigest signer,
      @HeaderParam("CB-ACCESS-TIMESTAMP") String timestamp, @HeaderParam("CB-ACCESS-PASSPHRASE") String passphrase);

  @GET
  @Path("orders?status={status}")
  CoinbaseExOrder[] getListOrders(@HeaderParam("CB-ACCESS-KEY") String apiKey, @HeaderParam("CB-ACCESS-SIGN") ParamsDigest signer,
      @HeaderParam("CB-ACCESS-TIMESTAMP") String timestamp, @HeaderParam("CB-ACCESS-PASSPHRASE") String passphrase,
      @PathParam("status") String status);

  @POST
  @Path("orders")
  @Consumes(MediaType.APPLICATION_JSON)
  CoinbaseExIdResponse placeLimitOrder(CoinbaseExPlaceOrder placeOrder, @HeaderParam("CB-ACCESS-KEY") String apiKey,
      @HeaderParam("CB-ACCESS-SIGN") ParamsDigest signer, @HeaderParam("CB-ACCESS-TIMESTAMP") String timestamp,
      @HeaderParam("CB-ACCESS-PASSPHRASE") String passphrase);

  @POST
  @Path("orders")
  @Consumes(MediaType.APPLICATION_JSON)
  CoinbaseExIdResponse placeMarketOrder(CoinbaseExPlaceOrder placeOrder, @HeaderParam("CB-ACCESS-KEY") String apiKey,
      @HeaderParam("CB-ACCESS-SIGN") ParamsDigest signer, @HeaderParam("CB-ACCESS-TIMESTAMP") String timestamp,
      @HeaderParam("CB-ACCESS-PASSPHRASE") String passphrase);

  @DELETE
  @Path("orders/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  void cancelOrder(@PathParam("id") String id, @HeaderParam("CB-ACCESS-KEY") String apiKey, @HeaderParam("CB-ACCESS-SIGN") ParamsDigest signer,
      @HeaderParam("CB-ACCESS-TIMESTAMP") String timestamp, @HeaderParam("CB-ACCESS-PASSPHRASE") String passphrase);

}