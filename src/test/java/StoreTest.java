import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class StoreTest extends BaseTest {
    RequestSpecification requestSpec;
    ResponseSpecification responseOrderDto;

    @BeforeEach
    public void setUp() {
        requestSpec = RestAssured.given();
        ResponseSpecBuilder specBuilder = new ResponseSpecBuilder()
                .expectStatusCode(200);
        responseOrderDto = specBuilder.build();
    }

    @Test
    public void shouldAddOrder() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        JSONObject order = new JSONObject()
                .put("id", 3L)
                .put("idOrder", 1L)
                .put("quantity", 2)
                .put("shipDate", now)
                .put("status", "placed")
                .put("complete", true);

        OrderDTO orderRes = requestSpec
                .body(order.toString())
                .contentType("application/json")
                .post("/store/order")
                .as(new TypeRef<>() {
                });

        assertThat(orderRes.getId()).isNotNull();

    }
    @Test
    public void shouldNotCreateOrder() {
        RequestSpecification request = RestAssured.given();

        Response response = request.post("/store/order");
        response.then()
                .statusCode(415)
                .assertThat()
                .body(
                        "code", equalTo(415),
                        "type", equalTo("unknown"),
                        "message", containsString("A message body reader for Java " +
                                "class io.swagger.sample.model.Order")
                );
    }
    @Test
    public void shouldGetOrderById() {
        requestSpec
                .get("/store/order/1")
                .then()
                .spec(responseOrderDto)
                .body("status", equalTo("placed"));
    }

    @Test
    public void shouldDeleteOrder() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        JSONObject Order = new JSONObject()
                .put("id", 4L)
                .put("idOrder", 4L)
                .put("quantity", 34)
                .put("shipDate", now)
                .put("status", "approved")
                .put("complete", false);

        OrderDTO createdOrder = generateOrder(Order);

        requestSpec
                .delete("/store/order/{id}", createdOrder.getId())
                .then()
                .statusCode(200);
    }

    private OrderDTO generateOrder(Object o) {
        return requestSpec
                .body(o.toString())
                .contentType("application/json")
                .post("/store/order")
                .as(new TypeRef<>() {
                });
    }

    @Test
    public void shouldGetInventoryOrder() {
        RequestSpecification request = RestAssured.given();

        Response response = request.get("/store/inventory");
        response.then()
                .statusCode(200);
    }

}
