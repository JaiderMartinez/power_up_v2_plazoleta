package com.reto.plazoleta.domain.api;

import com.reto.plazoleta.domain.model.OrderModel;

public interface IOrderServicePort {

    OrderModel saveOrder(OrderModel orderModelRequest, String tokenWithPrefixBearer);
}
