package org.anas.bidderx_rest.service.dto.mapper;


import org.anas.bidderx_rest.domain.Transaction;
import org.anas.bidderx_rest.service.dto.TransactionDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDTO toDto(Transaction transaction);
    List<TransactionDTO> toDtoList(List<Transaction> transactions);
}
