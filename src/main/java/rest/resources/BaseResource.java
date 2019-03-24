package rest.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import rest.FaireRestClient;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BaseResource {

    protected WebTarget baseResource;

    private FaireRestClient client;

    public static final SimpleDateFormat sdfISO8601 = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSXXX");

    public BaseResource(FaireRestClient client, WebTarget baseResource) {
        this.baseResource = baseResource;
        this.client = client;
    }

    public FaireRestClient getClient() {
        return client;
    }

    public int closeResponse(Response response){
        if ( null != response ) {
            response.close( );
            return response.getStatus( );
        }
        // There is no code 0 in protocol. If you get 0, something is wrong.
        return 0;
    }

    public <T> List<T> readList(Response response, Class<T> base, String path)  {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(!response.hasEntity())
                return new ArrayList<>();
            JsonNode node = objectMapper.readTree(response.readEntity(String.class));
            JsonNode values = node.get(path);
            List<T> toReturn = new ArrayList<>();
            for (int i = 0 ; i < values.size(); i++){
                JsonNode value = values.get(i);
                T result = objectMapper.treeToValue(value, base);
                toReturn.add(result);
            }
            return toReturn;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }finally {
            closeResponse(response);
        }
    }

    public WebTarget getBaseResourceForList(Integer page, Integer limit){
        return baseResource.queryParam("page", page)
                .queryParam("limit", limit);
    }

    public Response doGet(WebTarget baseResource){
        return baseResource.request(MediaType.APPLICATION_JSON_TYPE).get();
    }
}
