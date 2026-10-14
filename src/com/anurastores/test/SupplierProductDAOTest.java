package com.anurastores.test;

import com.anurastores.dao.ProductDAO;
import com.anurastores.dao.SupplierDAO;
import com.anurastores.dao.SupplierProductDAO;
import com.anurastores.model.Product;
import com.anurastores.model.Supplier;
import com.anurastores.model.SupplierProduct;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SupplierProductDAOTest {

    private final SupplierProductDAO supplierProductDAO =
            new SupplierProductDAO();

    @Test
    void testGetAllSupplierProducts() {

        List<SupplierProduct> supplierProducts =
                supplierProductDAO.getAllSupplierProducts();

        assertNotNull(supplierProducts);
    }

    @Test
    void testSearchSupplierProducts() {

        List<SupplierProduct> supplierProducts =
                supplierProductDAO.searchSupplierProducts("a");

        assertNotNull(supplierProducts);
    }

    @Test
    void testAssignProductToSupplierInvalidIds() {

        boolean result =
                supplierProductDAO.assignProductToSupplier(0, 0);

        assertFalse(result);
    }
    
    @Test
    void testAssignProductToSupplier() {

        SupplierDAO supplierDAO = new SupplierDAO();
        ProductDAO productDAO = new ProductDAO();

        // Create a new supplier
        Supplier supplier = new Supplier();

        supplier.setSupplierName(
                "JUnit Assignment Supplier "
                        + System.currentTimeMillis());

        supplier.setContactPerson("JUnit Contact");

        supplier.setPhone(
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7));

        supplier.setEmail(
                "assignment"
                        + System.currentTimeMillis()
                        + "@test.com");

        supplier.setAddress("JUnit Assignment Address");
        supplier.setStatus("ACTIVE");

        boolean supplierAdded =
                supplierDAO.addSupplier(supplier);

        assertTrue(supplierAdded);

        // Find the newly created supplier
        List<Supplier> suppliers =
                supplierDAO.searchSuppliers(
                        supplier.getSupplierName());

        assertFalse(suppliers.isEmpty());

        int supplierId =
                suppliers.get(0).getSupplierId();

        // Create a new product
        Product product = new Product();

        product.setProductName(
                "JUnit Assignment Product "
                        + System.currentTimeMillis());

        product.setCategoryId(1);

        product.setBarcode(
                "JUNIT-ASSIGN-"
                        + System.currentTimeMillis());

        product.setSellingPrice(
                new BigDecimal("500.00"));

        product.setReorderLevel(10);
        product.setExpiryDate(null);
        product.setStatus("ACTIVE");

        boolean productAdded =
                productDAO.addProduct(product);

        assertTrue(productAdded);

        // Find the newly created product
        List<Product> products =
                productDAO.searchProducts(
                        product.getBarcode());

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        // Assign the product to the supplier
        boolean assigned =
                supplierProductDAO.assignProductToSupplier(
                        supplierId,
                        productId);

        assertTrue(assigned);

        // Confirm that the association now exists
        assertTrue(
                supplierProductDAO.associationExists(
                        supplierId,
                        productId));
    }
    
    @Test
    void testGetProductsBySupplier() {

        SupplierDAO supplierDAO = new SupplierDAO();
        ProductDAO productDAO = new ProductDAO();

        // Create a new supplier
        Supplier supplier = new Supplier();

        supplier.setSupplierName(
                "JUnit Get Products Supplier "
                        + System.currentTimeMillis());

        supplier.setContactPerson("JUnit Contact");

        supplier.setPhone(
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7));

        supplier.setEmail(
                "getproducts"
                        + System.currentTimeMillis()
                        + "@test.com");

        supplier.setAddress("JUnit Test Address");
        supplier.setStatus("ACTIVE");

        assertTrue(supplierDAO.addSupplier(supplier));

        // Find supplier ID
        List<Supplier> suppliers =
                supplierDAO.searchSuppliers(
                        supplier.getSupplierName());

        assertFalse(suppliers.isEmpty());

        int supplierId =
                suppliers.get(0).getSupplierId();

        // Create a new product
        Product product = new Product();

        product.setProductName(
                "JUnit Get Products Product "
                        + System.currentTimeMillis());

        product.setCategoryId(1);

        product.setBarcode(
                "JUNIT-GETPRODUCTS-"
                        + System.currentTimeMillis());

        product.setSellingPrice(
                new BigDecimal("600.00"));

        product.setReorderLevel(10);
        product.setExpiryDate(null);
        product.setStatus("ACTIVE");

        assertTrue(productDAO.addProduct(product));

        // Find product ID
        List<Product> products =
                productDAO.searchProducts(
                        product.getBarcode());

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        // Assign product to supplier
        assertTrue(
                supplierProductDAO.assignProductToSupplier(
                        supplierId,
                        productId));

        // Get products belonging to this supplier
        List<SupplierProduct> supplierProducts =
                supplierProductDAO.getProductsBySupplier(
                        supplierId);

        assertNotNull(supplierProducts);
        assertFalse(supplierProducts.isEmpty());

        // Verify our product is in the result
        boolean found = false;

        for (SupplierProduct sp : supplierProducts) {

            if (sp.getProductId() == productId) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }
    
    @Test
    void testRemoveSupplierProduct() {

        SupplierDAO supplierDAO = new SupplierDAO();
        ProductDAO productDAO = new ProductDAO();

        // Create a new supplier
        Supplier supplier = new Supplier();

        supplier.setSupplierName(
                "JUnit Remove Supplier "
                        + System.currentTimeMillis());

        supplier.setContactPerson("JUnit Contact");

        supplier.setPhone(
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7));

        supplier.setEmail(
                "remove"
                        + System.currentTimeMillis()
                        + "@test.com");

        supplier.setAddress("JUnit Test Address");
        supplier.setStatus("ACTIVE");

        assertTrue(supplierDAO.addSupplier(supplier));

        // Find supplier ID
        List<Supplier> suppliers =
                supplierDAO.searchSuppliers(
                        supplier.getSupplierName());

        assertFalse(suppliers.isEmpty());

        int supplierId =
                suppliers.get(0).getSupplierId();

        // Create a new product
        Product product = new Product();

        product.setProductName(
                "JUnit Remove Product "
                        + System.currentTimeMillis());

        product.setCategoryId(1);

        product.setBarcode(
                "JUNIT-REMOVE-"
                        + System.currentTimeMillis());

        product.setSellingPrice(
                new BigDecimal("700.00"));

        product.setReorderLevel(10);
        product.setExpiryDate(null);
        product.setStatus("ACTIVE");

        assertTrue(productDAO.addProduct(product));

        // Find product ID
        List<Product> products =
                productDAO.searchProducts(
                        product.getBarcode());

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        // Assign product to supplier
        assertTrue(
                supplierProductDAO.assignProductToSupplier(
                        supplierId,
                        productId));

        // Confirm association exists
        assertTrue(
                supplierProductDAO.associationExists(
                        supplierId,
                        productId));

        // Remove association
        boolean removed =
                supplierProductDAO.removeSupplierProduct(
                        supplierId,
                        productId);

        assertTrue(removed);

        // Confirm association no longer exists
        assertFalse(
                supplierProductDAO.associationExists(
                        supplierId,
                        productId));
    }
    
    @Test
    void testAssociationExists() {

        SupplierDAO supplierDAO = new SupplierDAO();
        ProductDAO productDAO = new ProductDAO();

        // Create a new supplier
        Supplier supplier = new Supplier();

        supplier.setSupplierName(
                "JUnit Association Supplier "
                        + System.currentTimeMillis());

        supplier.setContactPerson("JUnit Contact");

        supplier.setPhone(
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7));

        supplier.setEmail(
                "association"
                        + System.currentTimeMillis()
                        + "@test.com");

        supplier.setAddress("JUnit Test Address");
        supplier.setStatus("ACTIVE");

        assertTrue(supplierDAO.addSupplier(supplier));

        // Find supplier ID
        List<Supplier> suppliers =
                supplierDAO.searchSuppliers(
                        supplier.getSupplierName());

        assertFalse(suppliers.isEmpty());

        int supplierId =
                suppliers.get(0).getSupplierId();

        // Create a new product
        Product product = new Product();

        product.setProductName(
                "JUnit Association Product "
                        + System.currentTimeMillis());

        product.setCategoryId(1);

        product.setBarcode(
                "JUNIT-ASSOCIATION-"
                        + System.currentTimeMillis());

        product.setSellingPrice(
                new BigDecimal("800.00"));

        product.setReorderLevel(10);
        product.setExpiryDate(null);
        product.setStatus("ACTIVE");

        assertTrue(productDAO.addProduct(product));

        // Find product ID
        List<Product> products =
                productDAO.searchProducts(
                        product.getBarcode());

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        // Before assignment, association should not exist
        assertFalse(
                supplierProductDAO.associationExists(
                        supplierId,
                        productId));

        // Assign product to supplier
        assertTrue(
                supplierProductDAO.assignProductToSupplier(
                        supplierId,
                        productId));

        // After assignment, association should exist
        assertTrue(
                supplierProductDAO.associationExists(
                        supplierId,
                        productId));
    }
}