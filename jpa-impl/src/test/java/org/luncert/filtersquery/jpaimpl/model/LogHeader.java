package org.luncert.filtersquery.jpaimpl.model;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogHeader implements Serializable {

  @Serial
  private static final long serialVersionUID = 3413266763239477699L;

  @Id
  private long id;

  private String externalReference;

  private String severity;

  private String categoryId;

  private String subCategoryId;

  private Boolean bool;

  private long createdAt;

  @OneToOne
  @JoinColumn(name = "extId")
  private Tag tag;

  @OneToMany
  @JoinColumn(
      name = "extId",
      updatable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
  )
  private List<Label> labels;
}
