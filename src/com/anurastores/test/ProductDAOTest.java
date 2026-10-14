package com.anurastores.test;

import com.anurastores.dao.ProductDAO;
import com.anurastores.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOTest {

    private final ProductDAO productDAO = new ProductDAO();

    @Test
    void testGetAllProducts() {

        List<Product> products = productDAO.getAllProducts();

        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Test
    void testSearchProducts() {

        List<Product> products = productDAO.searchProducts("a");

        assertNotNull(products);
    }

    @Test
    void testAddProduct() {

        Product product = new Product();

        product.setProductName("JUnit Test Product");
        product.setCategoryId(1);
        product.setBarcode("JUNIT-" + System.currentTimeMillis());
        product.setSellingPrice(new BigDecimal("100.00"));
        product.setReorderLevel(10);
        product.setExpiryDate(null);
        product.setStatus("ACTIVE");

        boolean result = productDAO.addProduct(product);

        assertTrue(result);
    }

    @Test
    void testUpdateProduct() {

        String barcode = "JUNIT-UPDATE-" + System.currentTimeMillis();

        Product product = new Product();

        product.setProductName("JUnit Update Product");
        product.setCategoryId(1);
        product.setBarcode(barcode);
        product.setSellingPrice(new BigDecimal("200.00"));
        product.setReorderLevel(10);
        product.setExpiryDate(null);
        product.setStatus("ACTIVE");

        boolean added = productDAO.addProduct(product);

        assertTrue(added);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        Product addedProduct = products.get(0);

        addedProduct.setProductName("JUnit Updated Product");
        addedProduct.setSellingPrice(new BigDecimal("250.00"));

        boolean updated =
                productDAO.updateProduct(addedProduct);

        assertTrue(updated);

        List<Product> updatedProducts =
                productDAO.searchProducts(barcode);

        assertFalse(updatedProducts.isEmpty());

        Product result = updatedProducts.get(0);

        assertEquals(
                "JUnit Updated Product",
                result.getProductName()
        );

        assertEquals(
                new BigDecimal("250.00"),
                result.getSellingPrice()
        );
    }
    
    @Test
    void testDeactivateProduct() {

        String barcode = "JUNIT-DEACTIVATE-" + System.currentTimeMillis();

        Product product = new Product();

        product.setProductName("JUnit Deactivate Product");
        product.setCategoryId(1);
        product.setBarcode(barcode);
        product.setSellingPrice(new BigDecimal("300.00"));
        product.setReorderLevel(5);
        product.setExpiryDate(null);
        product.setStatus("ACTIVE");

        boolean added = productDAO.addProduct(product);

        assertTrue(added);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        Product addedProduct = products.get(0);

        int productId = addedProduct.getProductId();

        boolean deactivated =
                productDAO.deactivateProduct(productId);

        assertTrue(deactivated);

        List<Product> deactivatedProducts =
                productDAO.searchProducts(barcode);

        assertFalse(deactivatedProducts.isEmpty());

        Product result = deactivatedProducts.get(0);

        assertEquals(
                "INACTIVE",
                result.getStatus()
        );
    }
    
    @Test
    void testBarcodeExists() {

        String barcode = "JUNIT-BARCODE-" + System.currentTimeMillis();

        Product product = new Product();

        product.setProductName("JUnit Barcode Product");
        product.setCategoryId(1);
        product.setBarcode(barcode);
        product.setSellingPrice(new BigDecimal("400.00"));
        product.setReorderLevel(5);
        product.setExpiryDate(null);
        product.setStatus("ACTIVE");

        boolean added = productDAO.addProduct(product);

        assertTrue(added);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        Product addedProduct = products.get(0);

        int productId = addedProduct.getProductId();

        boolean exists =
                productDAO.barcodeExists(barcode, 0);

        assertTrue(exists);

        boolean existsWhenExcluded =
                productDAO.barcodeExists(barcode, productId);

        assertFalse(existsWhenExcluded);
    }
    
    @Test
    void testBarcodeDoesNotExist() {

        String barcode =
                "JUNIT-NONEXISTENT-" + System.currentTimeMillis();

        boolean exists =
                productDAO.barcodeExists(barcode, 0);

        assertFalse(exists);
    }
    
}