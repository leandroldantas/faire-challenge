import config.JarConfig;
import entity.order.ItemOrder;
import entity.order.Order;
import entity.order.OrderState;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static entity.order.OrderState.*;
import static java.lang.Integer.parseInt;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("We need some values to start.");
            System.out.println("1 - TOKEN that will be used");
            System.out.println("OR");
            System.out.println("1 - URL that will be used");
            System.out.println("2 - Token that will be used");
            System.out.println("3 - Limit for rest list's. Default will be 50");
        }

        if (args.length == 1) {
            JarConfig.setInstance("https://www.faire-stage.com/api/v1", args[0], 50);
        } else if (args.length >= 2) {
            JarConfig.setInstance(args[0], args[1], args.length >= 3 ? parseInt(args[2]) : 50);
        } else {
            System.out.println("Bye !!!");
            System.exit(0);
        }


        firstExercise();
        secondExercise();
        thirthExercize();

    }

    /**
     * Consumes all products for a given brand*2, recording the inventory levels
     * for each product option
     */
    public static void firstExercise() {
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

        if (atomicBoolean.get()) {
            secondExerciseAcceptOrder(order);
            secondExerciseUpdateInventory(toUpdate);
        } else {
            secondExerciseBackordered(order.getId(), toBackordered);
        }
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


    /**
     * After all orders have been read, the program should print the following metrics,
     * plus 2 of your choosing, and then exit - your creativity is welcome!
     * The best selling product option
     * The largest order by dollar amount
     * The state with the most orders
     */
    public static void thirthExercize() {
        /**
         * Let's get all orders that are in processing to print the metrics
         */
        Collection<Order> orders = thirthExercizeReadAllOrders();
        thirthExercizeBestSellingProductOption(orders);
        thirthExercizeLargestOrder(orders);
        thirthExercizeStatesOfTheOrders();
        thirthExercizeLargestProductionOptionQtySellgingInOneOrder(orders);
        thirthExercizeTheOrderWithMoreItens(orders);
    }

    /**
     * Let's find the orders in the state NEW in the server to proccess
     *
     * @return
     */
    public static Collection<Order> thirthExercizeReadAllOrders() {
        Collection<Order> result = new ArrayList<>();

        for (int i = 0; ; i++) {
            Collection<Order> response = FaireRestClient.getInstance().getOrdersResource()
                    .getOrders(i + 1, 50, null, null, NEW, CANCELED, PRE_TRANSIT,
                            IN_TRANSIT, DELIVERED, BACKORDERED, CANCELED);
            if (response.isEmpty()) {
                break;
            }
            result.addAll(response);
        }

        return result;
    }

    public static void thirthExercizeBestSellingProductOption(Collection<Order> orders) {
        Collection<ItemOrder> itens = orders.stream().flatMap(it -> it.getItems().stream()).collect(Collectors.toList());
        Map<String, Long> best = new HashMap<>();
        AtomicReference<String> bestOption = new AtomicReference<>();
        AtomicLong count = new AtomicLong(0l);
        itens.forEach(it -> {
            if (best.containsKey(it.getProductOptionId())) {
                Long value = best.get(it.getProductOptionId()) + 1;
                best.put(it.getProductOptionId(), value);
                if (value.longValue() > count.get()){
                    bestOption.set(it.getProductOptionId());
                    count.set(value);
                }
            } else {
                best.put(it.getProductOptionId(), 1l);
                if (null == bestOption.get()) {
                    bestOption.set(it.getProductOptionId());
                    count.set(1l);
                }
            }
        });

        System.out.println(String.format("Best Product Option %s, %d", bestOption.get(), count.get()));
    }

    public static void thirthExercizeLargestOrder(Collection<Order> orders) {
        AtomicReference<String> bestOption = new AtomicReference<>();
        AtomicLong value = new AtomicLong(0);
        orders.forEach(it -> {
            long result = it.getItems().stream().mapToLong(item -> item.getQuantity() * item.getPriceCents()).sum();
            if(result > value.get()){
                bestOption.set(it.getId());
                value.set(result);
            }
        });

        System.out.println(String.format("Largest Order : %s, USS cents %d", bestOption.get(), value.get()));

    }

    public static void thirthExercizeStatesOfTheOrders(){
        Map<OrderState, Long> states = new HashMap<>();
        Stream.of(OrderState.values()).forEach(it-> states.put(it, 0l));

        for (int i = 0; ; i++) {
            Collection<Order> response = FaireRestClient.getInstance().getOrdersResource()
                    .getOrders(i + 1, 50, null, null, null);
            if (response.isEmpty()) {
                break;
            }
            response.forEach(it -> {
                states.put(it.getState(), states.get(it.getState()) + 1);
            });
        }

        System.out.println("Count by States of the orders");
        states.entrySet().stream().forEach(it->{
            System.out.println(String.format("State : %s, Count : %d", it.getKey(), it.getValue()));
        });
    }

    public static void thirthExercizeLargestProductionOptionQtySellgingInOneOrder(Collection<Order> orders){
        Collection<ItemOrder> itens = orders.stream().flatMap(it -> it.getItems().stream()).collect(Collectors.toList());
        AtomicLong max = new AtomicLong(0);
        AtomicReference<String> option = new AtomicReference<>();
        AtomicReference<String> order = new AtomicReference<>();
        itens.forEach(it->{
            if(it.getQuantity().longValue() > max.get()){
                max.set(it.getQuantity());
                option.set(it.getProductOptionId());
                order.set(it.getOrderId());
            }
        });

        System.out.println(String.format("Largest Product Option Selling by Qty in One Order. " +
                "Order : %s, Option : %s, Qty : %d", order.get(), option.get(), max.get()));
    }

    public static void thirthExercizeTheOrderWithMoreItens(Collection<Order> orders){
        AtomicInteger count = new AtomicInteger();
        AtomicReference<String> order = new AtomicReference<>();

        orders.forEach(it->{
            if(count.get() < it.getItems().size()){
                count.set(it.getItems().size());
                order.set(it.getId());
            }
        });

        System.out.println(String.format("The order with more item. Order : %s, Items : %d",
                order.get(), count.get()));
    }
}
