package com.ivanfrias.generic_project.exceptions.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
  public static Pageable createPageable(Integer numPage, Integer sizePage, String orderBy) {
    int page =
        (numPage != null && numPage >= 0)
            ? numPage - 1
            : 0; // El - 1 es para que empiece en 1, no en 0
    int size = (sizePage != null && sizePage > 0) ? sizePage : 10;

    Sort sort = Sort.unsorted();

    if (orderBy != null && !orderBy.isBlank()) {
      if (orderBy.startsWith("-")) {
        String field = orderBy.substring(1);
        sort = Sort.by(Sort.Direction.DESC, field);
      } else {
        sort = Sort.by(Sort.Direction.ASC, orderBy);
      }
    }

    return PageRequest.of(page, size, sort);
  }
}
