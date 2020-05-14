package com.abnamro.challenge.futuretxn.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.abnamro.challenge.futuretxn.model.InputRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class InputValueMapperTest {

  private InputValueMapper inputValueMapper;

  @BeforeEach
  public void setUp() {
    inputValueMapper = new InputValueMapper();
    ReflectionTestUtils.setField(inputValueMapper, "fixedLengthTokenizer", inputValueMapper.fixedLengthTokenizer());
  }

  @Test
  public void test_apply_valid() {

    InputRecord inputRecord =
        inputValueMapper.apply("315CL  432100020001SGXDC FUSGX NK    20100910JPY01B 0000000001 "
            + "0000000000000000000060DUSD000000000030DUSD000000000000DJPY201008200012380     688032000"
            + "092500000000             O");
    assertEquals("688032", inputRecord.getExternalNumber());
    assertNotNull(inputRecord.getClientInformation());
    assertEquals("CL", inputRecord.getClientInformation().getClientType());
  }

  @Test
  public void test_apply_invalid_message() {

    Exception exception =
      assertThrows(IllegalArgumentException.class, () -> inputValueMapper.apply("20100910JPY01B 0000000001"));
    assertEquals("The input data is missing or in an invalid format", exception.getMessage());
  }

  @Test
  public void test_apply_null_message() {

    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> inputValueMapper.apply(null));
    assertEquals("The input data is missing or in an invalid format", exception.getMessage());
  }

  @Test
  public void test_apply_empty_message() {

    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> inputValueMapper.apply(""));
    assertEquals("The input data is missing or in an invalid format", exception.getMessage());
  }

  @Test
  void fixedLengthTokenizer() {
  }
}