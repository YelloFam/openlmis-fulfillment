/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.fulfillment.service.shipment;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.openlmis.fulfillment.FileColumnBuilder;
import org.openlmis.fulfillment.FileTemplateBuilder;
import org.openlmis.fulfillment.domain.FileColumn;
import org.openlmis.fulfillment.domain.FileTemplate;
import org.springframework.util.ResourceUtils;

public class ShipmentCsvFileParserTest {

  ShipmentCsvFileParser parser = new ShipmentCsvFileParser();

  FileTemplate template;

  @Test(expected = IOException.class)
  public void shouldThrowIoExceptionWhenFileCannotBeRead() throws Exception {
    template = new FileTemplateBuilder().withHeaderInFile(true).build();

    parser.parse(new File("xxxxx"), template);
  }

  @Test()
  public void shouldIgnoreFirstRowIfTemplateHasHeader() throws Exception {
    FileColumn orderCodeColumn = new FileColumnBuilder().withKeyPath("orderCode").build();

    template = new FileTemplateBuilder()
        .withFileColumns(asList(orderCodeColumn))
        .withHeaderInFile(true).build();

    File file = ResourceUtils.getFile(this.getClass()
        .getResource("/shipment_file_with_header.csv"));
    List<Object[]> response = parser.parse(file, template);

    assertNotNull(response);
    assertThat(response.get(0)[0].toString(),
        is("ORDER-00000000-0000-0000-0000-000000000010R"));
  }

  @Test
  public void shouldReadFirstRowIfTemplateHasNoHeader() throws Exception {
    FileColumn orderCodeColumn = new FileColumnBuilder().withKeyPath("orderCode").build();

    template = new FileTemplateBuilder()
        .withFileColumns(asList(orderCodeColumn))
        .withHeaderInFile(false).build();

    File file = ResourceUtils.getFile(this.getClass()
        .getResource("/shipment_file_with_header.csv"));
    List<Object[]> response = parser.parse(file, template);

    assertNotNull(response);
    assertThat(response.get(0)[0].toString(), is("orderId"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfRowIsInconsistent() throws Exception {
    FileColumn orderCodeColumn = new FileColumnBuilder().withKeyPath("orderCode").build();

    template = new FileTemplateBuilder()
        .withFileColumns(asList(orderCodeColumn))
        .withHeaderInFile(true).build();

    File file = ResourceUtils.getFile(this.getClass()
        .getResource("/shipment_file_inconsistent.csv"));
    List<Object[]> response = parser.parse(file, template);

    assertNotNull(response);
  }

}