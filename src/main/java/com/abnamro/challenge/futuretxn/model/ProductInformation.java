package com.abnamro.challenge.futuretxn.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProductInformation {

  private String exchangeCode;

  private String productGroupCode;

  private String symbol;

  private Date expirationDate;
}
