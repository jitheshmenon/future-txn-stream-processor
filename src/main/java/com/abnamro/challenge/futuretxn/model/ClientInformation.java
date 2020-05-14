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
public class ClientInformation {
  private String clientType;

  private int clientNumber;

  private int accountNumber;

  private int subAccountNumber;
}
