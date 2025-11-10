package com.mycompany.bookme.table.service;

import com.mycompany.bookme.exceptions.ConflictException;
import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.model.TableCreateRequestDTO;
import com.mycompany.bookme.model.TableDTO;
import com.mycompany.bookme.model.TableUpdateRequestDTO;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.service.RestaurantService;
import com.mycompany.bookme.table.dao.models.entities.TableEntity;
import com.mycompany.bookme.table.dao.repositories.TableRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;
    private final ModelMapper modelMapper;
    private final RestaurantService restaurantService;

    @PreAuthorize("@authz.canAccessRestaurant(#restaurantId, authentication)")
    public List<TableDTO> getTables(Long restaurantId) {
        return tableRepository.findByRestaurant_Id(restaurantId)
                .stream()
                .map(table -> modelMapper.map(table, TableDTO.class))
                .toList();
    }

    @PreAuthorize("@authz.canAccessRestaurant(#req.restaurantId, authentication)")
    public TableDTO postTable(@P("req") TableCreateRequestDTO tableRequestDTO) {
        validateCreateTable(tableRequestDTO);
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantEntityById(tableRequestDTO.getRestaurantId());
        TableEntity tableToBeSaved = modelMapper.map(tableRequestDTO, TableEntity.class);
        tableToBeSaved.setId(null);
        try {
            tableToBeSaved.setRestaurant(restaurantEntity);
            return modelMapper.map(tableRepository.save(tableToBeSaved), TableDTO.class);
        } catch (DataIntegrityViolationException e) {
            handleException(e.getRootCause());
            throw e;
        }
    }

    void handleException(Throwable root) {
        if (root instanceof java.sql.SQLException sqlEx &&
                sqlEx.getMessage().contains("ux_tables_restaurant_table_number")) {
            throw new ConflictException("Ya existe una mesa con ese número en este restaurante.");
        }
    }

    void validateCreateTable(TableCreateRequestDTO tableRequestDTO) {
        validateCapacity(tableRequestDTO.getCapacity());
        validateTableNumber(tableRequestDTO.getTableNumber());
    }

    void validateUpdateTable(TableUpdateRequestDTO tableRequestDTO) {
        validateCapacity(tableRequestDTO.getCapacity());
    }

    void validateCapacity(int capacity) {
        if(capacity < 1) {
            throw new ConflictException("El mínimo de la capacidad de la mesa debe ser 1");
        }
    }

    void validateTableNumber(int tableNumber) {
        if(tableNumber < 1) {
            throw new ConflictException("El número de mesa debe ser positivo");
        }
    }

    @Transactional
    @PreAuthorize("@authz.canAccessTable(#tableId, authentication)")
    public TableDTO putTable(Long tableId, TableUpdateRequestDTO tableRequestDTO) {
        validateUpdateTable(tableRequestDTO);
        TableEntity tableToBeSaved = getTableEntityById(tableId);
        try {
            tableToBeSaved.setCapacity(tableRequestDTO.getCapacity());
            tableToBeSaved.setObservations(tableRequestDTO.getObservations());
            return modelMapper.map(tableToBeSaved, TableDTO.class);
        } catch (DataIntegrityViolationException e) {
            handleException(e.getRootCause());
            throw e;
        }
    }

    public TableEntity getTableEntityById(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table not found: " + tableId));
    }
}
