package com.xeiam.xchange.quoine.dto.trade;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test QuoineOrderDetailsResponse JSON parsing
 */
public class QuoineOrderResponseJSONTest {

  @Test
  public void testUnmarshal() throws IOException {

    // Read in the JSON from the example resources
    InputStream is = QuoineOrderResponseJSONTest.class.getResourceAsStream("/trade/example-order-response.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();
    QuoineOrderResponse qoineOrderResponse = mapper.readValue(is, QuoineOrderResponse.class);

    // Verify that the example data was unmarshalled correctly
    assertThat(qoineOrderResponse.getId()).isEqualTo("52351");
    assertThat(qoineOrderResponse.getQuantity()).isEqualTo(new BigDecimal(".1"));
    assertThat(qoineOrderResponse.getCreatedAt()).isEqualTo("2015-04-25T08:20:40+00:00");
    assertThat(qoineOrderResponse.getOrderType()).isEqualTo("limit");
  }
}
