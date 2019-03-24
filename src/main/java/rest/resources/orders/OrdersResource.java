package rest.resources.orders;

import config.JarConfig;
import entity.order.Order;
import entity.order.OrderState;
import rest.FaireRestClient;
import rest.resources.BaseResource;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrdersResource extends BaseResource {

    public OrdersResource(FaireRestClient client, WebTarget baseResource) {
        super(client, baseResource);
    }

    public Collection<Order> getOrders(){
        return getOrders(1);
    }

    private Collection<Order> getOrders(int page) {
        return getOrders(page, JarConfig.getInstance().getLimitOnList(), null, null, null);
    }

    private Collection<Order> getOrders(int page, Calendar updatedAtMin, Calendar shipAfterMax,
                                        OrderState... excludedStates){
        return getOrders(page, JarConfig.getInstance().getLimitOnList(),
                updatedAtMin, shipAfterMax, excludedStates);

    }

    public Collection<Order> getOrders(int page, int limit, Calendar updatedAtMin, Calendar shipAfterMax,
                                        OrderState... excludedStates){
        WebTarget base = getBaseResourceForList(page, limit);

        if(updatedAtMin != null){
            base = base.queryParam("updated_at_min", sdfISO8601.format(updatedAtMin.getTime()));
        }
        if (shipAfterMax != null) {
            base = base.queryParam("ship_after_max", sdfISO8601.format(updatedAtMin.getTime()));
        }
        if(excludedStates != null && excludedStates.length > 0){
            base = base.queryParam("excluded_states", Stream.of(excludedStates)
                    .map(it -> it.name()).collect(Collectors.joining(",")));
        }

        return readList(doGet(base), Order.class, "orders");
    }

    public void orderAccepted(String id, Order order){

        Response response = baseResource.path(id).path("processing")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(order, MediaType.APPLICATION_JSON_TYPE));

        System.out.println(response.getStatus());
        closeResponse(response);
    }


    public ItemResource itemResource(String order){
        return new ItemResource(getClient(), baseResource.path(order).path("items"));
    }


}
