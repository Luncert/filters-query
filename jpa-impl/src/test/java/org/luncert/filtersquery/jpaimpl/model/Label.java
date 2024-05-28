package org.luncert.filtersquery.jpaimpl.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Label.ID.class)
public class Label implements Serializable {

  @Id
  private long extId;

  @Id
  private String label;

  @Builder
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ID implements Serializable {

    private Long extId;
    private String label;
  }
}
