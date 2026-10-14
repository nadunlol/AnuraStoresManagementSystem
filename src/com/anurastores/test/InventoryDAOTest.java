package com.anurastores.test;

import com.anurastores.dao.InventoryDAO;
import com.anurastores.model.Inventory;
import com.anurastores.model.StockMovement;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryDAOTest {

    private final InventoryDAO inventoryDAO =
            new InventoryDAO();

    @Test
    void testGetAllInventory() {

        List<Inventory> inventory =
                inventoryDAO.getAllInventory();

        assertNotNull(inventory);
    }
    
    @Test
    void testSearchInventory() {

        List<Inventory> inventory =
                inventoryDAO.searchInventory("1");

        assertNotNull(inventory);

        boolean foundProduct = false;

        for (Inventory item : inventory) {

            if (item.getProductId() == 1) {
                foundProduct = true;
                break;
            }
        }

        assertTrue(foundProduct);
    }
    
    @Test
    void testAddStockInvalidQuantity() {

        boolean result =
                inventoryDAO.addStock(
                        1,
                        0,
                        "JUnit invalid quantity test");

        assertFalse(result);
    }
    
    @Test
    void testAddStock() {

        boolean result =
                inventoryDAO.addStock(
                        1,
                        10,
                        "JUnit valid stock test");

        assertTrue(result);
    }
    
    @Test
    void testAdjustStockInvalidQuantity() {

        boolean result =
                inventoryDAO.adjustStock(
                        1,
                        0,
                        "JUnit invalid adjustment test");

        assertFalse(result);
    }
    
    @Test
    void testAdjustStock() {

        boolean result =
                inventoryDAO.adjustStock(
                        1,
                        5,
                        "JUnit stock adjustment test");

        assertTrue(result);
    }
    
    @Test
    void testGetAllStockMovements() {

        List<StockMovement> movements =
                inventoryDAO.getAllStockMovements();

        assertNotNull(movements);
        assertFalse(movements.isEmpty());
    }
    
    @Test
    void testDeleteStockMovement() {

        List<StockMovement> movements =
                inventoryDAO.getAllStockMovements();

        assertNotNull(movements);

        StockMovement targetMovement = null;

        for (StockMovement movement : movements) {

            if (movement.getProductId() == 1
                    && "JUnit valid stock test".equals(
                            movement.getRemarks())) {

                targetMovement = movement;
                break;
            }
        }

        assertNotNull(targetMovement);

        int movementId =
                targetMovement.getMovementId();

        boolean result =
                inventoryDAO.deleteStockMovement(movementId);

        assertTrue(result);

        List<StockMovement> updatedMovements =
                inventoryDAO.getAllStockMovements();

        boolean stillExists = false;

        for (StockMovement movement : updatedMovements) {

            if (movement.getMovementId() == movementId) {
                stillExists = true;
                break;
            }
        }

        assertFalse(stillExists);
    }
    
    @Test
    void testAdjustStockCannotMakeNegativeStock() {

        boolean result =
                inventoryDAO.adjustStock(
                        1,
                        -100000,
                        "JUnit negative stock test");

        assertFalse(result);
    }
    
    @Test
    void testAddStockNegativeQuantity() {

        boolean result =
                inventoryDAO.addStock(
                        1,
                        -5,
                        "JUnit negative quantity test");

        assertFalse(result);
    }
    
    @Test
    void testDeleteStockMovementNotFound() {

        boolean result =
                inventoryDAO.deleteStockMovement(
                        999999);

        assertFalse(result);
    }
}