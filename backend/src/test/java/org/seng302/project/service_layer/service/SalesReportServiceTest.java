package org.seng302.project.service_layer.service;

import org.seng302.project.AbstractInitializer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
public class SalesReportServiceTest extends AbstractInitializer {

    private SalesReportService salesReportService;

    //TODO: tests for getSalesReport method
    //success
    //forbidden
    //406 business not found
    //invalid dates supplied e.g. start date after end date
}
