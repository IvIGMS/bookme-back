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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TableServiceTest {

    @InjectMocks
    private TableService tableService;

    @Mock
    private TableRepository tableRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- validateCapacity ---
    @Test
    void validateCapacity_shouldThrow_whenLessThanOne() {
        ConflictException e = assertThrows(ConflictException.class,
                () -> tableService.validateCapacity(0));
        assertEquals("El mínimo de la capacidad de la mesa debe ser 1", e.getMessage());
    }

    @Test
    void validateCapacity_shouldPass_whenValid() {
        assertDoesNotThrow(() -> tableService.validateCapacity(4));
    }

    // --- validateTableNumber ---
    @Test
    void validateTableNumber_shouldThrow_whenLessThanOne() {
        ConflictException e = assertThrows(ConflictException.class,
                () -> tableService.validateTableNumber(0));
        assertEquals("El número de mesa debe ser positivo", e.getMessage());
    }

    @Test
    void validateTableNumber_shouldPass_whenValid() {
        assertDoesNotThrow(() -> tableService.validateTableNumber(2));
    }

    // --- handleException ---
    @Test
    void handleException_shouldThrowConflict_whenDuplicateConstraint() {
        SQLException sqlException = new SQLException("ux_tables_restaurant_table_number");
        ConflictException e = assertThrows(ConflictException.class,
                () -> tableService.handleException(sqlException));
        assertEquals("Ya existe una mesa con ese número en este restaurante.", e.getMessage());
    }

    @Test
    void handleException_shouldDoNothing_whenOtherSqlException() {
        SQLException sqlException = new SQLException("other_constraint");
        assertDoesNotThrow(() -> tableService.handleException(sqlException));
    }

    // --- getTableEntityById ---
    @Test
    void getTableEntityById_shouldReturn_whenExists() {
        TableEntity tableEntity = TableEntity.builder().id(1L).build();
        when(tableRepository.findById(1L)).thenReturn(Optional.of(tableEntity));

        TableEntity result = tableService.getTableEntityById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTableEntityById_shouldThrow_whenNotFound() {
        when(tableRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> tableService.getTableEntityById(99L));
        assertTrue(e.getMessage().contains("Table not found"));
    }

    // --- postTable ---
    @Test
    void postTable_shouldSaveSuccessfully() {
        // given
        TableCreateRequestDTO dto = TableCreateRequestDTO.builder()
                .restaurantId(10L)
                .tableNumber(1)
                .capacity(4)
                .observations("Ventana")
                .build();

        RestaurantEntity restaurant = RestaurantEntity.builder().id(10L).build();
        TableEntity entityToSave = TableEntity.builder()
                .tableNumber(1)
                .capacity(4)
                .observations("Ventana")
                .restaurant(restaurant)
                .build();

        TableEntity savedEntity = TableEntity.builder()
                .id(5L)
                .tableNumber(1)
                .capacity(4)
                .observations("Ventana")
                .restaurant(restaurant)
                .build();

        TableDTO mapped = new TableDTO();
        mapped.setId(5L);

        when(restaurantService.getRestaurantEntityById(10L)).thenReturn(restaurant);
        when(modelMapper.map(dto, TableEntity.class)).thenReturn(entityToSave);
        when(tableRepository.save(any(TableEntity.class))).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, TableDTO.class)).thenReturn(mapped);

        // when
        TableDTO result = tableService.postTable(dto);

        // then
        assertNotNull(result);
        assertEquals(5L, result.getId());
        verify(tableRepository).save(any(TableEntity.class));
    }

    @Test
    void postTable_shouldThrowConflict_whenDuplicateConstraint() {
        // given
        TableCreateRequestDTO dto = TableCreateRequestDTO.builder()
                .restaurantId(1L)
                .tableNumber(1)
                .capacity(4)
                .build();

        RestaurantEntity restaurant = RestaurantEntity.builder().id(1L).build();
        when(restaurantService.getRestaurantEntityById(1L)).thenReturn(restaurant);
        when(modelMapper.map(dto, TableEntity.class))
                .thenReturn(TableEntity.builder().tableNumber(1).capacity(4).restaurant(restaurant).build());

        SQLException sqlEx = new SQLException("ux_tables_restaurant_table_number");
        DataIntegrityViolationException dive = new DataIntegrityViolationException("duplicate", sqlEx);

        when(tableRepository.save(any(TableEntity.class))).thenThrow(dive);

        // then
        ConflictException e = assertThrows(ConflictException.class,
                () -> tableService.postTable(dto));
        assertEquals("Ya existe una mesa con ese número en este restaurante.", e.getMessage());
    }

    // --- validateUpdateTable ---
    @Test
    void validateUpdateTable_shouldThrow_whenCapacityInvalid() {
        TableUpdateRequestDTO dto = TableUpdateRequestDTO.builder()
                .capacity(0)
                .observations("algo")
                .build();

        ConflictException e = assertThrows(ConflictException.class,
                () -> tableService.validateUpdateTable(dto));
        assertEquals("El mínimo de la capacidad de la mesa debe ser 1", e.getMessage());
    }

    @Test
    void validateUpdateTable_shouldPass_whenValid() {
        TableUpdateRequestDTO dto = TableUpdateRequestDTO.builder()
                .capacity(3)
                .observations("zona terraza")
                .build();

        assertDoesNotThrow(() -> tableService.validateUpdateTable(dto));
    }

    // --- putTable ---
    @Test
    void putTable_shouldUpdateSuccessfully() {
        Long tableId = 7L;
        TableUpdateRequestDTO dto = TableUpdateRequestDTO.builder()
                .capacity(6)
                .observations("Ventana")
                .build();

        TableEntity existing = TableEntity.builder()
                .id(tableId)
                .tableNumber(2)
                .capacity(4)
                .observations("Vieja")
                .build();

        when(tableRepository.findById(tableId)).thenReturn(Optional.of(existing));
        when(modelMapper.map(existing, TableDTO.class)).thenReturn(new TableDTO());

        TableDTO result = tableService.putTable(tableId, dto);

        assertNotNull(result);
        assertEquals(6, existing.getCapacity());
        assertEquals("Ventana", existing.getObservations());
        verify(tableRepository, never()).save(any()); // se actualiza dentro del contexto @Transactional
    }

    @Test
    void putTable_shouldThrowConflict_whenConstraintViolation() {
        Long tableId = 7L;
        TableUpdateRequestDTO dto = TableUpdateRequestDTO.builder()
                .capacity(6)
                .observations("Ventana")
                .build();

        TableEntity existing = TableEntity.builder()
                .id(tableId)
                .tableNumber(2)
                .capacity(4)
                .observations("Vieja")
                .build();

        SQLException sqlEx = new SQLException("ux_tables_restaurant_table_number");
        DataIntegrityViolationException dive = new DataIntegrityViolationException("dup", sqlEx);

        when(tableRepository.findById(tableId)).thenReturn(Optional.of(existing));
        doThrow(dive).when(modelMapper).map(existing, TableDTO.class);

        ConflictException e = assertThrows(ConflictException.class,
                () -> tableService.putTable(tableId, dto));

        assertEquals("Ya existe una mesa con ese número en este restaurante.", e.getMessage());
    }

    // --- getTables ---
    @Test
    void getTables_shouldReturnListOfTables() {
        Long restaurantId = 1L;

        TableEntity entity1 = TableEntity.builder().id(1L).tableNumber(1).capacity(4).build();
        TableEntity entity2 = TableEntity.builder().id(2L).tableNumber(2).capacity(2).build();

        TableDTO dto1 = new TableDTO();
        dto1.setId(1L);
        TableDTO dto2 = new TableDTO();
        dto2.setId(2L);

        when(tableRepository.findByRestaurant_Id(restaurantId))
                .thenReturn(List.of(entity1, entity2));
        when(modelMapper.map(entity1, TableDTO.class)).thenReturn(dto1);
        when(modelMapper.map(entity2, TableDTO.class)).thenReturn(dto2);

        List<TableDTO> result = tableService.getTables(restaurantId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(tableRepository).findByRestaurant_Id(restaurantId);
        verify(modelMapper, times(2)).map(any(TableEntity.class), eq(TableDTO.class));
    }

    @Test
    void getTables_shouldReturnEmptyList_whenNoTables() {
        Long restaurantId = 99L;
        when(tableRepository.findByRestaurant_Id(restaurantId)).thenReturn(List.of());

        List<TableDTO> result = tableService.getTables(restaurantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tableRepository).findByRestaurant_Id(restaurantId);
        verify(modelMapper, never()).map(any(), any());
    }

}
