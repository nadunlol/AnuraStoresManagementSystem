package com.anurastores.test;

import com.anurastores.dao.SalesDAO;
import com.anurastores.dao.ProductDAO;
import com.anurastores.dao.InventoryDAO;
import com.anurastores.model.Sale;
import com.anurastores.model.SaleItem;
import com.anurastores.model.Product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class SalesDAOTest {

    private final SalesDAO salesDAO =
            new SalesDAO();

    @Test
    void testCreateSaleWithEmptyItems() {

        int result =
                salesDAO.createSale(
                        1,
                        new ArrayList<>(),
                        "CASH");

        assertEquals(-1, result);
    }

    @Test
    void testCreateSaleWithInvalidPaymentMethod() {

        ArrayList<SaleItem> items =
                new ArrayList<SaleItem>();

        int result =
                salesDAO.createSale(
                        1,
                        items,
                        "INVALID");

        assertEquals(-1, result);
    }

    @Test
    void testGetAllSales() {

        List<Sale> sales =
                salesDAO.getAllSales();

        assertNotNull(sales);
    }

    @Test
    void testSearchSales() {

        List<Sale> sales =
                salesDAO.searchSales("1");

        assertNotNull(sales);
    }

    @Test
    void testSearchSalesByDateRange() {

        Date fromDate =
                Date.valueOf("2000-01-01");

        Date toDate =
                Date.valueOf("2100-12-31");

        List<Sale> sales =
                salesDAO.searchSalesByDateRange(
                        fromDate,
                        toDate);

        assertNotNull(sales);
    }

    @Test
    void testGetSaleItems() {

        List<Sale> sales =
                salesDAO.getAllSales();

        assertNotNull(sales);
        assertFalse(sales.isEmpty());

        int saleId =
                sales.get(0).getSaleId();

        List<SaleItem> items =
                salesDAO.getSaleItems(saleId);

        assertNotNull(items);
    }

    @Test
    void testSavePendingSaleWithEmptyItems() {

        int result =
                salesDAO.savePendingSale(
                        1,
                        new ArrayList<>());

        assertEquals(-1, result);
    }

    @Test
    void testUpdatePendingSaleWithEmptyItems() {

        boolean result =
                salesDAO.updatePendingSale(
                        999999,
                        new ArrayList<>());

        assertFalse(result);
    }

    @Test
    void testCompletePendingSaleWithInvalidPaymentMethod() {

        boolean result =
                salesDAO.completePendingSale(
                        999999,
                        "INVALID");

        assertFalse(result);
    }

    @Test
    void testCancelSaleNotFound() {

        boolean result =
                salesDAO.cancelSale(999999);

        assertFalse(result);
    }

    @Test
    void testSavePendingSale() {

        ProductDAO productDAO =
                new ProductDAO();

        InventoryDAO inventoryDAO =
                new InventoryDAO();

        Product product =
                new Product();

        String barcode =
                "JUNIT-PENDING-" +
                System.currentTimeMillis();

        product.setProductName(
                "JUnit Pending Product");

        product.setCategoryId(1);

        product.setBarcode(barcode);

        product.setSellingPrice(
                new BigDecimal("150.00"));

        product.setReorderLevel(5);

        product.setExpiryDate(null);

        product.setStatus("ACTIVE");

        boolean productAdded =
                productDAO.addProduct(product);

        assertTrue(productAdded);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        boolean stockAdded =
                inventoryDAO.addStock(
                        productId,
                        20,
                        "JUnit pending sale setup");

        assertTrue(stockAdded);

        SaleItem item =
                new SaleItem();

        item.setProductId(productId);
        item.setQuantity(2);

        ArrayList<SaleItem> items =
                new ArrayList<>();

        items.add(item);

        int saleId =
                salesDAO.savePendingSale(
                        1,
                        items);

        assertTrue(saleId > 0);

        List<SaleItem> savedItems =
                salesDAO.getSaleItems(saleId);

        assertNotNull(savedItems);
        assertFalse(savedItems.isEmpty());

        assertEquals(
                productId,
                savedItems.get(0).getProductId());

        assertEquals(
                2,
                savedItems.get(0).getQuantity());
    }
    
    @Test
    void testUpdatePendingSale() {

        ProductDAO productDAO =
                new ProductDAO();

        Product product =
                new Product();

        String barcode =
                "JUNIT-UPDATE-PENDING-" +
                System.currentTimeMillis();

        product.setProductName(
                "JUnit Update Pending Product");

        product.setCategoryId(1);

        product.setBarcode(barcode);

        product.setSellingPrice(
                new BigDecimal("200.00"));

        product.setReorderLevel(5);

        product.setExpiryDate(null);

        product.setStatus("ACTIVE");

        boolean productAdded =
                productDAO.addProduct(product);

        assertTrue(productAdded);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        SaleItem originalItem =
                new SaleItem();

        originalItem.setProductId(productId);
        originalItem.setQuantity(2);

        ArrayList<SaleItem> originalItems =
                new ArrayList<>();

        originalItems.add(originalItem);

        int saleId =
                salesDAO.savePendingSale(
                        1,
                        originalItems);

        assertTrue(saleId > 0);

        SaleItem updatedItem =
                new SaleItem();

        updatedItem.setProductId(productId);
        updatedItem.setQuantity(5);

        ArrayList<SaleItem> updatedItems =
                new ArrayList<>();

        updatedItems.add(updatedItem);

        boolean updated =
                salesDAO.updatePendingSale(
                        saleId,
                        updatedItems);

        assertTrue(updated);

        List<SaleItem> savedItems =
                salesDAO.getSaleItems(saleId);

        assertNotNull(savedItems);
        assertFalse(savedItems.isEmpty());

        assertEquals(
                productId,
                savedItems.get(0).getProductId());

        assertEquals(
                5,
                savedItems.get(0).getQuantity());
    }
    
    @Test
    void testCreateSale() {

        ProductDAO productDAO =
                new ProductDAO();

        InventoryDAO inventoryDAO =
                new InventoryDAO();

        Product product =
                new Product();

        String barcode =
                "JUNIT-CREATE-SALE-" +
                System.currentTimeMillis();

        product.setProductName(
                "JUnit Create Sale Product");

        product.setCategoryId(1);

        product.setBarcode(barcode);

        product.setSellingPrice(
                new BigDecimal("250.00"));

        product.setReorderLevel(5);

        product.setExpiryDate(null);

        product.setStatus("ACTIVE");

        boolean productAdded =
                productDAO.addProduct(product);

        assertTrue(productAdded);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        boolean stockAdded =
                inventoryDAO.addStock(
                        productId,
                        20,
                        "JUnit create sale setup");

        assertTrue(stockAdded);

        SaleItem item =
                new SaleItem();

        item.setProductId(productId);
        item.setQuantity(2);

        ArrayList<SaleItem> items =
                new ArrayList<>();

        items.add(item);

        int saleId =
                salesDAO.createSale(
                        1,
                        items,
                        "CASH");

        assertTrue(saleId > 0);

        List<SaleItem> saleItems =
                salesDAO.getSaleItems(saleId);

        assertNotNull(saleItems);
        assertFalse(saleItems.isEmpty());

        assertEquals(
                productId,
                saleItems.get(0).getProductId());

        assertEquals(
                2,
                saleItems.get(0).getQuantity());
    }
    
    @Test
    void testCancelSale() {

        ProductDAO productDAO =
                new ProductDAO();

        InventoryDAO inventoryDAO =
                new InventoryDAO();

        Product product =
                new Product();

        String barcode =
                "JUNIT-CANCEL-SALE-" +
                System.currentTimeMillis();

        product.setProductName(
                "JUnit Cancel Sale Product");

        product.setCategoryId(1);

        product.setBarcode(barcode);

        product.setSellingPrice(
                new BigDecimal("300.00"));

        product.setReorderLevel(5);

        product.setExpiryDate(null);

        product.setStatus("ACTIVE");

        boolean productAdded =
                productDAO.addProduct(product);

        assertTrue(productAdded);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        boolean stockAdded =
                inventoryDAO.addStock(
                        productId,
                        20,
                        "JUnit cancel sale setup");

        assertTrue(stockAdded);

        SaleItem item =
                new SaleItem();

        item.setProductId(productId);
        item.setQuantity(2);

        ArrayList<SaleItem> items =
                new ArrayList<>();

        items.add(item);

        int saleId =
                salesDAO.createSale(
                        1,
                        items,
                        "CASH");

        assertTrue(saleId > 0);

        boolean cancelled =
                salesDAO.cancelSale(saleId);

        assertTrue(cancelled);

        List<Sale> sales =
                salesDAO.searchSales(
                        String.valueOf(saleId));

        assertFalse(sales.isEmpty());

        assertEquals(
                "CANCELLED",
                sales.get(0).getSaleStatus());
    }
    
    @Test
    void testCompletePendingSale() {

        ProductDAO productDAO =
                new ProductDAO();

        InventoryDAO inventoryDAO =
                new InventoryDAO();

        Product product =
                new Product();

        String barcode =
                "JUNIT-COMPLETE-PENDING-" +
                System.currentTimeMillis();

        product.setProductName(
                "JUnit Complete Pending Product");

        product.setCategoryId(1);

        product.setBarcode(barcode);

        product.setSellingPrice(
                new BigDecimal("350.00"));

        product.setReorderLevel(5);

        product.setExpiryDate(null);

        product.setStatus("ACTIVE");

        boolean productAdded =
                productDAO.addProduct(product);

        assertTrue(productAdded);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        boolean stockAdded =
                inventoryDAO.addStock(
                        productId,
                        20,
                        "JUnit complete pending setup");

        assertTrue(stockAdded);

        SaleItem item =
                new SaleItem();

        item.setProductId(productId);
        item.setQuantity(2);

        ArrayList<SaleItem> items =
                new ArrayList<>();

        items.add(item);

        int saleId =
                salesDAO.savePendingSale(
                        1,
                        items);

        assertTrue(saleId > 0);

        boolean completed =
                salesDAO.completePendingSale(
                        saleId,
                        "CARD");

        assertTrue(completed);

        List<Sale> sales =
                salesDAO.searchSales(
                        String.valueOf(saleId));

        assertFalse(sales.isEmpty());

        assertEquals(
                "COMPLETED",
                sales.get(0).getSaleStatus());

        assertEquals(
                "CARD",
                sales.get(0).getPaymentMethod());
    }
    
    @Test
    void testCreateSaleWithInvalidQuantity() {

        SaleItem item =
                new SaleItem();

        item.setProductId(1);
        item.setQuantity(0);

        ArrayList<SaleItem> items =
                new ArrayList<>();

        items.add(item);

        int result =
                salesDAO.createSale(
                        1,
                        items,
                        "CASH");

        assertEquals(-1, result);
    }
    
    @Test
    void testCompletePendingSaleWithInsufficientStock() {

        ProductDAO productDAO =
                new ProductDAO();

        InventoryDAO inventoryDAO =
                new InventoryDAO();

        Product product =
                new Product();

        String barcode =
                "JUNIT-INSUFFICIENT-STOCK-" +
                System.currentTimeMillis();

        product.setProductName(
                "JUnit Insufficient Stock Product");

        product.setCategoryId(1);

        product.setBarcode(barcode);

        product.setSellingPrice(
                new BigDecimal("400.00"));

        product.setReorderLevel(5);

        product.setExpiryDate(null);

        product.setStatus("ACTIVE");

        boolean productAdded =
                productDAO.addProduct(product);

        assertTrue(productAdded);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        boolean stockAdded =
                inventoryDAO.addStock(
                        productId,
                        2,
                        "JUnit insufficient stock setup");

        assertTrue(stockAdded);

        SaleItem item =
                new SaleItem();

        item.setProductId(productId);
        item.setQuantity(5);

        ArrayList<SaleItem> items =
                new ArrayList<>();

        items.add(item);

        int saleId =
                salesDAO.savePendingSale(
                        1,
                        items);

        assertTrue(saleId > 0);

        boolean completed =
                salesDAO.completePendingSale(
                        saleId,
                        "CASH");

        assertFalse(completed);
    }
    
    @Test
    void testCancelAlreadyCancelledSale() {

        ProductDAO productDAO =
                new ProductDAO();

        InventoryDAO inventoryDAO =
                new InventoryDAO();

        Product product =
                new Product();

        String barcode =
                "JUNIT-DOUBLE-CANCEL-" +
                System.currentTimeMillis();

        product.setProductName(
                "JUnit Double Cancel Product");

        product.setCategoryId(1);

        product.setBarcode(barcode);

        product.setSellingPrice(
                new BigDecimal("500.00"));

        product.setReorderLevel(5);

        product.setExpiryDate(null);

        product.setStatus("ACTIVE");

        boolean productAdded =
                productDAO.addProduct(product);

        assertTrue(productAdded);

        List<Product> products =
                productDAO.searchProducts(barcode);

        assertFalse(products.isEmpty());

        int productId =
                products.get(0).getProductId();

        boolean stockAdded =
                inventoryDAO.addStock(
                        productId,
                        20,
                        "JUnit double cancel setup");

        assertTrue(stockAdded);

        SaleItem item =
                new SaleItem();

        item.setProductId(productId);
        item.setQuantity(2);

        ArrayList<SaleItem> items =
                new ArrayList<>();

        items.add(item);

        int saleId =
                salesDAO.createSale(
                        1,
                        items,
                        "CASH");

        assertTrue(saleId > 0);

        boolean firstCancel =
                salesDAO.cancelSale(saleId);

        assertTrue(firstCancel);

        boolean secondCancel =
                salesDAO.cancelSale(saleId);

        assertFalse(secondCancel);
    }
    
    
}