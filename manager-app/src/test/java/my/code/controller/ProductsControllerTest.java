package my.code.controller;

import my.code.client.BadRequestException;
import my.code.client.ProductsRestClient;
import my.code.controller.payload.NewProductPayload;
import my.code.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests ProductsController")
class ProductsControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController controller;

    @Test
    void getProductsList_ReturnsProductsListPage() {
        // given
        var model = new ConcurrentModel();
        var filter = "товар";

        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i),
                        "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(products).when(this.productsRestClient).findAllProducts(filter);

        // when
        var result = this.controller.getProductsList(model, filter);

        // then
        assertEquals("catalogue/products/list", result);
        assertEquals(filter, model.getAttribute("filter"));
        assertEquals(products, model.getAttribute("products"));
    }

    @Test
    void getNewProductPage_ReturnsNewProductPage () {
        // given

        // when
        var result = this.controller.getNewProductPage();

        // then
        assertEquals("catalogue/products/new_product", result);
    }

    @Test
    @DisplayName("creatProduct create a new product and redirect to the product page.")
    void creatProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        String title = "New Product";
        String details = "Description new product";
        //given
        var payload = new NewProductPayload(title, details);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(new Product(1, title, details))
                .when(this.productsRestClient)
                .createProduct(title, details);

        //when
        var result = this.controller.createProduct(payload, model, response);

        //then
        assertEquals("redirect:/catalogue/products/1", result);
        verify(this.productsRestClient).createProduct(title, details);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    @DisplayName("creatProduct return a page with errors if the request is not valid.")
    void creatProduct_RequestIsValid_ReturnsProductFromWithErrors() {
        var payload = new NewProductPayload("   ", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Mistake 1", "Error 2")))
                .when(this.productsRestClient)
                .createProduct("   ", null);

        var result = this.controller.createProduct(payload, model, response);

        assertEquals("catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Mistake 1", "Error 2"), model.getAttribute("errors"));
        verify(this.productsRestClient).createProduct("   ", null);
        verifyNoMoreInteractions(this.productsRestClient);
    }

}