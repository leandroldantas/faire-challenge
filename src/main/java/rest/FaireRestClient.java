package rest;

import config.JarConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import rest.resources.orders.OrdersResource;
import rest.resources.products.ProductsResource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.util.concurrent.TimeUnit;

public class FaireRestClient {

    private static volatile Client client;

    private static volatile WebTarget baseResource;

    private static volatile FaireRestClient rest;

    private FaireRestClient(Client client, WebTarget baseResource) {
        this.client = client;
        this.baseResource = baseResource;
    }

    public static FaireRestClient getInstance(){
        if(client == null){
            synchronized (FaireRestClient.class){
                if(client == null){
                    client = new ResteasyClientBuilder( )
                            .connectionPoolSize( 100 )
                            .connectTimeout( 10, TimeUnit.SECONDS )
                            .build( );

                    client.register(new FaireRequestFilter());

                    baseResource = client.target(JarConfig.getInstance().getUrl());

                    rest = new FaireRestClient(client, baseResource);
                }
            }
        }

        return rest;
    }

    public ProductsResource getProductsResource(){
        return new ProductsResource( this,
                baseResource.path( "products" )
        );
    }

    public OrdersResource getOrdersResource(){
        return new OrdersResource(this,
                baseResource.path("orders"));
    }

}
