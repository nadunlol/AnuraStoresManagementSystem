package com.anurastores.test;

import com.anurastores.dao.SupplierDAO;
import com.anurastores.model.Supplier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SupplierDAOTest {

    private final SupplierDAO supplierDAO = new SupplierDAO();

    @Test
    void testGetAllSuppliers() {

        List<Supplier> suppliers =
                supplierDAO.getAllSuppliers();

        assertNotNull(suppliers);
        assertFalse(suppliers.isEmpty());
    }

    @Test
    void testSearchSuppliers() {

        List<Supplier> suppliers =
                supplierDAO.searchSuppliers("a");

        assertNotNull(suppliers);
    }
    
    @Test
    void testAddSupplier() {

        Supplier supplier = new Supplier();

        supplier.setSupplierName(
                "JUnit Test Supplier " + System.currentTimeMillis());

        supplier.setContactPerson("JUnit Contact");

        supplier.setPhone(
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7));

        supplier.setEmail(
                "junit" + System.currentTimeMillis()
                        + "@test.com");

        supplier.setAddress("JUnit Test Address");

        supplier.setStatus("ACTIVE");

        boolean result =
                supplierDAO.addSupplier(supplier);

        assertTrue(result);
    }
    
    @Test
    void testUpdateSupplier() {

        String supplierName =
                "JUnit Update Supplier " + System.currentTimeMillis();

        String phone =
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7);

        Supplier supplier = new Supplier();

        supplier.setSupplierName(supplierName);
        supplier.setContactPerson("Original Contact");
        supplier.setPhone(phone);
        supplier.setEmail("original@test.com");
        supplier.setAddress("Original Address");
        supplier.setStatus("ACTIVE");

        boolean added =
                supplierDAO.addSupplier(supplier);

        assertTrue(added);

        List<Supplier> suppliers =
                supplierDAO.searchSuppliers(supplierName);

        assertFalse(suppliers.isEmpty());

        Supplier addedSupplier = suppliers.get(0);

        addedSupplier.setSupplierName(
                "JUnit Updated Supplier");

        addedSupplier.setContactPerson(
                "Updated Contact");

        addedSupplier.setEmail(
                "updated@test.com");

        addedSupplier.setAddress(
                "Updated Address");

        boolean updated =
                supplierDAO.updateSupplier(addedSupplier);

        assertTrue(updated);

        List<Supplier> updatedSuppliers =
                supplierDAO.searchSuppliers(
                        "JUnit Updated Supplier");

        assertFalse(updatedSuppliers.isEmpty());

        Supplier result =
                updatedSuppliers.get(0);

        assertEquals(
                "JUnit Updated Supplier",
                result.getSupplierName()
        );

        assertEquals(
                "Updated Contact",
                result.getContactPerson()
        );

        assertEquals(
                "updated@test.com",
                result.getEmail()
        );

        assertEquals(
                "Updated Address",
                result.getAddress()
        );
    }
    
    @Test
    void testDeactivateSupplier() {

        String supplierName =
                "JUnit Deactivate Supplier "
                        + System.currentTimeMillis();

        String phone =
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7);

        Supplier supplier = new Supplier();

        supplier.setSupplierName(supplierName);
        supplier.setContactPerson("Deactivate Contact");
        supplier.setPhone(phone);
        supplier.setEmail("deactivate@test.com");
        supplier.setAddress("Deactivate Address");
        supplier.setStatus("ACTIVE");

        boolean added =
                supplierDAO.addSupplier(supplier);

        assertTrue(added);

        List<Supplier> suppliers =
                supplierDAO.searchSuppliers(supplierName);

        assertFalse(suppliers.isEmpty());

        Supplier addedSupplier =
                suppliers.get(0);

        int supplierId =
                addedSupplier.getSupplierId();

        boolean deactivated =
                supplierDAO.deactivateSupplier(supplierId);

        assertTrue(deactivated);

        List<Supplier> deactivatedSuppliers =
                supplierDAO.searchSuppliers(supplierName);

        assertFalse(deactivatedSuppliers.isEmpty());

        Supplier result =
                deactivatedSuppliers.get(0);

        assertEquals(
                "INACTIVE",
                result.getStatus()
        );
    }
    
    @Test
    void testSupplierExists() {

        String supplierName =
                "JUnit Exists Supplier "
                        + System.currentTimeMillis();

        String phone =
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7);

        Supplier supplier = new Supplier();

        supplier.setSupplierName(supplierName);
        supplier.setContactPerson("Exists Contact");
        supplier.setPhone(phone);
        supplier.setEmail("exists@test.com");
        supplier.setAddress("Exists Address");
        supplier.setStatus("ACTIVE");

        boolean added =
                supplierDAO.addSupplier(supplier);

        assertTrue(added);

        List<Supplier> suppliers =
                supplierDAO.searchSuppliers(supplierName);

        assertFalse(suppliers.isEmpty());

        Supplier addedSupplier =
                suppliers.get(0);

        int supplierId =
                addedSupplier.getSupplierId();

        // Check if another supplier has the same name and phone
        boolean exists =
                supplierDAO.supplierExists(
                        supplierName,
                        phone,
                        0
                );

        assertTrue(exists);

        // Exclude the current supplier
        boolean existsWhenExcluded =
                supplierDAO.supplierExists(
                        supplierName,
                        phone,
                        supplierId
                );

        assertFalse(existsWhenExcluded);
    }
    
    @Test
    void testSupplierDoesNotExist() {

        String supplierName =
                "JUnit Nonexistent Supplier "
                        + System.currentTimeMillis();

        String phone =
                "077" + String.valueOf(System.currentTimeMillis())
                        .substring(7);

        boolean exists =
                supplierDAO.supplierExists(
                        supplierName,
                        phone,
                        0
                );

        assertFalse(exists);
    }
    
}