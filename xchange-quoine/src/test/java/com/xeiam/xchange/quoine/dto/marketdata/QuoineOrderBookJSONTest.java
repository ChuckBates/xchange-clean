package com.xeiam.xchange.quoine.dto.marketdata;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test QuoineOrderBook JSON parsing
 */
public class QuoineOrderBookJSONTest {

  @Test
  public void testUnmarshal() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = QuoineOrderBookJSONTest.class.getResourceAsStream("/marketdata/example-depth-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    QuoineOrderBook quoineOrderBook = mapper.readValue(is, QuoineOrderBook.class);

    // Verify that the example data was unmarshalled correctly

    assertThat(quoineOrderBook.getBuyPriceLevels().get(0).get(0)).isEqualTo(new BigDecimal("226.69718")); // first bid price
    assertThat(quoineOrderBook.getBuyPriceLevels().get(0).get(1)).isEqualTo(new BigDecimal("0.02000")); // first bid amount
    assertThat(quoineOrderBook.getSellPriceLevels().get(0).get(0)).isEqualTo(new BigDecimal("226.95718")); // first ask price
    assertThat(quoineOrderBook.getSellPriceLevels().get(0).get(1)).isEqualTo(new BigDecimal("0.02000")); // first ask amount
  }
}
