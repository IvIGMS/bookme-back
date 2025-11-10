package com.mycompany.bookme.table.controllers;

import com.mycompany.bookme.api.TablesApi;
import com.mycompany.bookme.exceptions.utils.ControllerUtils;
import com.mycompany.bookme.model.TableCreateRequestDTO;
import com.mycompany.bookme.model.TableDTO;
import com.mycompany.bookme.model.TableUpdateRequestDTO;
import com.mycompany.bookme.table.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class TableController extends ControllerUtils implements TablesApi {
    private final TableService tableService;

    @Override
    public ResponseEntity<List<TableDTO>> getTables(Long restaurantId) {
        return ResponseEntity.ok(tableService.getTables(restaurantId));
    }

    @Override
    public ResponseEntity<TableDTO> postTable(TableCreateRequestDTO tableRequestDTO) {
        return ResponseEntity.created(null).body(tableService.postTable(tableRequestDTO));
    }

    @Override
    public ResponseEntity<TableDTO> putTable(Long tableId, TableUpdateRequestDTO tableRequestDTO) {
        return ResponseEntity.ok(tableService.putTable(tableId, tableRequestDTO));
    }
}