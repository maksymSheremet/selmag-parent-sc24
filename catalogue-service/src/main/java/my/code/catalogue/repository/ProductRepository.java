package my.code.catalogue.repository;

import my.code.catalogue.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends CrudRepository<Product, Integer> {

//    @Query(value = "select p from Product p where p.title ilike :filter")
//    @Query(value = "select * from catalogue.t_product where c_title ilike :filter", nativeQuery = true)

    @Query(name = "Product.findAllByTitleLikeIgnoringCase", nativeQuery = true)
    Iterable<Product> findAllByTitleLikeIgnoreCase(@Param("filter") String filter);

}
