package com.reto.plazoleta.infraestructure.drivenadapter.gateways.adapter;

import com.reto.plazoleta.domain.model.MessageSmsModel;
import com.reto.plazoleta.domain.spi.clients.IMessengerServiceProviderPort;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.dto.request.MessageSmsRequestDto;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.dto.response.MessageSmsResponseDto;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.exceptions.MessagingApiFailedException;
import com.reto.plazoleta.infraestructure.drivenadapter.gateways.mapper.IMessengerServiceRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MessengerServiceAdapter implements IMessengerServiceProviderPort {

    @Qualifier("webClientMessengerService")
    private final WebClient webClient;
    private final IMessengerServiceRequestMapper messengerServiceRequestMapper;

    @Override
    public void notifyCustomerBySmsMessage(MessageSmsModel messageSmsToSend, String tokenWithPrefixBearer) {
        MessageSmsRequestDto messageSmsRequestDto = this.messengerServiceRequestMapper.messageSmsToMessageSmsRequestDto(messageSmsToSend);
        this.webClient.post().uri(uriBuilder -> uriBuilder.path("notify").build())
                .header(HttpHeaders.AUTHORIZATION, tokenWithPrefixBearer)
                .bodyValue(messageSmsRequestDto)
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().equals(HttpStatus.CREATED)) {
                        return clientResponse.bodyToMono(MessageSmsResponseDto.class);
                    } else {
                        return Mono.error(MessagingApiFailedException::new);
                    }
                })
                .block();
    }
}
