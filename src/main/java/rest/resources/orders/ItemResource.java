package rest.resources.orders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import rest.FaireRestClient;
import rest.pojos.ItemAwaitAvailability;
import rest.resources.BaseResource;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

public class ItemResource extends BaseResource {

    public ItemResource(FaireRestClient client, WebTarget baseResource) {
        super(client, baseResource);
    }

    public int availability(Collection<ItemAwaitAvailability> items){
        if(null == items || items.isEmpty())
            return 0 ;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.getNodeFactory().objectNode();
        items.forEach(it->{
            rootNode.set(it.getName(), mapper.convertValue(it, JsonNode.class));
        });

        Response response = baseResource.path("availability")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(rootNode.toString()), Response.class);

        return closeResponse(response);
    }

}
