package steps;

import entity.product.Product;
import entity.product.option.ProductOption;
import rest.FaireRestClient;
import rest.pojos.InventoryLevel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class FirstStep {


    /**
     * Consumes all products for a given brand*2, recording the inventory levels
     * for each product option
     */
    public static void runFirstStep(){
        Collection<Product> result = firstExerciseReadProducts();
        result.forEach(it -> firstExerciseUpdateInventory(it.getOptions()));
    }


    /**
     * Let's consume all products avaliable in the endpoint
     *
     * @return Collection of Products avaliable in the endpoint
     */
    public static Collection<Product> firstExerciseReadProducts() {
        Collection<Product> result = new ArrayList<>();

        for (int i = 0; ; i++) {
            Collection<Product> response = FaireRestClient.getInstance().getProductsResource().getProducts(i + 1);
            if (response.isEmpty()) {
                break;
            }
            result.addAll(response);
        }

        return result;
    }

    /**
     * Here we are doing "recording the inventory levels for each product option"
     * mentioned in the clallenge
     *
     * @param options A collection of the product option to update the inventory
     */
    public static void firstExerciseUpdateInventory(Collection<ProductOption> options) {
        Collection<ProductOption> optionsResult = options.stream()
                .filter(opt -> opt.getActive().booleanValue())
                .filter(opt -> opt.getAvailableQuantity() != null && opt.getAvailableQuantity().intValue() > 0)
                .collect(Collectors.toList());
        if (!optionsResult.isEmpty()) {
            FaireRestClient.getInstance().getProductsResource().getOptionsResource().updateInventoryLevels(
                    optionsResult.stream().map(m -> new InventoryLevel(m.getSku(), m.getAvailableQuantity(),
                            m.getActive(), null)).collect(Collectors.toList())
            );
        }
    }



}
