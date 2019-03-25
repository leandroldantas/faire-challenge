package steps;

import entity.order.ItemOrder;
import entity.order.Order;
import entity.product.Product;
import entity.product.option.ProductOption;
import rest.FaireRestClient;
import rest.pojos.InventoryLevel;
import rest.pojos.ItemAwaitAvailability;
import util.ValidatorUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static entity.order.OrderState.*;

public class SecondStep {

    public static void runSecondStep(){

    }

    /**
     * Consumes all orders, accepting the order if there is inventory to fulfill the order
     * and it is not already accepted, otherwise it marks the items that don’t have
     * enough inventory as backordered
     */
    public static void secondExercise() {
        /**
         * Here we will consue all orders avaliable in the state NEW avaliable in the endpoint.
         */
        Collection<Order> orders = secondExerciseReadOrders();

        /**
         * Now, we will check if we can fulfill the order , if true, we can accept the order
         * and update the inventory levels and accept it.
         *
         * If false, we will mark it as backordered
         */
        orders.forEach(it -> secondExerciseCheckOrderToAccept(it));
    }


    /**
     * We are going to analize the order to see if we can accept the order.
     * Doubts : 1 - We can accept the order if one of the itens is not avaliable ?
     *
     * @param order the order that will be analize
     */
    public static void secondExerciseCheckOrderToAccept(Order order) {
        /**
         * This variable indicates the if we accepted the order
         */
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);

        Collection<ItemOrder> items = order.getItems();
        Map<ItemOrder, ProductOption> toUpdate = new HashMap<>();
        Map<ItemOrder, ProductOption> toBackordered = new HashMap<>();
        items.forEach(item -> {
            ProductOption productOption = secondExerciseFindProductOption(item);
            if (null == productOption) {
                /**
                 * Something wrong. We did not find the prodcut option for the order.
                 */
                atomicBoolean.set(false);
                toBackordered.put(item, null);
            } else {
                if (ValidatorUtils.isValidValue(item.getQuantity(), productOption.getAvailableQuantity())) {
                    if (item.getQuantity() > productOption.getAvailableQuantity()) {
                        atomicBoolean.set(false);
                        toBackordered.put(item, productOption);
                    } else {
                        toUpdate.put(item, productOption);
                    }
                } else {
                    atomicBoolean.set(false);
                    toBackordered.put(item, productOption);
                }
            }
        });

        /**
         * If we have one item in the inventory, we will accept the order
         */
        if (toUpdate.size() > 0) {
            secondExerciseAcceptOrder(order);
            secondExerciseUpdateInventory(toUpdate);
        }

        if (toBackordered.size() > 0) {
            secondExerciseBackordered(order.getId(), toBackordered);
        }
        /**
         * If we don't have all items, what I have to do ?
         */
    }


    /**
     * Here we have to reduce the avaliable quantity , let's update our inventory
     *
     * @param toUpdate
     */
    public static void secondExerciseUpdateInventory(Map<ItemOrder, ProductOption> toUpdate) {
        Collection<InventoryLevel> inventoryLevels = new ArrayList<>();
        for (Map.Entry<ItemOrder, ProductOption> it : toUpdate.entrySet()) {
            ItemOrder item = it.getKey();
            ProductOption option = it.getValue();
            inventoryLevels.add(new InventoryLevel(
                    option.getSku(),
                    option.getAvailableQuantity() - item.getQuantity(),
                    false,
                    null
            ));
        }
        FaireRestClient.getInstance().getProductsResource().getOptionsResource().updateInventoryLevels(
                inventoryLevels
        );
    }


    /**
     * Here, we accept the order. Let's say that for the server.
     *
     * @param order
     */
    public static void secondExerciseAcceptOrder(Order order) {
        FaireRestClient.getInstance().getOrdersResource()
                .orderAccepted(order.getId(), order);
    }


    /**
     * Heare are the item that we have problems to accept.
     * Let's create other order to put that itens.
     *
     * @param orderId
     * @param toUpdate
     */
    public static void secondExerciseBackordered(String orderId, Map<ItemOrder, ProductOption> toUpdate) {
        Collection<ItemAwaitAvailability> itemAwaitAvailabilities = new ArrayList<>();
        for (Map.Entry<ItemOrder, ProductOption> it : toUpdate.entrySet()) {
            ItemOrder item = it.getKey();
            ProductOption option = it.getValue();
            itemAwaitAvailabilities.add(new ItemAwaitAvailability(
                    item.getId(),
                    option.getAvailableQuantity(),
                    null != option.getActive() ? !option.getActive() : false,
                    option.getBackorderedUntil()
            ));
        }
        FaireRestClient.getInstance().getOrdersResource().itemResource(orderId).availability(
                itemAwaitAvailabilities
        );

    }

    /**
     * We receive a ordem with a lot of itens, each item, we have to get a product and after
     * get the item indicated in the order, to analize if we have the quantity needed avaliable
     * in our inventory.
     *
     * @param item
     * @return
     */
    public static ProductOption secondExerciseFindProductOption(ItemOrder item) {
        Collection<Product> products = FaireRestClient.getInstance().getProductsResource().getProducts(item.getSku());
        products = products.stream().filter(p -> p.getId().equals(item.getProductId())).collect(Collectors.toList());
        if (products.size() == 1) {
            return products.iterator().next().getOptions().stream()
                    .filter(opt -> opt.getId().equals(item.getProductOptionId()))
                    .findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Let's find the orders in the state NEW in the server to proccess
     *
     * @return
     */
    public static Collection<Order> secondExerciseReadOrders() {
        Collection<Order> result = new ArrayList<>();

        for (int i = 0; ; i++) {
            Collection<Order> response = FaireRestClient.getInstance().getOrdersResource()
                    .getOrders(i + 1, 50, null, null, CANCELED, PROCESSING, PRE_TRANSIT,
                            IN_TRANSIT, DELIVERED, BACKORDERED, CANCELED);
            if (response.isEmpty()) {
                break;
            }
            result.addAll(response);
        }

        return result;
    }


}
