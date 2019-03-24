import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.product.Product;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;

public class TestProduct {

    public static final String resultToJson = "{\"id\":\"p_123\",\"brand_id\":\"brand_id\",\"short_description\":\"v\",\"description\":\"description\",\"wholesale_price_cents\":100,\"retail_price_cents\":200,\"active\":true,\"name\":\"Faire\",\"unit_multiplier\":2,\"created_at\":\"20190423T235739.293Z\",\"updated_at\":\"20190423T235739.293Z\"}";

    public static Calendar dateReference;

    static {
        dateReference = Calendar.getInstance();
        dateReference.set(Calendar.YEAR, 2019);
        dateReference.set(Calendar.MONTH, 3);
        dateReference.set(Calendar.DAY_OF_MONTH, 23);
        dateReference.set(Calendar.HOUR_OF_DAY, 20);
        dateReference.set(Calendar.MINUTE, 57);
        dateReference.set(Calendar.SECOND, 39);
        dateReference.set(Calendar.MILLISECOND, 293);
    }

    @Test
    public void testTo() throws JsonProcessingException {
        Product product = new Product();

        product.setId("p_123");
        product.setBrandId("brand_id");
        product.setShortDescription("v");
        product.setDescription("description");
        product.setWholesalePriceCents(100l);
        product.setRetailPriceCents(200l);
        product.setActive(true);
        product.setName("Faire");
        product.setUnitMultiplier(2);
        product.setCreatedAt(dateReference);
        product.setUpdatedAt(dateReference);

        ObjectMapper mapper = new ObjectMapper();

        Assert.assertEquals(resultToJson,
                mapper.writeValueAsString(product));

    }

    @Test
    public void testFromJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Product product = mapper.readValue(this.getClass().getResourceAsStream("product.json"), Product.class);

        System.out.println(product);
    }
}
