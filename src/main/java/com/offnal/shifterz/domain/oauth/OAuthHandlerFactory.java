package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.exception.OAuthErrorCode;
import com.offnal.shifterz.global.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthHandlerFactory {
    private final Map<Provider, OAuthHandler> providerMap;

    public OAuthHandlerFactory(List<OAuthHandler> providers) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(OAuthHandler::getProviderType, Function.identity()));
    }

    public OAuthHandler getProvider(Provider type) {
        OAuthHandler provider = providerMap.get(type);
        if(provider == null){
            throw new CustomException(OAuthErrorCode.UNSUPPORTED_PROVIDER);
        }
        return provider;
    }
}
