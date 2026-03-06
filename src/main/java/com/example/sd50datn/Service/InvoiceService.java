package com.example.sd50datn.Service;

import com.example.sd50datn.dto.InvoiceStatsDTO;
import com.example.sd50datn.dto.InvoiceSummaryDTO;

import java.util.List;

public interface InvoiceService {

    InvoiceStatsDTO getStats();

    List<InvoiceSummaryDTO> getInvoiceSummaries();
}

