package com.example.sd50datn.Service;

import com.example.sd50datn.Dto.InvoiceStatsDTO;
import com.example.sd50datn.Dto.InvoiceSummaryDTO;

import java.util.List;

public interface InvoiceService {

    InvoiceStatsDTO getStats();

    List<InvoiceSummaryDTO> getInvoiceSummaries();
}

