package com.banking.model;

import java.math.BigDecimal;

public interface Transferable {
    void transfer(Account targetAccount, BigDecimal amount);
}
