package com.reto.plazoleta.domain.spi.clients;

import com.reto.plazoleta.domain.model.MessageSmsModel;

public interface IMessengerServiceProviderPort {

    void notifyCustomerBySmsMessage(MessageSmsModel messageSmsToSend, String tokenWithPrefixBearer);
}
