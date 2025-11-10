package com.mycompany.bookme.table.controllers;

import com.mycompany.bookme.model.TableCreateRequestDTO;
import com.mycompany.bookme.model.TableDTO;
import com.mycompany.bookme.model.TableUpdateRequestDTO;
import com.mycompany.bookme.table.service.TableService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableControllerTest {

    @InjectMocks
    TableController controller;

    @Mock
    TableService service;

    // --- getTables ---
    @Test
    void getTables_shouldReturnOkResponseWithList() {
        Long restaurantId = 1L;
        TableDTO table1 = new TableDTO();
        table1.setId(1L);
        TableDTO table2 = new TableDTO();
        table2.setId(2L);

        when(service.getTables(restaurantId)).thenReturn(List.of(table1, table2));

        ResponseEntity<List<TableDTO>> response = controller.getTables(restaurantId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        verify(service).getTables(restaurantId);
    }

    @Test
    void getTables_shouldReturnEmptyList_whenNoTablesFound() {
        when(service.getTables(1L)).thenReturn(List.of());

        ResponseEntity<List<TableDTO>> response = controller.getTables(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // --- postTable ---
    @Test
    void postTable_shouldReturnCreatedResponse() {
        TableCreateRequestDTO req = new TableCreateRequestDTO();
        TableDTO resultDto = new TableDTO();
        resultDto.setId(10L);

        when(service.postTable(req)).thenReturn(resultDto);

        ResponseEntity<TableDTO> response = controller.postTable(req);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(10L, response.getBody().getId());
        verify(service).postTable(req);
    }

    // --- putTable ---
    @Test
    void putTable_shouldReturnOkResponse() {
        Long tableId = 5L;
        TableUpdateRequestDTO req = new TableUpdateRequestDTO();
        TableDTO updated = new TableDTO();
        updated.setId(tableId);

        when(service.putTable(tableId, req)).thenReturn(updated);

        ResponseEntity<TableDTO> response = controller.putTable(tableId, req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tableId, response.getBody().getId());
        verify(service).putTable(tableId, req);
    }
}
