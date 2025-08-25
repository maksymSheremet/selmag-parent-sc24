package my.code.catalogue.repository;

import my.code.catalogue.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Sql("/sql/products.sql")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIT {

    @Autowired
    ProductRepository repository;

    @Test
    void findAllByTitleLikeIgnoreCase_ReturnFilterProductList() {
        //given
        var filter = "%chocolate%";

        //when
        var products = this.repository.findAllByTitleLikeIgnoreCase(filter);

        //then
        var productList = StreamSupport.stream(products.spliterator(), false)
                .toList();
        assertEquals(1, productList.size());

        Product actualProduct = productList.getFirst();
        assertNotNull(actualProduct);
        assertEquals(2, actualProduct.getId());
        assertEquals("Chocolate", actualProduct.getTitle());
        assertEquals("Very tasty chocolate", actualProduct.getDetails());
    }
}