package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.Transaction;

public interface TransactionService {
    boolean existsBySessionId(String sessionId);

    Transaction save(Transaction transaction);
}
