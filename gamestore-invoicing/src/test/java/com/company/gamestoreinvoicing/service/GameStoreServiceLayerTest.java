package com.company.gamestoreinvoicing.service;

import com.company.gamestoreinvoicing.model.*;
import com.company.gamestoreinvoicing.repository.InvoiceRepository;
import com.company.gamestoreinvoicing.repository.ProcessingFeeRepository;
import com.company.gamestoreinvoicing.repository.TaxRepository;
import com.company.gamestoreinvoicing.util.feign.GameStoreCatalog;
import com.company.gamestoreinvoicing.viewModel.ConsoleViewModel;
import com.company.gamestoreinvoicing.viewModel.GameViewModel;
import com.company.gamestoreinvoicing.viewModel.InvoiceViewModel;
import com.company.gamestoreinvoicing.viewModel.TShirtViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.Mockito.doReturn;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
public class GameStoreServiceLayerTest {

    InvoiceRepository invoiceRepository;
    ProcessingFeeRepository processingFeeRepository;
    TaxRepository taxRepository;
    GameStoreCatalog serviceClient;
    GameStoreServiceLayer service;

    @Before
    public void setUp() throws Exception {
        setUpInvoiceRepositoryMock();
        setUpProcessingFeeRepositoryMock();
        setUpTaxRepositoryMock();
        setUpFeignCatalogMock();

        service = new GameStoreServiceLayer(invoiceRepository, taxRepository,
                processingFeeRepository, serviceClient);
    }


    @Test
    public void shouldCreateFindInvoice() {
        InvoiceViewModel invoiceViewModel = new InvoiceViewModel();
        invoiceViewModel.setName("John Jake");
        invoiceViewModel.setStreet("street");
        invoiceViewModel.setCity("Charlotte");
        invoiceViewModel.setState("NC");
        invoiceViewModel.setZipcode("83749");
        invoiceViewModel.setItemType("T-Shirt");
        invoiceViewModel.setItemId(54);
        invoiceViewModel.setQuantity(2);

        invoiceViewModel = service.createInvoice(invoiceViewModel);

        InvoiceViewModel ivmfromService = service.getInvoice(invoiceViewModel.getId());

        assertEquals(invoiceViewModel, ivmfromService);
    }

    @Test
    public void shouldFindAllInvoices(){
        InvoiceViewModel savedInvoice1 = new InvoiceViewModel();
        savedInvoice1.setName("Sandy Beach");
        savedInvoice1.setStreet("123 Main St");
        savedInvoice1.setCity("any City");
        savedInvoice1.setState("NY");
        savedInvoice1.setZipcode("10016");
        savedInvoice1.setItemType("T-Shirt");
        savedInvoice1.setItemId(12);
        savedInvoice1.setUnitPrice(new BigDecimal("12.50"));
        savedInvoice1.setQuantity(2);
        savedInvoice1.setSubtotal(savedInvoice1.getUnitPrice().multiply(new BigDecimal(savedInvoice1.getQuantity())));
        savedInvoice1.setTax(savedInvoice1.getSubtotal().multiply(new BigDecimal("0.06")));
        savedInvoice1.setProcessingFee(new BigDecimal("10.00"));
        savedInvoice1.setTotal(savedInvoice1.getSubtotal().add(savedInvoice1.getTax()).add(savedInvoice1.getProcessingFee()));
        savedInvoice1.setId(22);

        InvoiceViewModel savedInvoice2 = new InvoiceViewModel();
        savedInvoice2.setName("Johnny Dangerously");
        savedInvoice2.setStreet("Once Blvd");
        savedInvoice2.setCity("New York ");
        savedInvoice2.setState("NY");
        savedInvoice2.setZipcode("08234");
        savedInvoice2.setItemType("Console");
        savedInvoice2.setItemId(120);
        savedInvoice2.setUnitPrice(new BigDecimal("129.50"));
        savedInvoice2.setQuantity(1);
        savedInvoice2.setSubtotal(savedInvoice2.getUnitPrice().multiply(new BigDecimal(savedInvoice2.getQuantity())));
        savedInvoice2.setTax(savedInvoice2.getSubtotal().multiply(new BigDecimal("0.08")));
        savedInvoice2.setProcessingFee(new BigDecimal("10.00"));
        savedInvoice2.setTotal(savedInvoice2.getSubtotal().add(savedInvoice2.getTax()).add(savedInvoice2.getProcessingFee()));
        savedInvoice2.setId(12);

        InvoiceViewModel savedInvoice3 = new InvoiceViewModel();
        savedInvoice3.setName("Sandy Beach");
        savedInvoice3.setStreet("123 Broad St");
        savedInvoice3.setCity("any where");
        savedInvoice3.setState("CA");
        savedInvoice3.setZipcode("90016");
        savedInvoice3.setItemType("Game");
        savedInvoice3.setItemId(19);
        savedInvoice3.setUnitPrice(new BigDecimal("12.50"));
        savedInvoice3.setQuantity(4);
        savedInvoice3.setSubtotal(savedInvoice3.getUnitPrice().multiply(new BigDecimal(savedInvoice3.getQuantity())));
        savedInvoice3.setTax(savedInvoice3.getSubtotal().multiply(new BigDecimal("0.09")));
        savedInvoice3.setProcessingFee(BigDecimal.ZERO);
        savedInvoice3.setTotal(savedInvoice3.getSubtotal().add(savedInvoice3.getTax()).add(savedInvoice3.getProcessingFee()));
        savedInvoice3.setId(73);

        List<InvoiceViewModel> currInvoices = new ArrayList<>();
        currInvoices.add(savedInvoice1);
        currInvoices.add(savedInvoice2);
        currInvoices.add(savedInvoice3);

        List<InvoiceViewModel> foundAllInvoices = service.getAllInvoices();

        assertEquals(currInvoices.size(), foundAllInvoices.size());
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldFailCreateFindInvoiceWithBadState() {
        TShirtViewModel tShirt = new TShirtViewModel();
        tShirt.setId(99);
        tShirt.setSize("Small");
        tShirt.setColor("Red");
        tShirt.setDescription("sleeveless");
        tShirt.setPrice(new BigDecimal("400"));
        tShirt.setQuantity(30);

        InvoiceViewModel invoiceViewModel = new InvoiceViewModel();
        invoiceViewModel.setName("John Jake");
        invoiceViewModel.setStreet("street");
        invoiceViewModel.setCity("Charlotte");
        invoiceViewModel.setState("NY");
        invoiceViewModel.setZipcode("83749");
        invoiceViewModel.setItemType("T-Shirt");
        invoiceViewModel.setItemId(99);
        invoiceViewModel.setQuantity(2);

        invoiceViewModel = service.createInvoice(invoiceViewModel);

        InvoiceViewModel ivmfromService = service.getInvoice(invoiceViewModel.getId());

        assertEquals(invoiceViewModel, ivmfromService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailCreateFindInvoiceWithBadItemType() {
        InvoiceViewModel invoiceViewModel = new InvoiceViewModel();
        invoiceViewModel.setName("John Jake");
        invoiceViewModel.setStreet("street");
        invoiceViewModel.setCity("Charlotte");
        invoiceViewModel.setState("NC");
        invoiceViewModel.setZipcode("83749");
        invoiceViewModel.setItemType("Bad Item Type");
        invoiceViewModel.setItemId(54);
        invoiceViewModel.setQuantity(2);

        invoiceViewModel = service.createInvoice(invoiceViewModel);

        InvoiceViewModel ivmfromService = service.getInvoice(invoiceViewModel.getId());

        assertEquals(invoiceViewModel, ivmfromService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailCreateFindInvoiceWithNoInventory() {
        InvoiceViewModel invoiceViewModel = new InvoiceViewModel();
        invoiceViewModel.setName("John Jake");
        invoiceViewModel.setStreet("street");
        invoiceViewModel.setCity("Charlotte");
        invoiceViewModel.setState("NC");
        invoiceViewModel.setZipcode("83749");
        invoiceViewModel.setItemType("T-Shirt");
        invoiceViewModel.setItemId(54);
        invoiceViewModel.setQuantity(6);

        invoiceViewModel = service.createInvoice(invoiceViewModel);

        InvoiceViewModel ivmfromService = service.getInvoice(invoiceViewModel.getId());

        assertEquals(invoiceViewModel, ivmfromService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenCreateInvoiceInvalidItem() {
        InvoiceViewModel invoiceViewModel = new InvoiceViewModel();
        invoiceViewModel.setName("John Jake");
        invoiceViewModel.setStreet("street");
        invoiceViewModel.setCity("Charlotte");
        invoiceViewModel.setState("NC");
        invoiceViewModel.setZipcode("83749");
        invoiceViewModel.setItemType("nothing");
        invoiceViewModel.setItemId(54);
        invoiceViewModel.setQuantity(2);

        invoiceViewModel = service.createInvoice(invoiceViewModel);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenCreateInvoiceInvalidQuantity() {
        InvoiceViewModel invoiceViewModel = new InvoiceViewModel();
        invoiceViewModel.setName("John Jake");
        invoiceViewModel.setStreet("street");
        invoiceViewModel.setCity("Charlotte");
        invoiceViewModel.setState("NC");
        invoiceViewModel.setZipcode("83749");
        invoiceViewModel.setItemType("T-Shirt");
        invoiceViewModel.setItemId(54);
        invoiceViewModel.setQuantity(0);

        invoiceViewModel = service.createInvoice(invoiceViewModel);
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailWhenCreateInvoiceInvalidInvoiceMV() {
        InvoiceViewModel invoiceViewModel = null;

        invoiceViewModel = service.createInvoice(invoiceViewModel);
    }

    private void setUpInvoiceRepositoryMock() {
        invoiceRepository = mock(InvoiceRepository.class);

        Invoice invoice = new Invoice();
        invoice.setName("John Jake");
        invoice.setStreet("street");
        invoice.setCity("Charlotte");
        invoice.setState("NC");
        invoice.setZipcode("83749");
        invoice.setItemType("T-Shirt");
        invoice.setItemId(54);
        invoice.setUnitPrice(new BigDecimal("19.99"));
        invoice.setQuantity(2);
        invoice.setSubtotal(new BigDecimal("39.98"));
        invoice.setTax(new BigDecimal("2"));
        invoice.setProcessingFee(new BigDecimal("1.98"));
        invoice.setTotal(new BigDecimal("43.96"));

        Invoice invoice1 = new Invoice();
        invoice1.setId(20);
        invoice1.setName("John Jake");
        invoice1.setStreet("street");
        invoice1.setCity("Charlotte");
        invoice1.setState("NC");
        invoice1.setZipcode("83749");
        invoice1.setItemType("T-Shirt");
        invoice1.setItemId(54);
        invoice1.setUnitPrice(new BigDecimal("19.99"));
        invoice1.setQuantity(2);
        invoice1.setSubtotal(new BigDecimal("39.98"));
        invoice1.setTax(new BigDecimal("2"));
        invoice1.setProcessingFee(new BigDecimal("1.98"));
        invoice1.setTotal(new BigDecimal("43.96"));

        doReturn(invoice1).when(invoiceRepository).save(invoice);
        doReturn(Optional.of(invoice1)).when(invoiceRepository).findById(20L);


        Invoice savedInvoice1 = new Invoice();
        savedInvoice1.setName("Sandy Beach");
        savedInvoice1.setStreet("123 Main St");
        savedInvoice1.setCity("any City");
        savedInvoice1.setState("NY");
        savedInvoice1.setZipcode("10016");
        savedInvoice1.setItemType("T-Shirt");
        savedInvoice1.setItemId(12);
        savedInvoice1.setUnitPrice(new BigDecimal("12.50"));
        savedInvoice1.setQuantity(2);
        savedInvoice1.setSubtotal(savedInvoice1.getUnitPrice().multiply(new BigDecimal(savedInvoice1.getQuantity())));
        savedInvoice1.setTax(savedInvoice1.getSubtotal().multiply(new BigDecimal("0.06")));
        savedInvoice1.setProcessingFee(new BigDecimal("10.00"));
        savedInvoice1.setTotal(savedInvoice1.getSubtotal().add(savedInvoice1.getTax()).add(savedInvoice1.getProcessingFee()));
        savedInvoice1.setId(22);

        Invoice savedInvoice2 = new Invoice();
        savedInvoice2.setName("Johnny Dangerously");
        savedInvoice2.setStreet("Once Blvd");
        savedInvoice2.setCity("New York ");
        savedInvoice2.setState("NY");
        savedInvoice2.setZipcode("08234");
        savedInvoice2.setItemType("Console");
        savedInvoice2.setItemId(120);
        savedInvoice2.setUnitPrice(new BigDecimal("129.50"));
        savedInvoice2.setQuantity(1);
        savedInvoice2.setSubtotal(savedInvoice2.getUnitPrice().multiply(new BigDecimal(savedInvoice2.getQuantity())));
        savedInvoice2.setTax(savedInvoice2.getSubtotal().multiply(new BigDecimal("0.08")));
        savedInvoice2.setProcessingFee(new BigDecimal("10.00"));
        savedInvoice2.setTotal(savedInvoice2.getSubtotal().add(savedInvoice2.getTax()).add(savedInvoice2.getProcessingFee()));
        savedInvoice2.setId(12);

        Invoice savedInvoice3 = new Invoice();
        savedInvoice3.setName("Sandy Beach");
        savedInvoice3.setStreet("123 Broad St");
        savedInvoice3.setCity("any where");
        savedInvoice3.setState("CA");
        savedInvoice3.setZipcode("90016");
        savedInvoice3.setItemType("Game");
        savedInvoice3.setItemId(19);
        savedInvoice3.setUnitPrice(new BigDecimal("12.50"));
        savedInvoice3.setQuantity(4);
        savedInvoice3.setSubtotal(savedInvoice3.getUnitPrice().multiply(new BigDecimal(savedInvoice3.getQuantity())));
        savedInvoice3.setTax(savedInvoice3.getSubtotal().multiply(new BigDecimal("0.09")));
        savedInvoice3.setProcessingFee(BigDecimal.ZERO);
        savedInvoice3.setTotal(savedInvoice3.getSubtotal().add(savedInvoice3.getTax()).add(savedInvoice3.getProcessingFee()));
        savedInvoice3.setId(73);

        List<Invoice> allList = new ArrayList<>();
        allList.add(savedInvoice1);
        allList.add(savedInvoice2);
        allList.add(savedInvoice3);

        doReturn(allList).when(invoiceRepository).findAll();
    }

    private void setUpProcessingFeeRepositoryMock() {

        processingFeeRepository = mock(ProcessingFeeRepository.class);

        ProcessingFee processingFee = new ProcessingFee();
        processingFee.setFee(new BigDecimal("1.98"));
        processingFee.setProductType("T-Shirt");

        doReturn(Optional.of(processingFee)).when(processingFeeRepository).findById("T-Shirt");

    }

    private void setUpTaxRepositoryMock() {
        taxRepository = mock(TaxRepository.class);

        Tax taxNC = new Tax();
        taxNC.setRate(new BigDecimal(".05"));
        taxNC.setState("NC");

        Tax taxNY = new Tax();
        taxNY.setRate(BigDecimal.ZERO);
        taxNY.setState("NY");

        doReturn(Optional.of(taxNC)).when(taxRepository).findById("NC");
        doReturn(Optional.of(taxNY)).when(taxRepository).findById("NY");

    }

    private void setUpFeignCatalogMock() {
        serviceClient = mock(GameStoreCatalog.class);

        ConsoleViewModel savedConsole1 = new ConsoleViewModel();
        savedConsole1.setId(40);
        savedConsole1.setModel("Playstation");
        savedConsole1.setManufacturer("Sony");
        savedConsole1.setMemoryAmount("120gb");
        savedConsole1.setProcessor("Intel I7-9750H");
        savedConsole1.setPrice(new BigDecimal("299.99"));
        savedConsole1.setQuantity(4);

        GameViewModel savedGame1 = new GameViewModel();
        savedGame1.setId(32);
        savedGame1.setTitle("Halo");
        savedGame1.setEsrbRating("E10+");
        savedGame1.setDescription("Puzzles and Math");
        savedGame1.setPrice(new BigDecimal("23.99"));
        savedGame1.setStudio("Xbox Game Studios");
        savedGame1.setQuantity(5);

        TShirtViewModel savedTShirt1 = new TShirtViewModel();
        savedTShirt1.setId(54);
        savedTShirt1.setSize("Medium");
        savedTShirt1.setColor("Blue");
        savedTShirt1.setDescription("V-Neck");
        savedTShirt1.setPrice(new BigDecimal("19.99"));
        savedTShirt1.setQuantity(5);

        doReturn(savedConsole1).when(serviceClient).getConsole(40);
        doReturn(savedGame1).when(serviceClient).getGame(32);
        doReturn(savedTShirt1).when(serviceClient).getTShirt(54);
    }
}