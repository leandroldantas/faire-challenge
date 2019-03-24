package rest.resources.products;

import config.JarConfig;
import entity.product.Product;
import rest.FaireRestClient;
import rest.resources.BaseResource;
import rest.resources.products.options.OptionsResource;
import util.ValidatorUtils;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProductsResource extends BaseResource {

    public ProductsResource(FaireRestClient client, WebTarget baseResource) {
        super(client, baseResource);
    }

    public Collection<Product> getProducts() {
        return getProducts(1);
    }

    public Collection<Product> getProducts(Integer page) {
        return getProducts(page, JarConfig.getInstance().getLimitOnList(), null);
    }

    public Collection<Product> getProducts(String sku) {
        List<Product> toReturn = new ArrayList<>();
        int limit = JarConfig.getInstance().getLimitOnList().intValue();
        for (int i = 0; ; i++) {
            Collection<Product> result = getProducts(i + 1, limit, sku);
            if (result.isEmpty()) {
                return toReturn;
            }
            toReturn.addAll(result);
            if(result.size() < limit)
                return toReturn;
        }

    }

    public Collection<Product> getProducts(Integer page, Integer limit, String sku) {
        WebTarget base = getBaseResourceForList(page, limit);

        if (ValidatorUtils.isValidValue(sku)) {
            base = base.queryParam("sku", sku);
        }
        return readList(doGet(base), Product.class, "products");
    }


    public OptionsResource getOptionsResource() {
        return new OptionsResource(getClient(), baseResource.path("options"));
    }


}
