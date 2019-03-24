package rest;

import config.JarConfig;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class FaireRequestFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext clientRequestContext) throws IOException {

        clientRequestContext.getHeaders()
                .add("X-FAIRE-ACCESS-TOKEN",
                        JarConfig.getInstance().getToken());

        clientRequestContext.getAcceptableMediaTypes().add(MediaType.APPLICATION_JSON_TYPE);
    }
}
