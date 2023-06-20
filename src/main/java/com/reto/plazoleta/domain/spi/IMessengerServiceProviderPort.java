package com.reto.plazoleta.domain.spi;

import com.reto.plazoleta.domain.model.MessageSms;

public interface IMessengerServiceProviderPort {

    void notifyCustomerBySmsMessage(MessageSms messageSmsToSend, String tokenWithPrefixBearer);
}
