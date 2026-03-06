package com.example.sd50datn.Service.impl;

import com.example.sd50datn.Dto.InvoiceStatsDTO;
import com.example.sd50datn.Dto.InvoiceSummaryDTO;
import com.example.sd50datn.Repository.InvoiceRepository;
import com.example.sd50datn.Service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public InvoiceStatsDTO getStats() {
        return invoiceRepository.fetchInvoiceStats();
    }

    @Override
    public List<InvoiceSummaryDTO> getInvoiceSummaries() {
        return invoiceRepository.fetchInvoiceSummaries();
    }
}

