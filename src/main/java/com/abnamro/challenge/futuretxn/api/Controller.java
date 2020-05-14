package com.abnamro.challenge.futuretxn.api;

import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.requests.ApiError;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
public class Controller {

  @Autowired
  private InteractiveQueryService interactiveQueryService;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/txn-amount",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation("Provide Frequent Flyers with the ability to claim points for their flights.")
  @ApiResponses({
      @ApiResponse(code = SC_ACCEPTED, message = "Accepted", response = Map.class),
      @ApiResponse(code = SC_BAD_REQUEST, message = "Bad Request", response = ApiError.class),
      @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal Server Error", response = ApiError.class)
  })
  public Map<String, Double> txnAmountByClientAndProduct() {
    log.info("Request for txnAmountByClientAndProduct");
    final ReadOnlyKeyValueStore<String, Double> prodTotal =
        interactiveQueryService.getQueryableStore("customer_product_total", QueryableStoreTypes.<String, Double>keyValueStore());
    return getMap(prodTotal.all());
  }

  private Map<String, Double> getMap(final KeyValueIterator<String, Double> prodTotal) {
    final Map<String, Double> results = new HashMap<>();
    while (prodTotal.hasNext()) {
      final KeyValue<String, Double> next = prodTotal.next();
      results.put(next.key, next.value);
    }
    return results;
  }
}
