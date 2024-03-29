package com.xeiam.xchange.blockchain;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.xeiam.xchange.blockchain.dto.BitcoinAddress;
import com.xeiam.xchange.blockchain.dto.BitcoinAddresses;

/**
 * @author Tim Molter
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface Blockchain {

  @GET
  @Path("address/{address}?format=json")
  public BitcoinAddress getBitcoinAddress(@PathParam("address") String address) throws IOException;

  /**
   * @param addresses - Pipe (|) separated addresses
   * @return
   */
  @GET
  @Path("multiaddr")
  public BitcoinAddresses getBitcoinAddresses(@QueryParam("active") String addresses) throws IOException;
}
