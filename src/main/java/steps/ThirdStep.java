package steps;

import entity.order.ItemOrder;
import entity.order.Order;
import entity.order.OrderState;
import rest.FaireRestClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static entity.order.OrderState.*;

public class ThirdStep {

    /**
     * After all orders have been read, the program should print the following metrics,
     * plus 2 of your choosing, and then exit - your creativity is welcome!
     * The best selling product option
     * The largest order by dollar amount
     * The state with the most orders
     */
    public static void runThirdStep() {
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
