package rest.resources.products.options;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import rest.FaireRestClient;
import rest.pojos.InventoryLevel;
import rest.resources.BaseResource;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

public class OptionsResource extends BaseResource {


    public OptionsResource(FaireRestClient client, WebTarget baseResource) {
        super(client, baseResource);
    }

    public int updateInventoryLevels(Collection<InventoryLevel> levels){
        if(null == levels || levels.isEmpty())
            return 0;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.getNodeFactory().objectNode();
        ArrayNode arrayNode = mapper.createArrayNode();
        levels.forEach(it->{
            arrayNode.add(mapper.convertValue(it, JsonNode.class));
        });
        rootNode.set("inventories", arrayNode);

        Response response = baseResource.path("inventory-levels").request(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", Entity.json(rootNode.toString()), Response.class);

        return closeResponse(response);
    }

}
