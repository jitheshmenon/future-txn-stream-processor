package com.abnamro.challenge.futuretxn.model;

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
public class InputRecord {

  private String externalNumber;

  private ClientInformation clientInformation;

  private ProductInformation productInformation;

  private double transactionAmount;
}
