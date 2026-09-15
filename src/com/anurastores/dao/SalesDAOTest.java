package com.anurastores.dao;

import java.util.List;

import com.anurastores.model.Sale;

public class SalesDAOTest {

    public static void main(String[] args) {

        SalesDAO salesDAO =
                new SalesDAO();

        List<Sale> sales =
                salesDAO.getAllSales();

        System.out.println(
                "Number of sales: "
                + sales.size());

        for (Sale sale : sales) {

            System.out.println(
                    sale.getSaleId()
                    + " | "
                    + sale.getEmployeeName()
                    + " | "
                    + sale.getTotalAmount()
                    + " | "
                    + sale.getSaleStatus()
                    + " | "
                    + sale.getPaymentMethod()
            );
        }
    }
}