package com.zentoodevs.login.repository.callbacks;

import com.zentoodevs.login.repository.models.BalanceResponse;

public interface BalanceCallback {
    void onSuccess(BalanceResponse saldoResponse);

    void onError(Exception e);
}
