package com.anurastores.test;

import com.anurastores.dao.CategoryDAO;
import com.anurastores.model.Category;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDAOTest {

    private final CategoryDAO categoryDAO =
            new CategoryDAO();

    @Test
    void testGetAllCategories() {

        List<Category> categories =
                categoryDAO.getAllCategories();

        assertNotNull(categories);
    }
    
    @Test
    void testGetActiveCategories() {

        List<Category> categories =
                categoryDAO.getActiveCategories();

        assertNotNull(categories);

        for (Category category : categories) {
            assertEquals(
                    "ACTIVE",
                    category.getStatus());
        }
    }
    
    @Test
    void testAddCategory() {

        String categoryName =
                "JUnit Test Category "
                        + System.currentTimeMillis();

        String description =
                "JUnit test description";

        boolean result =
                categoryDAO.addCategory(
                        categoryName,
                        description);

        assertTrue(result);

        // Verify that the category was actually added
        List<Category> categories =
                categoryDAO.searchCategories(
                        categoryName);

        assertFalse(categories.isEmpty());

        Category addedCategory =
                categories.get(0);

        assertEquals(
                categoryName,
                addedCategory.getCategoryName());

        assertEquals(
                description,
                addedCategory.getDescription());

        assertEquals(
                "ACTIVE",
                addedCategory.getStatus());
    }
    
    @Test
    void testSearchCategories() {

        String categoryName =
                "JUnit Search Category "
                        + System.currentTimeMillis();

        boolean added =
                categoryDAO.addCategory(
                        categoryName,
                        "Search test description");

        assertTrue(added);

        List<Category> categories =
                categoryDAO.searchCategories(
                        "JUnit Search Category");

        assertNotNull(categories);
        assertFalse(categories.isEmpty());

        boolean found = false;

        for (Category category : categories) {

            if (category.getCategoryName()
                    .equals(categoryName)) {

                found = true;
                break;
            }
        }

        assertTrue(found);
    }
    
    @Test
    void testUpdateCategory() {

        String originalName =
                "JUnit Update Category "
                        + System.currentTimeMillis();

        String updatedName =
                "JUnit Updated Category "
                        + System.currentTimeMillis();

        // Add category
        boolean added =
                categoryDAO.addCategory(
                        originalName,
                        "Original description");

        assertTrue(added);

        // Find the category
        List<Category> categories =
                categoryDAO.searchCategories(
                        originalName);

        assertFalse(categories.isEmpty());

        Category category =
                categories.get(0);

        int categoryId =
                category.getCategoryId();

        // Update category
        boolean updated =
                categoryDAO.updateCategory(
                        categoryId,
                        updatedName,
                        "Updated description",
                        "INACTIVE");

        assertTrue(updated);

        // Search for updated category
        List<Category> updatedCategories =
                categoryDAO.searchCategories(
                        updatedName);

        assertFalse(updatedCategories.isEmpty());

        Category result =
                updatedCategories.get(0);

        // Verify updated values
        assertEquals(
                updatedName,
                result.getCategoryName());

        assertEquals(
                "Updated description",
                result.getDescription());

        assertEquals(
                "INACTIVE",
                result.getStatus());
    }
    
    @Test
    void testDeactivateCategory() {

        String categoryName =
                "JUnit Deactivate Category "
                        + System.currentTimeMillis();

        // Add category
        boolean added =
                categoryDAO.addCategory(
                        categoryName,
                        "Deactivate test description");

        assertTrue(added);

        // Find category
        List<Category> categories =
                categoryDAO.searchCategories(
                        categoryName);

        assertFalse(categories.isEmpty());

        Category category =
                categories.get(0);

        int categoryId =
                category.getCategoryId();

        // Deactivate category
        boolean deactivated =
                categoryDAO.deactivateCategory(
                        categoryId);

        assertTrue(deactivated);

        // Verify category still exists
        List<Category> deactivatedCategories =
                categoryDAO.searchCategories(
                        categoryName);

        assertFalse(deactivatedCategories.isEmpty());

        Category result =
                deactivatedCategories.get(0);

        // Verify status changed
        assertEquals(
                "INACTIVE",
                result.getStatus());
    }
    
    @Test
    void testCategoryNameExists() {

        String categoryName =
                "JUnit Exists Category "
                        + System.currentTimeMillis();

        // Add category
        boolean added =
                categoryDAO.addCategory(
                        categoryName,
                        "Exists test description");

        assertTrue(added);

        // Find the category
        List<Category> categories =
                categoryDAO.searchCategories(
                        categoryName);

        assertFalse(categories.isEmpty());

        Category category =
                categories.get(0);

        int categoryId =
                category.getCategoryId();

        // Check without excluding any category
        boolean exists =
                categoryDAO.categoryNameExists(
                        categoryName,
                        0);

        assertTrue(exists);

        // Check while excluding the same category
        boolean existsWhenExcluded =
                categoryDAO.categoryNameExists(
                        categoryName,
                        categoryId);

        assertFalse(existsWhenExcluded);

        // Check a category name that does not exist
        boolean doesNotExist =
                categoryDAO.categoryNameExists(
                        "JUnit Definitely Does Not Exist "
                                + System.currentTimeMillis(),
                        0);

        assertFalse(doesNotExist);
    }
}