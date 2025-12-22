package ru.yandex.practicum.shopping_store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.ProductState;
import ru.yandex.practicum.interaction_api.enums.QuantityState;
import ru.yandex.practicum.interaction_api.model.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {

    /**
     * Найти товар по ID
     */
    @Override
    Optional<ProductEntity> findById(String id);

    /**
     * Найти товары по названию (точное совпадение)
     */
    Optional<ProductEntity> findByName(String name);

    /**
     * Найти товары по названию (частичное совпадение)
     */
    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Найти товары по категории
     */
    List<ProductEntity> findByProductCategory(ProductCategory category);

    /**
     * Найти товары по цене меньше или равно указанной
     */
    List<ProductEntity> findByPriceLessThanEqual(Integer maxPrice);

    /**
     * Найти доступные товары (состояние AVAILABLE и достаточное количество)
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.productState = 'AVAILABLE' AND p.quantityState = 'IN_STOCK'")
    List<ProductEntity> findAvailableProducts();

    /**
     * Найти товары с сортировкой по цене (убывание)
     */
    @Query("SELECT p FROM ProductEntity p ORDER BY p.price DESC")
    List<ProductEntity> findAllOrderByPriceDesc();

    /**
     * Проверить существование товара по названию
     */
    boolean existsByName(String name);

    /**
     * Получить общее количество товаров
     */
    @Query("SELECT COUNT(p) FROM ProductEntity p")
    Long countAllProducts();

    /**
     * Найти товары, которые есть в указанных корзинах
     */
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
            "JOIN CartProductEntity cp ON cp.product.id = p.id " +
            "WHERE cp.shoppingCart.id IN :cartIds")
    List<ProductEntity> findProductsInCarts(@Param("cartIds") List<String> cartIds);

    /**
     * Обновить состояние товара
     */
    @Query("UPDATE ProductEntity p SET p.productState = :state WHERE p.id = :id")
    void updateProductState(@Param("id") String id,
                            @Param("state") ProductState state);

    /**
     * Обновить состояние количества товара
     */
    @Query("UPDATE ProductEntity p SET p.quantityState = :quantityState WHERE p.id = :id")
    void updateQuantityState(@Param("id") String id,
                             @Param("quantityState") QuantityState quantityState);
}