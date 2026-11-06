package com.dah.rs.common;

import org.springframework.data.domain.Page;

/// @param data T
/// @param meta Meta
/// @param <T>
public record PageResponse<T>(
  T data,
  Meta meta
) {

  public static PageResponse<?> from(Page<?> page) {
    return new PageResponse<>(
      page.getContent().stream().toList(),
      new Meta(
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast()
      )
    );
  }

}
